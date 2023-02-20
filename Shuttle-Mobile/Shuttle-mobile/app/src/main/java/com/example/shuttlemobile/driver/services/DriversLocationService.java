package com.example.shuttlemobile.driver.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.shuttlemobile.ride.dto.VehicleLocationDTO;
import com.example.shuttlemobile.util.RetrofitUtils;
import com.example.shuttlemobile.util.Utils;
import com.example.shuttlemobile.vehicle.IVehicleService;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DriversLocationService extends PullingService {

    static final public String PREFIX = "CURRENT_RIDE_DRIVER_LOCATION_";

    static final public String RESULT = PREFIX + "PROCESSED";
    static final public String ERROR = PREFIX + "ERROR";
    static final public String NEW_ERROR_MESSAGE = PREFIX + "FETCHING_ERROR_MESSAGE";
    static final public String NEW_VEHICLES_LOCATIONS = PREFIX + "VEHICLES_LOCATIONS_MESSAGE";

    public void sendResult(List<VehicleLocationDTO> result) {
        Intent intent = new Intent(RESULT);
        intent.putExtra(NEW_VEHICLES_LOCATIONS, (Serializable) result);
        /*broadcaster.*/sendBroadcast(intent);
    }

    @Override
    protected void startExecutor(Intent intent){
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleWithFixedDelay(() -> {
            Call<List<VehicleLocationDTO>> call = IVehicleService.service.getDriversLocation();
            call.enqueue(new Callback<List<VehicleLocationDTO>>() {
                @Override
                public void onResponse(Call<List<VehicleLocationDTO>> call, Response<List<VehicleLocationDTO>> response) {
                    if(response.isSuccessful()){
                        sendResult(response.body());
                    }
                    else{
                        sendError(ERROR, NEW_ERROR_MESSAGE, response.message());
                    }
                }

                @Override
                public void onFailure(Call<List<VehicleLocationDTO>> call, Throwable t) {
                    sendError(ERROR, NEW_ERROR_MESSAGE, t.getMessage());
                }
            });

        }, 0, 2, TimeUnit.SECONDS);
    }
}