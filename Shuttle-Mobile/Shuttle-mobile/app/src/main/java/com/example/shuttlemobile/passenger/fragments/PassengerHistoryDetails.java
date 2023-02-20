package com.example.shuttlemobile.passenger.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shuttlemobile.FavoriteDialog;
import com.example.shuttlemobile.R;
import com.example.shuttlemobile.common.GenericUserMapFragment;
import com.example.shuttlemobile.common.UserChatActivity;
import com.example.shuttlemobile.common.adapter.EasyListAdapter;
import com.example.shuttlemobile.driver.DriverDTO;
import com.example.shuttlemobile.driver.IDriverService;
import com.example.shuttlemobile.driver.fragments.DriverHistory;
import com.example.shuttlemobile.message.Message;
import com.example.shuttlemobile.passenger.orderride.OrderActivity;
import com.example.shuttlemobile.review.IReviewService;
import com.example.shuttlemobile.review.ReviewDTO;
import com.example.shuttlemobile.review.ReviewPairDTO;
import com.example.shuttlemobile.review.ReviewSendDTO;
import com.example.shuttlemobile.ride.dto.RideDTO;
import com.example.shuttlemobile.route.LocationDTO;
import com.example.shuttlemobile.user.dto.UserEmailDTO;
import com.example.shuttlemobile.util.SettingsUtil;
import com.example.shuttlemobile.util.Utils;
import com.example.shuttlemobile.vehicle.IVehicleService;
import com.example.shuttlemobile.vehicle.VehicleDTO;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.geojson.Point;
import com.mapbox.maps.MapView;
import com.mapbox.maps.extension.observable.eventdata.CameraChangedEventData;
import com.mapbox.maps.plugin.delegates.listeners.OnCameraChangeListener;
import com.mapbox.maps.plugin.gestures.GesturesPlugin;
import com.mapbox.maps.plugin.gestures.GesturesPluginImpl;
import com.mapbox.maps.plugin.gestures.GesturesUtils;
import com.mapbox.maps.plugin.gestures.OnMoveListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PassengerHistoryDetails extends GenericUserMapFragment {
    private RideDTO ride;
    private DriverDTO driver;
    private List<ReviewPairDTO> reviews;
    private VehicleDTO vehicle;

    private static final String RIDE_KEY = "ride";

    private TextView txtRouteFrom;
    private TextView txtRouteTo;
    private ImageButton btnFavorite;
    private Button btnOrderAgain;
    private ImageView imgDriver;
    private TextView txtDriverFullName;
    private TextView txtVehicle;
    private Button btnChat;
    private TextView txtDistace;
    private TextView txtPrice;
    private TextView txtDate;
    private TextView txtTime;
    private ListView lvPassengers;
    private View viewRateDriver;
    private View viewRateVehicle;
    private ScrollView viewScroll;
    private MapView mapView;

    public static PassengerHistoryDetails newInstance(RideDTO ride) {
        PassengerHistoryDetails fragment = new PassengerHistoryDetails();
        Bundle args = new Bundle();
        args.putSerializable(RIDE_KEY, ride);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.ride = (RideDTO) getArguments().getSerializable(RIDE_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_passenger_history_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final LocationDTO A_loc = ride.getLocations().get(0).getDeparture();
        final LocationDTO B_loc = ride.getLocations().get(ride.getLocations().size() - 1).getDestination();
        Point a = Point.fromLngLat(A_loc.getLongitude(), A_loc.getLatitude());
        Point b = Point.fromLngLat(B_loc.getLongitude(), B_loc.getLatitude());

        drawRoute(a, b, "#FF0000");
        lookAtPoint(a, 10, 1000);
        initViews(view);
        fillData();
    }

    private void fillData() {
//        basic ride data
        final LocationDTO A_loc = ride.getLocations().get(0).getDeparture();
        final LocationDTO B_loc = ride.getLocations().get(ride.getLocations().size() - 1).getDestination();
        txtRouteFrom.setText(A_loc.getAddress());
        txtRouteTo.setText(B_loc.getAddress());
        if(ride.getTotalLength() != null)
            txtDistace.setText(ride.getTotalLength().toString() + "km");
        if(ride.getTotalCost() != null)
            txtPrice.setText(ride.getTotalCost().toString() + "RSD");

//        Date time data
        final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.YYYY");
        final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String endTimeStr = "";
        String startTimeStr = "";
        String endDateStr = "";
        if(ride.getEndTime() != null) {
            LocalDateTime endTime = LocalDateTime.parse(ride.getEndTime());
            endTimeStr = timeFormatter.format(endTime);
            endDateStr = dateFormatter.format(endTime);
        }
        if(ride.getStartTime() != null){
            LocalDateTime startTime = LocalDateTime.parse(ride.getStartTime());
            startTimeStr = timeFormatter.format(startTime);
        }
        txtDate.setText(endDateStr);
        txtTime.setText(startTimeStr + " - " + endTimeStr);

//        button listeners
        btnOrderAgain.setOnClickListener(view -> {
            Intent intent = new Intent(requireContext(), OrderActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(OrderActivity.KEY_ROUTE, ride.getLocations().get(0));
            bundle.putSerializable(OrderActivity.KEY_DIST, ride.getTotalLength());
            intent.putExtras(bundle);
            startActivity(intent);
        });

        btnFavorite.setOnClickListener(view1 -> {
            DialogFragment favoriteDialog = FavoriteDialog.newInstance(ride);
            favoriteDialog.show(getChildFragmentManager(), "favorite");
        });

//        set on rate review
        Button btnVehicleRating = viewRateVehicle.findViewById(R.id.btn_p_ride_leave_rating);
        btnVehicleRating.setOnClickListener(view -> onRate(viewRateVehicle, IReviewService.service::leaveReviewVehicle));

        Button btnDriverRating = viewRateDriver.findViewById(R.id.btn_p_ride_leave_rating);
        btnDriverRating.setOnClickListener(view -> onRate(viewRateDriver, IReviewService.service::leaveReviewDriver));

//        set passengers
        lvPassengers.setAdapter(new EasyListAdapter<UserEmailDTO>() {
            @Override
            public List<UserEmailDTO> getList() {
                return ride.getPassengers();
            }

            @Override
            public LayoutInflater getLayoutInflater() {
                return PassengerHistoryDetails.this.getLayoutInflater();
            }

            @Override
            public void applyToView(View view, UserEmailDTO passenger) {
                TextView txtPassenger = view.findViewById(R.id.txt_p_history_p_name);
                txtPassenger.setText(passenger.getEmail());

            }

            @Override
            public int getListItemLayoutId() {
                return R.layout.list_p_history_passengers;
            }
        });

//        fetch  reviews
        fetchReviews();
//        driver fetching and filling data
        fetchDriver();
    }

    private void onRate(View viewRate, BiFunction<Long, ReviewSendDTO, Call<ReviewDTO>> func) {
        RatingBar ratingBar = viewRate.findViewById(R.id.rating_p_ride_rating);
        EditText etComment = viewRate.findViewById(R.id.txt_p_ride_comment);
        Long rating = (long) ratingBar.getRating();
        String comment = etComment.getText().toString();

        if(comment.equals("")) {
            Toast.makeText(requireContext(), "Comment field can't be empty", Toast.LENGTH_LONG).show();
            return;
        }

        ReviewSendDTO review = new ReviewSendDTO();
        review.setRating(rating);
        review.setComment(comment);

        Call<ReviewDTO> call = func.apply(ride.getId(), review);
        call.enqueue(new Callback<ReviewDTO>() {
            @Override
            public void onResponse(Call<ReviewDTO> call, Response<ReviewDTO> response) {
                if(response.isSuccessful()){
                    fetchReviews();
                }
            }

            @Override
            public void onFailure(Call<ReviewDTO> call, Throwable t) {

            }
        });
    }

    private void fetchDriver() {
        Call<DriverDTO> call = IDriverService.service.getDriver(ride.getDriver().getId());
        call.enqueue(new Callback<DriverDTO>() {
            @Override
            public void onResponse(Call<DriverDTO> call, Response<DriverDTO> response) {
                if(response.isSuccessful()){
                    driver = response.body();
                    if(driver != null){
                        fillDriverData(driver);
                    }
                }
            }

            @Override
            public void onFailure(Call<DriverDTO> call, Throwable t) {
            }
        });
    }

    private void fetchReviews() {
        Call<List<ReviewPairDTO>> getReviews = IReviewService.service.findByRide(ride.getId());
        getReviews.enqueue(new Callback<List<ReviewPairDTO>>() {
            @Override
            public void onResponse(Call<List<ReviewPairDTO>> call, Response<List<ReviewPairDTO>> response) {
                if(response.isSuccessful()){
                    reviews = response.body();
                    if(reviews != null){
                        handleReviews(reviews);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ReviewPairDTO>> call, Throwable t) {

            }
        });
    }

    private void handleReviews(List<ReviewPairDTO> reviews) {
        Optional<ReviewDTO> vehicleReview = reviews
                .stream()
                .map(ReviewPairDTO::getVehicleReview)
                .filter(review -> Objects.nonNull(review.getPassenger()))
                .filter(review ->
                        Objects.equals(review.getPassenger().getId(), SettingsUtil.getUserJWT().getId())).findFirst();
        vehicleReview.ifPresent(reviewDTO -> fillReview(reviewDTO, "Vehicle rate", viewRateVehicle));
        if(!vehicleReview.isPresent()){
            LocalDateTime endTime = LocalDateTime.parse(ride.getEndTime());
            if(endTime.isBefore(LocalDateTime.now().minusDays(3))){
                viewRateVehicle.setVisibility(View.GONE);
            }
        }
        Optional<ReviewDTO> driverReview = reviews
                .stream()
                .map(ReviewPairDTO::getDriverReview)
                .filter(review -> Objects.nonNull(review.getPassenger()))
                .filter(review ->
                        Objects.equals(review.getPassenger().getId(), SettingsUtil.getUserJWT().getId())).findFirst();
        driverReview.ifPresent(reviewDTO -> fillReview(reviewDTO, "Driver rate", viewRateDriver));
        if(!driverReview.isPresent()){
            LocalDateTime endTime = LocalDateTime.parse(ride.getEndTime());
            if(endTime.isBefore(LocalDateTime.now().minusDays(3))){
                viewRateDriver.setVisibility(View.GONE);
            }
        }
    }

    private void fillReview(ReviewDTO reviewDTO, String title, View viewRate) {
        TextView lblRating = viewRate.findViewById(R.id.lbl_p_rating);
        RatingBar ratingBar = viewRate.findViewById(R.id.rating_p_ride_rating);
        EditText etComment = viewRate.findViewById(R.id.txt_p_ride_comment);
        Button btnRate = viewRate.findViewById(R.id.btn_p_ride_leave_rating);

        lblRating.setText(title);

        ratingBar.setRating(reviewDTO.getRating());
        ratingBar.setIsIndicator(true);

        etComment.setText(reviewDTO.getComment());
        etComment.setEnabled(false);

        btnRate.setVisibility(View.GONE);
    }

    private void fillDriverData(DriverDTO driver) {
        imgDriver.setImageBitmap(Utils.getImageFromBase64(driver.getProfilePicture()));
        txtDriverFullName.setText(driver.getName() + " " + driver.getSurname());
        btnChat.setOnClickListener(view -> {
            openChat(driver.getId());
        });

        Call<VehicleDTO> call = IDriverService.service.getVehicle(driver.getId());
        call.enqueue(new Callback<VehicleDTO>() {
            @Override
            public void onResponse(Call<VehicleDTO> call, Response<VehicleDTO> response) {
                if(response.isSuccessful()){
                    vehicle = response.body();
                    if(vehicle != null){
                        txtVehicle.setText(vehicle.getModel());
                    }
                }
            }
            @Override
            public void onFailure(Call<VehicleDTO> call, Throwable t) {

            }
        });
    }

    private void openChat(Long driverId) {
        Intent intent = new Intent(getActivity(), UserChatActivity.class);

        intent.putExtra(UserChatActivity.PARAM_OTHER_ID, driverId);
        intent.putExtra(UserChatActivity.PARAM_RIDE_ID, ride.getId());
        intent.putExtra(UserChatActivity.PARAM_MSG_TYPE, Message.Type.RIDE);

        startActivity(intent);
    }

    private void initViews(View view) {
        txtRouteFrom = view.findViewById(R.id.txt_p_ride_routeA);
        txtRouteTo = view.findViewById(R.id.txt_p_ride_routeB);
        btnOrderAgain = view.findViewById(R.id.bnt_p_ride_again);
        btnFavorite = view.findViewById(R.id.btn_p_ride_favorite);

        imgDriver = view.findViewById(R.id.img_p_ride_dpfp);
        txtDriverFullName = view.findViewById(R.id.txt_p_ride_dname);
        txtVehicle = view.findViewById(R.id.txt_p_ride_dcar);
        btnChat = view.findViewById(R.id.btn_p_ride_dchat);
        txtDistace = view.findViewById(R.id.txt_p_ride_distance);
        txtPrice = view.findViewById(R.id.txt_p_ride_price);
        txtDate = view.findViewById(R.id.txt_p_ride_date);
        txtTime = view.findViewById(R.id.txt_p_ride_tinterval);
        lvPassengers = view.findViewById(R.id.li_p_ride_passengers);

        viewRateDriver = view.findViewById(R.id.p_history_driver_rating);
        viewRateVehicle = view.findViewById(R.id.p_history_vehicle_rating);

        setLowerLimit(viewRateDriver);
        setLowerLimit(viewRateVehicle);

        viewScroll = view.findViewById(R.id.scroll_p_history_details);
        mapView = view.findViewById(R.id.p_details_map);
    }

    private void setLowerLimit(View viewRate) {
        RatingBar ratingBar = viewRate.findViewById(R.id.rating_p_ride_rating);
        ratingBar.setOnRatingBarChangeListener((ratingBar1, rating, fromUser) -> {
            if(rating < 1){
                ratingBar1.setRating(1);
            }
        });
    }

    @Override
    public String getPublicMapApiToken() {
        return requireActivity().getResources().getString(R.string.mapbox_access_token);
    }

    @Override
    public void onMapLoaded() {
//        disable scrolling while panning
        GesturesPlugin gesturesPlugin = GesturesUtils.getGestures(mapView);
        gesturesPlugin.addOnMoveListener(new OnMoveListener() {
            @Override
            public void onMoveBegin(@NonNull MoveGestureDetector moveGestureDetector) {
                viewScroll.requestDisallowInterceptTouchEvent(true);
            }

            @Override
            public boolean onMove(@NonNull MoveGestureDetector moveGestureDetector) {
                return false;
            }

            @Override
            public void onMoveEnd(@NonNull MoveGestureDetector moveGestureDetector) {
                viewScroll.requestDisallowInterceptTouchEvent(false);
            }
        });
    }

    @Override
    public int getLayoutID() {
        return R.layout.fragment_passenger_history_details;
    }

    @Override
    public int getMapViewID() {
        return R.id.p_details_map;
    }

    @Override
    public void onNewLocation(Location location) {

    }

}