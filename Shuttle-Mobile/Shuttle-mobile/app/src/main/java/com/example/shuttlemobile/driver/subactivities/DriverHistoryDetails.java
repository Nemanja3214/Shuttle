package com.example.shuttlemobile.driver.subactivities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.shuttlemobile.R;
import com.example.shuttlemobile.common.GenericUserMapFragment;
import com.example.shuttlemobile.common.UserChatActivity;
import com.example.shuttlemobile.common.adapter.EasyListAdapter;
import com.example.shuttlemobile.message.Message;
import com.example.shuttlemobile.review.IReviewService;
import com.example.shuttlemobile.review.ReviewPairDTO;
import com.example.shuttlemobile.ride.dto.RideDTO;
import com.example.shuttlemobile.route.LocationDTO;
import com.example.shuttlemobile.user.dto.UserEmailDTO;
import com.mapbox.geojson.Point;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class PassengerWithReviews {
    public UserEmailDTO user;
    public ReviewPairDTO reviews = null;

    public PassengerWithReviews(UserEmailDTO user, ReviewPairDTO reviews) {
        this.user = user;
        this.reviews = reviews;
    }
}

public class DriverHistoryDetails extends GenericUserMapFragment {
    private RideDTO ride;
    private Point A, B;
    private List<ReviewPairDTO> reviews = new ArrayList<>();
    private List<PassengerWithReviews> passengers = new ArrayList<>();
    ListView liPassengers;

    public void setRide(RideDTO dto) {
        this.ride = dto;
    }

    public static DriverHistoryDetails newInstance(RideDTO ride) {
        DriverHistoryDetails dhd = new DriverHistoryDetails();
        dhd.setRide(ride);
        dhd.passengers = ride.getPassengers().stream().map(p -> new PassengerWithReviews(p, null)).collect(Collectors.toList());
        return dhd;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getLayoutID(), container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final LocationDTO A_loc = ride.getLocations().get(0).getDeparture();
        final LocationDTO B_loc = ride.getLocations().get(ride.getLocations().size() - 1).getDestination();
        A = Point.fromLngLat(A_loc.getLongitude(), A_loc.getLatitude());
        B = Point.fromLngLat(B_loc.getLongitude(), B_loc.getLatitude());

        drawRoute(A, B, "#FF0000");
        lookAtPoint(A, 10, 1000);

        initViewElements(view);
        fetchReviews(); // After initViewElements.
    }


    @Override
    public String getPublicMapApiToken() {
        return getActivity().getResources().getString(R.string.mapbox_access_token);
    }

    @Override
    public void onMapLoaded() {
    }

    @Override
    public int getLayoutID() {
        return R.layout.fragment_driver_history_details;
    }


    @Override
    public int getMapViewID() {
        return R.id.map_driver_history_details;
    }

    @Override
    public void onNewLocation(Location location) {

    }

    private void fetchReviews() {
        IReviewService.service.findByRide(ride.getId()).enqueue(new Callback<List<ReviewPairDTO>>() {
            @Override
            public void onResponse(Call<List<ReviewPairDTO>> call, Response<List<ReviewPairDTO>> response) {
                if (response.code() == 200) {
                    onFetchReviews(response.body());
                } else {
                    Log.e("DriverHistoryDetails", response.toString());
                }
            }

            @Override
            public void onFailure(Call<List<ReviewPairDTO>> call, Throwable t) {
                Log.e("DriverHistoryDetails", t.toString());
            }
        });
    }

    private void onFetchReviews(List<ReviewPairDTO> reviews) {
        this.reviews = reviews;

        this.passengers.clear();
        for (UserEmailDTO u : ride.getPassengers()) {
            // Try to find the user in reviews.
            boolean foundReviews = false;
            for (ReviewPairDTO r : this.reviews) {
                if (r.getDriverReview() != null && r.getDriverReview().getPassenger().getId().equals(u.getId())) {
                    this.passengers.add(new PassengerWithReviews(u, r));
                    foundReviews = true;
                    break;
                }
                if (r.getVehicleReview() != null && r.getVehicleReview().getPassenger().getId().equals(u.getId())) {
                    this.passengers.add(new PassengerWithReviews(u, r));
                    foundReviews = true;
                    break;
                }
            }

            if (!foundReviews) {
                this.passengers.add(new PassengerWithReviews(u, null));
            }
        }

        ((EasyListAdapter)(liPassengers.getAdapter())).notifyDataSetChanged();
    }

