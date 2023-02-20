package com.example.shuttlemobile.user.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.shuttlemobile.message.MessageDTO;
import com.example.shuttlemobile.user.IUserService;
import com.example.shuttlemobile.user.JWT;
import com.example.shuttlemobile.util.ListDTO;
import com.example.shuttlemobile.util.SettingsUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserMessageService extends Service {
    public static String BROADCAST_CHANNEL = "user_service_broadcast_channel";
    public static String INTENT_MESSAGE_KEY = "message";

    final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public UserMessageService() {
    }

    @Override
    public void onCreate() {

        final Handler handler = new Handler(Looper.getMainLooper());
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
                            fetchMessages();
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

    private void fetchMessages() {
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
        IUserService.service.getMessages(jwt.getId()).enqueue(new Callback<ListDTO<MessageDTO>>() {
            @Override
            public void onResponse(Call<ListDTO<MessageDTO>> call, Response<ListDTO<MessageDTO>> response) {
                if (response.code() == 200) {
                    Intent intent = new Intent(BROADCAST_CHANNEL);
                    intent.putExtra(UserMessageService.INTENT_MESSAGE_KEY, response.body());
                    sendBroadcast(intent);
                } else {
                    Log.e("??", response.toString());
                }
            }

            @Override
            public void onFailure(Call<ListDTO<MessageDTO>> call, Throwable t) {
                Log.e("??", t.toString());
            }
        });
    }
}