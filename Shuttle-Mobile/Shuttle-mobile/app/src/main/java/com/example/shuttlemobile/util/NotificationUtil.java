package com.example.shuttlemobile.util;

import android.content.Context;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.shuttlemobile.R;

public class NotificationUtil {
    public static final String PASSENGER_NOTIFICATION_CHANNEL_ID = "passenger_notification_channel_id";
    public static final String DRIVER_NOTIFICATION_CHANNEL_ID = "driver_notification_channel_id";

    public static void sendNotification(Context context, String channelId, String title, String text, int icon, int id) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(icon)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(id, builder.build());
    }
}
