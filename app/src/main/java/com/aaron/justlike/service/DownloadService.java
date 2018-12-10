package com.aaron.justlike.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;

import com.aaron.justlike.R;
import com.aaron.justlike.util.DownloadListener;
import com.aaron.justlike.util.DownloadTask;
import com.aaron.justlike.util.FileUtils;

import java.io.File;

import androidx.core.app.NotificationCompat;

public class DownloadService extends Service {

    private static final int DOWNLOAD_NOTIFICATION_ID = (0x001);
    private static final String DOWNLOAD_CHANNEL_ID = "DOWNLOAD_NOTIFY_ID";
    private static final String DOWNLOAD_CHANNEL_NAME = "下载通知";
    private DownloadTask mDownloadTask;
    private String mDownloadUrl;
    private String mPhotoId;
    private int mFabType = 0; // 代表点击 FAB 是下载还是直接设置壁纸，在壁纸未下载的情况下先下载，完成后再设置壁纸

    private DownloadListener mDownloadListener = new DownloadListener() {
        @Override
        public void onProgress(int progress) {
            getNotificationManager().notify(DOWNLOAD_NOTIFICATION_ID, getNotification("正在下载 " + mPhotoId + ".JPG", "假装正在加速...", progress));
        }

        @Override
        public void onSuccess() {
            mDownloadTask = null;
            // 下载成功时将前台服务通知关闭，并创建一个下载成功的通知
            stopForeground(true);
            getNotificationManager().notify(DOWNLOAD_NOTIFICATION_ID, getNotification(mPhotoId + ".JPG", "下载成功", -1));
            Toast.makeText(DownloadService.this, "下载成功", Toast.LENGTH_SHORT).show();
            if (mFabType == 1) {
                String path = Environment.getExternalStorageDirectory().getPath() + "/JustLike/online/" + mPhotoId + ".JPG";
                FileUtils.setWallpaper(DownloadService.this, path);
            }
        }

        @Override
        public void onFailed() {
            mDownloadTask = null;
            // 下载失败时将前台服务通知关闭，并创建一个下载失败的通知
            stopForeground(true);
            getNotificationManager().notify(DOWNLOAD_NOTIFICATION_ID, getNotification(mPhotoId + ".JPG", "下载失败", -1));
            Toast.makeText(DownloadService.this, "下载失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPaused() {
            mDownloadTask = null;
            Toast.makeText(DownloadService.this, "下载暂停", Toast.LENGTH_SHORT).show();
        }
    };

    private DownloadBinder mDownloadBinder = new DownloadBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mDownloadBinder;
    }

    public class DownloadBinder extends Binder {

        public void startDownload(String url, String photoId, int fabType) {
            if (mDownloadTask == null) {
                mDownloadUrl = url;
                mPhotoId = photoId;
                mFabType = fabType;
                mDownloadTask = new DownloadTask(mDownloadListener);
                mDownloadTask.execute(mDownloadUrl, photoId);
                startForeground(1, getNotification("正在下载 " + mPhotoId + ".JPG", "假装正在加速...", 0));
                Toast.makeText(DownloadService.this, "正在下载", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    private Notification getNotification(String title, String subText, int progress) {
        NotificationManager manager = getNotificationManager();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String path = Environment.getExternalStorageDirectory().getPath() + "/JustLike/online/" + mPhotoId + ".JPG";
        Uri uri = Uri.parse(path);
        intent.setDataAndType(uri, "image/*");
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_for_nofitication);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_for_nofitication_large));
        builder.setContentIntent(pi);
        builder.setContentTitle(title);
        builder.setContentText(subText);
        builder.setAutoCancel(true);
        if (progress >= 0) {
            // 当 progress 大于或等于 0 时才需显示下载进度
            builder.setProgress(100, progress, false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(DOWNLOAD_CHANNEL_ID,
                    DOWNLOAD_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
            builder.setChannelId(DOWNLOAD_CHANNEL_ID);
        }
        return builder.build();
    }
}
