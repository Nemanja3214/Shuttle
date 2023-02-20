package com.example.shuttlemobile.passenger.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import com.example.shuttlemobile.R;
import com.example.shuttlemobile.common.SettingsFragment;
import com.example.shuttlemobile.message.Message;
import com.example.shuttlemobile.passenger.Passenger;
import com.example.shuttlemobile.util.NotificationUtil;

import java.sql.Time;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class PassengerMessageService extends Service {
    private ExecutorService executorService;
    public PassengerMessageService() {
    }

    @Override
    public void onCreate() {
        executorService = Executors.newSingleThreadExecutor();
        final Handler handler = new Handler(Looper.getMainLooper());

        // Get from prefs.

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
       // SharedPreferences prefs = getSharedPreferences(SettingsFragment.PREF_FILE, Context.MODE_PRIVATE);
        String message_sync_interval = prefs.getString("sync_msg", "");

        //Log.e("--------------has ", "" + prefs.contains("sync_msg"));
        //Log.e("--------------val ", prefs.getString("sync_msg", "not found"));
        String[] possibleValues = getResources().getStringArray(R.array.sync_message_values);

        // TODO: Make delay longer, this is just for testing.
        int delay_ = 0;
        if (message_sync_interval.equals(possibleValues[0])) {
            return;
        }
        if (message_sync_interval.equals(possibleValues[1])) {
            delay_ = 30000;
        }
        if (message_sync_interval.equals(possibleValues[2])) {
            delay_ = 60000;
        }
        if (message_sync_interval.equals(possibleValues[3])) {
            delay_ = 90000;
        }

        final int delay = 1000;
        executorService.submit(new Runnable() {
                @Override
                public void run() {
                    if (Thread.currentThread().isInterrupted()) {
                        return;
                    }
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (Thread.currentThread().isInterrupted()) {
                                return;
                            }
                            fetchNewMessages();
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

    private void fetchNewMessages() {
        //Log.e("PassengerMessageService", "fetchNewMessages()");

        //Message newMessage = new Message();
        //newMessage.setSender(new Passenger());
        //newMessage.setMessage("Hello PassengerMessageService::fetchNewMessages()");

        // If I'm the recipient of this message, send a notification.
        //if (newMessage.getRecipient() == session.getUser()) {
        //sendNotification(newMessage);
        //}

        Intent intent = new Intent(NotificationUtil.PASSENGER_NOTIFICATION_CHANNEL_ID);
        sendBroadcast(intent);
    }

    private void sendNotification(Message newMessage) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), NotificationUtil.PASSENGER_NOTIFICATION_CHANNEL_ID)
                .setContentTitle(newMessage.getSender().getName())
                .setContentText(newMessage.getMessage())
                .setSmallIcon(R.drawable.ic_baseline_inbox_24)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(112213, builder.build());
    }
}