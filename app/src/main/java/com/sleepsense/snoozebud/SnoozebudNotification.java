package com.sleepsense.snoozebud;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

/**
 * Created by shayne on 2017-12-03.
 * Set up notification channels.
 */

public class SnoozebudNotification {
    public static final String NOTIFICATION_CHANNEL_ID = "0";
    private static final String NOTIFICATION_CHANNEL_NAME = "DEFAULT";

    public static void setupNotifications(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel =
                    new NotificationChannel(
                            NOTIFICATION_CHANNEL_ID,
                            NOTIFICATION_CHANNEL_NAME,
                            NotificationManager.IMPORTANCE_LOW);
            NotificationManager notificationManager =
                    (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
