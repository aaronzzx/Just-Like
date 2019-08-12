package com.aaron.justlike.common.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.aaron.justlike.R;

public class NotificationUtil {

    private static final String CHANNEL_ID = "channel_download";
    private static final String CHANNEL_NAME = "下载通知";

    public static void createNotification(Context context, int id, String title, String text, int progress) {
        getNotificationManager(context).notify(id, getNotification(context, title, text, progress));
    }

    public static NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static Notification getNotification(Context context, String title, String text, int progress) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.ic_stat_name);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
        builder.setContentTitle(title);
        builder.setContentText(text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            getNotificationManager(context).createNotificationChannel(channel);
            builder.setChannelId(CHANNEL_ID);
        }
        if (progress > 0) {
            builder.setProgress(100, progress, false);
        }
        return builder.build();
    }
}
