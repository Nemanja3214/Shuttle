package com.example.shuttlemobile.passenger.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;

import com.example.shuttlemobile.BlankFragment;
import com.example.shuttlemobile.R;
import com.example.shuttlemobile.common.GenericUserFragment;
import com.example.shuttlemobile.common.GenericUserMapFragment;
import com.example.shuttlemobile.common.SessionContext;
import com.example.shuttlemobile.driver.services.DriversLocationService;
import com.example.shuttlemobile.passenger.fragments.home.PassengerCurrentRide;
import com.example.shuttlemobile.passenger.fragments.home.PassengerSearchRoute;
import com.example.shuttlemobile.passenger.services.PassengerRideService;
import com.example.shuttlemobile.passenger.subactivities.PassengerRateRidePromptActivity;
import com.example.shuttlemobile.ride.IRideService;
import com.example.shuttlemobile.ride.Ride;
import com.example.shuttlemobile.ride.dto.RideDTO;
import com.example.shuttlemobile.ride.dto.VehicleLocationDTO;
import com.example.shuttlemobile.route.LocationDTO;
import com.example.shuttlemobile.util.NotificationUtil;
import com.mapbox.geojson.Point;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PassengerHome extends GenericUserMapFragment {
    private boolean initiallyMovedToLocation = false;

    private FragmentContainerView fragmentContainerView;
    private BlankFragment blankFragment;
    private PassengerCurrentRide currentRideFragment;
    private PassengerSearchRoute searchRouteFragment;
    private Fragment currentFragment = null;

    private BroadcastReceiver rideReceiver;
    private BroadcastReceiver driversLocationReceiver;

    private RideDTO ride = null;
    private Point A = null;
    private Point B = null;
    private Double distance = 0.0;

    public static PassengerHome newInstance(SessionContext session) {
        PassengerHome fragment = new PassengerHome();
        Bundle bundle = new Bundle();
        bundle.putSerializable(GenericUserFragment.KEY_SESSION, session);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFragments();

        initViewElements(view);
        initRideReceiver();
        initDriversLocationReceiver();

        startDriversLocationService();

        determineSubFragment(null);
    }

    @Override
    public void onNewLocation(Location location) {
        if (!initiallyMovedToLocation) {
            // When the screen opens, move the map to our location (just the first time).
            lookAtPoint(Point.fromLngLat(location.getLongitude(), location.getLatitude()), 15, 4000);
            initiallyMovedToLocation = true;
        }
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
        return R.layout.fragment_passenger_home;
    }

    @Override
    public int getMapViewID() {
        return R.id.map_passenger_home;
    }

    @Override
    public void onPause() {
        super.onPause();

        getActivity().unregisterReceiver(rideReceiver);
        getActivity().unregisterReceiver(driversLocationReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();

        subscribeToRideReceiver();
        subscribeToDriversLocationReceiver();
    }

    @Override
    public void onGetDistance(Double d) {
        super.onGetDistance(distance);
        this.distance = d;
    }

    public Double getDistance() {
        return distance;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    private void startDriversLocationService() {
        Intent intent = new Intent(getActivity(), DriversLocationService.class);
        requireActivity().startService(intent);
    }

    private void initFragments() {
        blankFragment = BlankFragment.newInstance();
        currentRideFragment = PassengerCurrentRide.newInstance();
        searchRouteFragment = PassengerSearchRoute.newInstance();
    }

    private void initViewElements(View view) {
        this.fragmentContainerView = view.findViewById(R.id.passenger_home_fragment_frame_home);
    }

    public void makeRoute(Point A_, Point B_) {
        A = A_;
        B = B_;
        if (A != null && B != null) {
            drawRoute(A, B, "#2369ED");
            fitViewport(A, B, 3000);
        }
    }

    private void determineSubFragment(RideDTO dto) {
        if (dto == null) {
            if (ride != null) {
                // Current active ride is null (there isn't any), but there is a 'ride' which
                // means there's an old ride, to refresh it to get its final state.

                IRideService.service.getById(ride.getId()).enqueue(new Callback<RideDTO>() {
                    @Override
                    public void onResponse(Call<RideDTO> call, Response<RideDTO> response) {
                        Ride.State status = Ride.State.valueOf(response.body().getStatus().toUpperCase());

                        if (status == Ride.State.FINISHED) {
                            NotificationUtil.sendNotification(
                                    getActivity().getApplicationContext(),
                                    NotificationUtil.PASSENGER_NOTIFICATION_CHANNEL_ID,
                                    "Ride finished",
                                    "You can order new rides now.",
                                    R.drawable.car_green,
                                    1110101110);
                            promptToRateRide(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<RideDTO> call, Throwable t) {

                    }
                });

                ride = null;
                removeRoute();
            }
            setSubFragmentIfDifferent(searchRouteFragment);
            return;
        }

        ride = dto;
        Ride.State state = Ride.State.valueOf(dto.getStatus().toUpperCase());

        switch (state) {
            case PENDING: case ACCEPTED: case STARTED:
                setSubFragmentIfDifferent(currentRideFragment);
                break;
            case CANCELED: case REJECTED: case FINISHED:
                // TODO: This is probably never gonna be called, instead use the if(dto==null) from
                //  above.
                setSubFragmentIfDifferent(searchRouteFragment);
                break;
            default:
                throw new IllegalStateException("Unsupported state: " + state);
        }
    }

    private void setSubFragmentIfDifferent(Fragment fragment) {
        if (currentFragment != fragment) {
            setSubFragment(fragment);
        }
    }

    private void setSubFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getChildFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .setCustomAnimations(R.anim.slide_in_top, R.anim.slide_out_bottom)
                .setReorderingAllowed(true)
                .replace(R.id.passenger_home_fragment_frame_home, fragment);
        fragmentTransaction.addToBackStack("PassengerHome");
        fragmentTransaction.commit();
        currentFragment = fragment;
    }

    private void onGetRide(RideDTO dto) {
        determineSubFragment(dto);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    private void initDriversLocationReceiver() {
        Handler handler = new Handler();
        driversLocationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                List<VehicleLocationDTO> locations = (List<VehicleLocationDTO>) intent.getSerializableExtra(DriversLocationService.NEW_VEHICLES_LOCATIONS);
                deleteAllPoints();
                for (VehicleLocationDTO dto : locations) {
                    LocationDTO location = dto.getLocation();
                    Point driverLocation = Point.fromLngLat(location.getLongitude(), location.getLatitude());
                    handler.post(() -> drawCar(driverLocation, dto.getAvailable()));
                }
            }
        };

        subscribeToDriversLocationReceiver();
    }

    private void initRideReceiver() {
        rideReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                RideDTO rideDTO = (RideDTO)intent.getSerializableExtra(PassengerRideService.INTENT_RIDE_KEY);
                onGetRide(rideDTO);
            }
        };

        subscribeToRideReceiver();
    }

    private void subscribeToRideReceiver() {
        if (rideReceiver == null) {
            return;
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PassengerRideService.BROADCAST_CHANNEL);
        getActivity().registerReceiver(rideReceiver, intentFilter);
    }

    private void subscribeToDriversLocationReceiver() {
        if (driversLocationReceiver == null) {
            return;
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DriversLocationService.RESULT);
        getActivity().registerReceiver(driversLocationReceiver, intentFilter);
    }

    private void promptToRateRide(RideDTO dto) {
        Toast.makeText(getActivity().getApplicationContext(), "Rate the ride.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), PassengerRateRidePromptActivity.class);
        intent.putExtra(PassengerRateRidePromptActivity.KEY_RIDE, dto);
        startActivity(intent);
    }

}