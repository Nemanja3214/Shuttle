package com.example.shuttlemobile.passenger.fragments;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shuttlemobile.R;
import com.example.shuttlemobile.common.GenericUserFragment;
import com.example.shuttlemobile.common.SessionContext;
import com.example.shuttlemobile.common.adapter.EasyListAdapter;
import com.example.shuttlemobile.driver.DriverDTO;
import com.example.shuttlemobile.driver.IDriverService;
import com.example.shuttlemobile.passenger.IPassengerService;
import com.example.shuttlemobile.passenger.services.PassengerMessageService;
import com.example.shuttlemobile.passenger.subactivities.PassengerHistoryDetailsActivity;
import com.example.shuttlemobile.ride.IRideService;
import com.example.shuttlemobile.ride.Ride;
import com.example.shuttlemobile.ride.dto.RideDTO;
import com.example.shuttlemobile.route.LocationDTO;
import com.example.shuttlemobile.route.RouteDTO;
import com.example.shuttlemobile.util.ListDTO;
import com.example.shuttlemobile.util.SettingsUtil;
import com.example.shuttlemobile.util.ShakePack;
import com.example.shuttlemobile.util.Utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PassengerHistory extends GenericUserFragment implements SensorEventListener {
    private SensorManager sensorManager;
    private ShakePack shakePack = new ShakePack(12);
    private List<RideDTO> rides;
    private ListView lvRides;

    public static PassengerHistory newInstance(SessionContext session) {
        PassengerHistory fragment = new PassengerHistory();
        Bundle bundle = new Bundle();
        bundle.putSerializable(GenericUserFragment.KEY_SESSION, session);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_passenger_history, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initializeList();
        initSensorManager();
    }

    private void initSensorManager() {
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
    }

    private void initializeList() {

        Call<ListDTO<RideDTO>> call = IPassengerService.service.getRides(SettingsUtil.getUserJWT().getId());
        call.enqueue(new Callback<ListDTO<RideDTO>>() {
            @Override
            public void onResponse(Call<ListDTO<RideDTO>> call, Response<ListDTO<RideDTO>> response) {
                if(response.isSuccessful()){
                    rides = response.body().getResults();
                    rides.sort((r1, r2) -> {
                        if(r1.getEndTime() != null && r2.getEndTime() != null){
                            LocalDateTime time1 = LocalDateTime.parse(r1.getEndTime());
                            LocalDateTime time2 = LocalDateTime.parse(r2.getEndTime());
                            return time1.compareTo(time2);
                        }
                        return 1;
                    });
                    fillListView();
                }
                else{
                    Toast.makeText(requireContext(), "Failed to fetch rides", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ListDTO<RideDTO>> call, Throwable t) {
                Toast.makeText(requireContext(), "Failed to fetch rides", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fillListView(){
        lvRides = requireActivity().findViewById(R.id.list_p_rides);
        lvRides.setAdapter(new EasyListAdapter<RideDTO>() {
            @Override
            public List<RideDTO> getList() { return rides; }
            @Override
            public LayoutInflater getLayoutInflater() { return PassengerHistory.this.getLayoutInflater(); }
            @Override
            public int getListItemLayoutId() { return R.layout.list_p_history; }
            @Override
            public void applyToView(View view, RideDTO ride) {
                fillData(view, ride);
            }
        });
        lvRides.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                RideDTO obj = (RideDTO)lvRides.getItemAtPosition(i);
                openRideDetailsActivity(obj);
            }
        });
    }

    private void fillData(View view, RideDTO ride){
        TextView routeA = view.findViewById(R.id.list_p_history_route_A);
        TextView routeB = view.findViewById(R.id.list_p_history_route_B);
        TextView date = view.findViewById(R.id.list_p_history_date);
        TextView time = view.findViewById(R.id.list_p_history_time);
        TextView cost = view.findViewById(R.id.list_p_history_cost);
        TextView driverEmail = view.findViewById(R.id.list_p_history_dname);


        final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.YYYY");
        final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        if(ride.getEndTime() != null){
            LocalDateTime endTime = LocalDateTime.parse(ride.getEndTime());
            date.setText(dateFormatter.format(endTime));
            time.setText(timeFormatter.format(endTime));
        }

        List<RouteDTO> locations = ride.getLocations();
        routeA.setText(locations.get(0).getDeparture().getAddress());
        routeB.setText(locations.get(locations.size() - 1).getDestination().getAddress());
        if(ride.getTotalCost() != null){
            cost.setText(Double.toString(ride.getTotalCost()));
        }
        driverEmail.setText(ride.getDriver().getEmail());
        ((EasyListAdapter)lvRides.getAdapter()).notifyDataSetChanged();
    }

    private void openRideDetailsActivity(RideDTO ride) {
        Intent intent = new Intent(getActivity(), PassengerHistoryDetailsActivity.class);
        intent.putExtra(PassengerHistoryDetailsActivity.PARAM_RIDE, ride);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL
        );
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            shakePack.update(sensorEvent.values);
            if (shakePack.isShaking()) {
                onShake();
            }
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void onShake() {
        Collections.reverse(rides);
        ((EasyListAdapter)this.lvRides.getAdapter()).notifyDataSetChanged();
        Toast.makeText(getActivity(), "Shaking detected, reversing the list", Toast.LENGTH_SHORT).show();
    }
}