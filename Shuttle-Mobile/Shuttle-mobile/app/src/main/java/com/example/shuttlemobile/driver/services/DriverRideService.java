package com.example.shuttlemobile.driver.services;

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

public class DriverRideService extends Service {
    public static String BROADCAST_CHANNEL = "driver_ride_service_broadcast_channel";
    public static String INTENT_RIDE_KEY = "ride";

    public static String ACTIVE_CHANNEL = "driver_is_active_channel";
    public static String INTENT_IS_ACTIVE_KEY = "is_active";

    public DriverRideService() {
    }

    @Override
    public void onCreate() {
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        final Handler handler = new Handler(Looper.getMainLooper());
        final int delay = 500;

        executorService.execute(new Runnable() {
                @Override
                public void run() {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fetchRide();
                            fetchActiveState();
                            handler.postDelayed(this, delay);
                        }
                    }, delay);
                }
            }
        );
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void fetchRide() {
        JWT jwt;
        try {
            jwt = SettingsUtil.getUserJWT();
        } catch (Exception e) {
            stopSelf();
            return;
        }
        if (jwt == null) {
            stopSelf();
            return;
        }
        Call<RideDTO> call = IRideService.service.getActiveRideDriver(jwt.getId());
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

    private void fetchActiveState() {
        try {
            IUserService.service.getActive(SettingsUtil.getUserJWT().getId()).enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    Intent intent = new Intent(ACTIVE_CHANNEL);
                    intent.putExtra(INTENT_IS_ACTIVE_KEY, response.body());
                    sendBroadcast(intent);
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    Log.e("REST ERROR", t.toString());
                }
            });
        } catch (Exception e) {
            stopSelf();
            return;
        }
    }
}