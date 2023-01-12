package com.example.shuttlemobile.passenger.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.shuttlemobile.ride.IRideService;
import com.example.shuttlemobile.ride.dto.RideDTO;
import com.example.shuttlemobile.user.IUserService;
import com.example.shuttlemobile.user.JWT;
import com.example.shuttlemobile.util.SettingsUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PassengerRideService extends Service {
    public static String BROADCAST_CHANNEL = "passenger_ride_service_broadcast_channel";
    public static String INTENT_RIDE_KEY = "ride";

    public PassengerRideService() {
    }

    @Override
    public void onCreate() {
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        final Handler handler = new Handler(Looper.getMainLooper());
        final int delay = 1000;

        executorService.execute(new Runnable() {
                @Override
                public void run() {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fetchRide();
                            handler.postDelayed(this, delay);
                        }
                    }, delay);
                }
            }
        );
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void fetchRide() {
        final JWT jwt = SettingsUtil.getUserJWT();
        Call<RideDTO> call = IRideService.service.getActiveRidePassenger(jwt.getId());
        call.enqueue(new Callback<RideDTO>() {
            @Override
            public void onResponse(Call<RideDTO> call, Response<RideDTO> response) {
                RideDTO ride = response.body();
                if (ride == null) {
                    Intent intent = new Intent(BROADCAST_CHANNEL);
                    intent.putExtra(INTENT_RIDE_KEY, (java.io.Serializable) null);
                    sendBroadcast(intent);
                } else {
                    Intent intent = new Intent(BROADCAST_CHANNEL);
                    intent.putExtra(INTENT_RIDE_KEY, ride);
                    sendBroadcast(intent);
                }
            }

            @Override
            public void onFailure(Call<RideDTO> call, Throwable t) {
                Log.e("REST ERROR", t.toString());
            }
        });
    }
}