package com.example.shuttlemobile.driver.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.shuttlemobile.ride.Ride;
import com.example.shuttlemobile.util.RetrofitUtils;
import com.example.shuttlemobile.util.Utils;

import java.util.concurrent.ScheduledExecutorService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class PullingService extends Service {
    protected ScheduledExecutorService executor;
    protected LocalBroadcastManager broadcaster;

    protected abstract void startExecutor(Intent intent);

    @Override
    public void onCreate() {
        super.onCreate();
        broadcaster = LocalBroadcastManager.getInstance(this);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startExecutor(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    public void sendError(String intentFilter, String errorTitle, String errorMessage) {
        Intent intent = new Intent(intentFilter);
        intent.putExtra(errorTitle, errorMessage);
        broadcaster.sendBroadcast(intent);
    }
    private void stopExecutor(){
        executor.shutdownNow();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopExecutor();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}