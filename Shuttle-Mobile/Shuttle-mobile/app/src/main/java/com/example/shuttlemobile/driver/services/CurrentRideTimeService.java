package com.example.shuttlemobile.driver.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.shuttlemobile.R;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CurrentRideTimeService extends PullingService {

    private LocalDateTime startTime;

    static final public String PREFIX = "CURRENT_RIDE_DRIVER_TIME_";

    static final public String RESULT = PREFIX + "TIME_PROCESSED";
    static final public String NEW_TIME_MESSAGE = PREFIX + "TIME_MESSAGE";
    static final public String TIME_START = PREFIX + "time start";

    public void sendResult(String message) {
        Intent intent = new Intent(RESULT);
        if(message != null)
            intent.putExtra(NEW_TIME_MESSAGE, message);
        sendBroadcast(intent);
    }

    @Override
    protected void startExecutor(Intent intent){
        String startTimeStr = intent.getExtras().getString(TIME_START);
        startTime = LocalDateTime.parse(startTimeStr, DateTimeFormatter.ISO_DATE_TIME);

        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleWithFixedDelay(() -> {
            LocalDateTime now = LocalDateTime.now();


            long secondsTotal = ChronoUnit.SECONDS.between(startTime, LocalDateTime.now());
            @SuppressLint("DefaultLocale") String result = String.format("%d:%02d:%02d",
                    secondsTotal / 3600, (secondsTotal % 3600) / 60, secondsTotal % 60);
            sendResult(result);
        }, 0, 1, TimeUnit.SECONDS);
    }
}