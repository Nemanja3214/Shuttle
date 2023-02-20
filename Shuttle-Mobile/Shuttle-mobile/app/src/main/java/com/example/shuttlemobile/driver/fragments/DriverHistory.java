package com.example.shuttlemobile.driver.fragments;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.shuttlemobile.R;
import com.example.shuttlemobile.common.GenericUserFragment;
import com.example.shuttlemobile.common.SessionContext;
import com.example.shuttlemobile.common.adapter.EasyListAdapter;
import com.example.shuttlemobile.driver.IDriverService;
import com.example.shuttlemobile.driver.services.DriverMessageService;
import com.example.shuttlemobile.driver.subactivities.DriverHistoryDetailsActivity;
import com.example.shuttlemobile.ride.Ride;
import com.example.shuttlemobile.ride.dto.RideDTO;
import com.example.shuttlemobile.util.ListDTO;
import com.example.shuttlemobile.util.NotificationUtil;
import com.example.shuttlemobile.util.SettingsUtil;
import com.example.shuttlemobile.util.ShakePack;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverHistory extends GenericUserFragment implements SensorEventListener {
    private SensorManager sensorManager;
    private ShakePack shakePack = new ShakePack(12);
    private List<RideDTO> rides = new ArrayList<>();
    private ListView listView;
    private ProgressBar progressBar;

    public static DriverHistory newInstance(SessionContext session) {
        DriverHistory fragment = new DriverHistory();
        Bundle bundle = new Bundle();
        bundle.putSerializable(GenericUserFragment.KEY_SESSION, session);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_driver_history, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //initializeList();

        initViewElements(view);
        initSensorManager();
        fetchRides();
    }

    private void fetchRides() {
        IDriverService.service.getRides(SettingsUtil.getUserJWT().getId()).enqueue(new Callback<ListDTO<RideDTO>>() {
            @Override
            public void onResponse(Call<ListDTO<RideDTO>> call, Response<ListDTO<RideDTO>> response) {
                if (response.code() == 200) {
                    onFetchRides(response.body());
                } else {
                    Log.e("DriverHistory", response.toString());
                }
            }

            @Override
            public void onFailure(Call<ListDTO<RideDTO>> call, Throwable t) {
                Log.e("DriverHistory", t.toString());
            }
        });
    }

    private void initViewElements(View view) {
        listView = view.findViewById(R.id.list_d_history);
        progressBar = view.findViewById(R.id.progress_d_history);

        listView.setAdapter(new EasyListAdapter<RideDTO>() {
            @Override
            public List<RideDTO> getList() { return rides; }
            @Override
            public LayoutInflater getLayoutInflater() { return DriverHistory.this.getLayoutInflater(); }
            @Override
            public int getListItemLayoutId() { return R.layout.list_d_history; }
            @Override
            public void applyToView(View view, RideDTO obj) {
                TextView routeA = view.findViewById(R.id.list_d_history_route_A);
                TextView routeB = view.findViewById(R.id.list_d_history_route_B);
                TextView date = view.findViewById(R.id.list_d_history_date);
                TextView time = view.findViewById(R.id.list_d_history_time);
                TextView cost = view.findViewById(R.id.list_d_history_cost);

                final DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("dd/MM/YYYY");
                final DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH:mm");

                String dateStart = "", timeStart = "";
                String dateEnd = "", timeEnd = "";

                if (obj.getStartTime() != null) {
                    final LocalDateTime ts = LocalDateTime.parse(obj.getStartTime());
                    dateStart = ts.format(formatterDate);
                    timeStart = ts.format(formatterTime);
                }
                if (obj.getEndTime() != null) {
                    final LocalDateTime ts = LocalDateTime.parse(obj.getEndTime());
                    dateEnd = ts.format(formatterDate);
                    timeEnd = ts.format(formatterTime);
                }

                routeA.setText(obj.getLocations().get(0).getDeparture().getAddress());
                routeB.setText(obj.getLocations().get(obj.getLocations().size() - 1).getDestination().getAddress());
                date.setText(dateStart);
                time.setText(timeStart + " - " + timeEnd);
                cost.setText(obj.getTotalCost() + " RSD");
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                RideDTO obj = (RideDTO)listView.getItemAtPosition(i);
                openRideDetailsActivity(obj);
            }
        });
    }

    private void onFetchRides(ListDTO<RideDTO> response) {
        this.rides = response.getResults();
        this.progressBar.setVisibility(View.GONE);
        this.listView.setVisibility(View.VISIBLE);
        ((EasyListAdapter)this.listView.getAdapter()).notifyDataSetChanged();
    }

    private void initSensorManager() {
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
    }

    private void openRideDetailsActivity(RideDTO ride) {
        Intent intent = new Intent(getActivity(), DriverHistoryDetailsActivity.class);
        intent.putExtra(DriverHistoryDetailsActivity.PARAM_RIDE, ride);
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
        ((EasyListAdapter)this.listView.getAdapter()).notifyDataSetChanged();
        Toast.makeText(getActivity(), "Shaking detected, reversing the list", Toast.LENGTH_SHORT).show();
    }
}