    private void initViewElements(View view) {
        TextView txtStart = view.findViewById(R.id.txt_dhd_start);
        TextView txtEnd = view.findViewById(R.id.txt_dhd_end);
        TextView txtDist = view.findViewById(R.id.txt_dhd_dist);
        TextView txtPrice = view.findViewById(R.id.txt_dhd_price);
        TextView txtA = view.findViewById(R.id.txt_dhd_A);
        TextView txtB = view.findViewById(R.id.txt_dhd_B);

        txtA.setText(ride.getLocations().get(0).getDeparture().getAddress());
        txtB.setText(ride.getLocations().get(ride.getLocations().size() - 1).getDestination().getAddress());

        if (ride.getStartTime() == null) {
            txtStart.setText("Ride did not begin.");
        } else {
            txtStart.setText(LocalDateTime.parse(ride.getStartTime()).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        }

        if (ride.getEndTime() == null) {
            txtEnd.setText("Ride did not finish properly.");
        } else {
            txtEnd.setText(LocalDateTime.parse(ride.getEndTime()).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        }

        txtDist.setText(String.format("%.2f km", ride.getTotalLength() / 1000.0));
        txtPrice.setText(ride.getTotalCost() + " RSD");

        initPassengerList(view);
    }

    private void initPassengerList(View view) {
        liPassengers = view.findViewById(R.id.list_dhd_passengers);

        liPassengers.setAdapter(new EasyListAdapter<PassengerWithReviews>() {
            @Override
            public List<PassengerWithReviews> getList() {
                return passengers;
            }

            @Override
            public LayoutInflater getLayoutInflater() {
                return DriverHistoryDetails.this.getLayoutInflater();
            }

            @Override
            public void applyToView(View view, PassengerWithReviews obj) {
                TextView txtName = view.findViewById(R.id.txt_dhdp_name);
                RatingBar ratingDriver = view.findViewById(R.id.rating_dhdp_driver);
                RatingBar ratingVehicle = view.findViewById(R.id.rating_dhdp_vehicle);
                TextView txtCommentDriver = view.findViewById(R.id.txt_dhdp_driver_comment);
                TextView txtCommentVehicle = view.findViewById(R.id.txt_dhdp_vehicle_comment);

                Button btnChat = view.findViewById(R.id.btn_dhdp_inbox);
                btnChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), UserChatActivity.class);

                        intent.putExtra(UserChatActivity.PARAM_OTHER_ID, obj.user.getId());
                        intent.putExtra(UserChatActivity.PARAM_RIDE_ID, ride.getId());
                        intent.putExtra(UserChatActivity.PARAM_MSG_TYPE, Message.Type.RIDE);

                        startActivity(intent);
                    }
                });

                txtName.setText(obj.user.getEmail());

                if (obj.reviews == null) {
                    ratingDriver.setRating(0);
                    ratingVehicle.setRating(0);
                    txtCommentDriver.setText("No review.");
                    txtCommentVehicle.setText("No review.");
                    return;
                }

                if (obj.reviews.getDriverReview() == null) {
                    ratingDriver.setRating(0);
                    txtCommentDriver.setText("No review.");
                } else {
                    ratingDriver.setRating(obj.reviews.getDriverReview().getRating());
                    txtCommentDriver.setText(obj.reviews.getDriverReview().getComment());
                }

                if (obj.reviews.getVehicleReview() == null) {
                    ratingVehicle.setRating(0);
                    txtCommentVehicle.setText("No review.");
                } else {
                    ratingVehicle.setRating(obj.reviews.getVehicleReview().getRating());
                    txtCommentVehicle.setText(obj.reviews.getVehicleReview().getComment());
                }
            }

            @Override
            public int getListItemLayoutId() {
                return R.layout.list_dhd_passenger;
            }
        });
    }
}