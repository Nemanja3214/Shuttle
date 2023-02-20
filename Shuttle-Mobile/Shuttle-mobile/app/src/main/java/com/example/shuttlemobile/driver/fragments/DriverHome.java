package com.example.shuttlemobile.driver.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.shuttlemobile.BlankFragment;
import com.example.shuttlemobile.R;
import com.example.shuttlemobile.common.GenericUserFragment;
import com.example.shuttlemobile.common.GenericUserMapFragment;
import com.example.shuttlemobile.common.SessionContext;
import com.example.shuttlemobile.driver.fragments.home.DriverCurrentRide;
import com.example.shuttlemobile.driver.fragments.home.DriverHomeAcceptanceRide;
import com.example.shuttlemobile.driver.services.DriverRideService;
import com.example.shuttlemobile.driver.services.DriversLocationService;
import com.example.shuttlemobile.ride.Ride;
import com.example.shuttlemobile.ride.dto.RideDTO;
import com.example.shuttlemobile.ride.dto.VehicleLocationDTO;
import com.example.shuttlemobile.route.LocationDTO;
import com.example.shuttlemobile.user.IUserService;
import com.example.shuttlemobile.util.SettingsUtil;
import com.example.shuttlemobile.util.ShakePack;
import com.mapbox.geojson.Point;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverHome extends GenericUserMapFragment implements SensorEventListener {
    private boolean initiallyMovedToLocation = false;
    private FragmentContainerView fragmentContainerView;
    private BroadcastReceiver rideReceiver;
    private BroadcastReceiver isActiveReceiver;
    private BroadcastReceiver driversLocationReceiver;
    private Switch activeSwitch;
    private TextView speed;
    private ShakePack shakePack = new ShakePack(12);
    private SensorManager sensorManager;

    private BlankFragment blankFragment;
    private DriverCurrentRide currentRideFragment;
    private DriverHomeAcceptanceRide fragmentAcceptance;

    private Fragment currentFragment = null;
    private RideDTO lastDto = null;

    public static DriverHome newInstance(SessionContext session) {
        DriverHome fragment = new DriverHome();
        Bundle bundle = new Bundle();
        bundle.putSerializable(GenericUserFragment.KEY_SESSION, session);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewElements(view);
        initFragments();
        initSensorManager();

        initRideReceiver();
        initIsActiveReceiver();
        initDriversLocationReceiver();

        startDriversLocationService();

        determineSubFragment(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getLayoutID(), container, false);
    }

    @Override
    public String getPublicMapApiToken() {
        return getActivity().getResources().getString(R.string.mapbox_access_token);
    }

    @Override
    public void onMapLoaded() {
        lookAtPoint(Point.fromLngLat(19.833549,45.267136), 15, 4000);
    }

    @Override
    public int getLayoutID() {
        return R.layout.fragment_driver_home;
    }

    @Override
    public void onNewLocation(Location location) {
        if (!initiallyMovedToLocation) {
            // When the screen opens, move the map to our location (just the first time).
            lookAtPoint(Point.fromLngLat(location.getLongitude(), location.getLatitude()), 15, 4000);
            initiallyMovedToLocation = true;
        }
    }

    private void startDriversLocationService() {
        Intent intent = new Intent(getActivity(), DriversLocationService.class);
        requireActivity().startService(intent);
    }

    @Override
    public int getMapViewID() {
        return R.id.map_driver_home;
    }

    private void initViewElements(@NonNull View view) {
        fragmentContainerView = view.findViewById(R.id.driver_home_fragment_frame_home);
        activeSwitch = view.findViewById(R.id.switch_is_active);
        speed = view.findViewById(R.id.txt_driver_speed);
        initSwitchToggle();
    }

    @Override
    public void onPause() {
        super.onPause();

        getActivity().unregisterReceiver(rideReceiver);
        getActivity().unregisterReceiver(isActiveReceiver);
        getActivity().unregisterReceiver(driversLocationReceiver);

        sensorManager.unregisterListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        subscribeToRideReceiver();
        subscribeToIsActiveReceiver();
        subscribeToDriversLocationReceiver();

        sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI
        );
    }

    private void initFragments() {
        blankFragment = BlankFragment.newInstance();
        fragmentAcceptance = DriverHomeAcceptanceRide.newInstance();
        currentRideFragment = DriverCurrentRide.newInstance();
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
                .replace(R.id.driver_home_fragment_frame_home, fragment);
        fragmentTransaction.addToBackStack("DriverHome");
        fragmentTransaction.commit();
        currentFragment = fragment;
    }

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
                RideDTO rideDTO = (RideDTO)intent.getSerializableExtra(DriverRideService.INTENT_RIDE_KEY);
                onGetRide(rideDTO);
            }
        };

        subscribeToRideReceiver();
    }

    private void initIsActiveReceiver() {
        isActiveReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Boolean isActive = intent.getBooleanExtra(DriverRideService.INTENT_IS_ACTIVE_KEY, false);
                onGetIsActive(isActive);
            }
        };

        subscribeToIsActiveReceiver();
    }

    private void subscribeToDriversLocationReceiver() {
        if (driversLocationReceiver == null) {
            return;
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DriversLocationService.RESULT);
        getActivity().registerReceiver(driversLocationReceiver, intentFilter);
    }

    private void subscribeToRideReceiver() {
        if (rideReceiver == null) {
            return;
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DriverRideService.BROADCAST_CHANNEL);
        getActivity().registerReceiver(rideReceiver, intentFilter);
    }

    private void subscribeToIsActiveReceiver() {
        if (isActiveReceiver == null) {
            return;
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DriverRideService.ACTIVE_CHANNEL);
        getActivity().registerReceiver(isActiveReceiver, intentFilter);
    }

    private void onGetIsActive(Boolean isActive) {
        activeSwitch.setChecked(isActive);
    }

    private void onGetRide(RideDTO dto) {
        determineSubFragment(dto);
    }

    private void determineSubFragment(RideDTO dto) {
        if (dto == null) {
            setSubFragmentIfDifferent(blankFragment);
            removeRoute();
            return;
        }

        Ride.State state = Ride.State.valueOf(dto.getStatus().toUpperCase());

        switch (state) {
            case PENDING: case ACCEPTED:
                setSubFragmentIfDifferent(fragmentAcceptance);
                break;
            case STARTED:
                setSubFragmentIfDifferent(currentRideFragment);
                break;
            case CANCELED: case FINISHED: case REJECTED:
                setSubFragmentIfDifferent(blankFragment);
                removeRoute();
                break;
            default:
                throw new IllegalStateException("Unsupported state: " + state);
        }
    }

    private void initSwitchToggle() {
        activeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (compoundButton.isPressed() == false)
                    return;

                if (isChecked) {
                    IUserService.service.setActive(SettingsUtil.getUserJWT().getId()).enqueue(new Callback<Boolean>() {
                        @Override
                        public void onResponse(Call<Boolean> call, Response<Boolean> response) {}

                        @Override
                        public void onFailure(Call<Boolean> call, Throwable t) {
                            Log.e("REST ERROR", t.toString());
                        }
                    });
                } else {
                    IUserService.service.setInactive(SettingsUtil.getUserJWT().getId()).enqueue(new Callback<Boolean>() {
                        @Override
                        public void onResponse(Call<Boolean> call, Response<Boolean> response) {}

                        @Override
                        public void onFailure(Call<Boolean> call, Throwable t) {
                            Log.e("REST ERROR", t.toString());
                        }
                    });
                }
            }
        });
    }

    private void initSensorManager() {
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI
        );
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            shakePack.update(sensorEvent.values);
            String spdFormatted = String.format("%.1f", shakePack.getAcc() * 1000.0 / 3600.0);
            speed.setText(spdFormatted + " km/h");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}