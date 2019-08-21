package com.aaron.justlike.common.http.download;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.aaron.justlike.R;
import com.aaron.justlike.common.manager.UiManager;
import com.aaron.justlike.online.preview.PreviewActivity;

public class DownloadService extends Service {

    private static final String CHANNEL_ID = "channel_download";
    private static final String CHANNEL_NAME = "下载通知";

    private DownloadTask mDownloadTask;
    private DownloadBinder mBinder = new DownloadBinder();

    private String mDownloadUrl;

    private IDownloadListener mListener = new IDownloadListener() {
        @Override
        public void onProgress(int progress) {
            getNotificationManager().notify(1, getNotification("Downloading...", progress));
        }

        @Override
        public void onSuccess() {
            mDownloadTask = null;
            stopForeground(true);
            getNotificationManager().notify(1, getNotification("Download Success", -1));
            UiManager.showShort("Download Success");
        }

        @Override
        public void onFailure() {
            mDownloadTask = null;
            stopForeground(true);
            getNotificationManager().notify(1, getNotification("Download Failure", -1));
            UiManager.showShort("Download Failure");
        }

        @Override
        public void onPause() {
            mDownloadTask = null;
            UiManager.showShort("Paused");
        }
    };

    public DownloadService() {

    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    private Notification getNotification(String title, int progress) {
        Intent intent = new Intent(this, PreviewActivity.class);
//        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_stat_name);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
//        actualBuilder.setContentIntent(pi);
        builder.setContentTitle(title);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            getNotificationManager().createNotificationChannel(channel);
            builder.setChannelId(CHANNEL_ID);
        }
        if (progress > 0) {
            builder.setContentText(progress + "%");
            builder.setProgress(100, progress, false);
        }
        return builder.build();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class DownloadBinder extends Binder {

        public void startDownload(String url) {
            if (mDownloadTask == null) {
                mDownloadUrl = url;
                mDownloadTask = new DownloadTask(mListener);
                mDownloadTask.execute(mDownloadUrl);
                startForeground(1, getNotification("Downloading...", 0));
                UiManager.showShort("Downloading...");
            }
        }
    }
}
