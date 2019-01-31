package com.aaron.justlike.util;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.aaron.justlike.custom.BaseApplication;

public class DownloadUtil {

    public static final int NORMAL = 0;
    public static final int SET_WALLPAPER = 1;
    private static final int FAILURE = -1;

    // 下载器
    private DownloadManager mDownloadManager;
    // 上下文
    private Context mContext;
    // 下载的ID
    private long mDownloadId;
    // photoId
    private String mPhotoName;
    // 判断是否需要设置壁纸
    private int mFabType;
    //广播监听下载的各个状态
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkStatus();
        }
    };

    public DownloadUtil() {
        mContext = BaseApplication.getContext();
    }

    public void downloadImage(String url, String name, int fabType) {
        mPhotoName = name;
        mFabType = fabType;

        // 创建下载任务
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

        // 在通知栏中显示，默认就是显示的
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle(name);

        // 设置下载的路径
        request.setDestinationInExternalPublicDir
                (Environment.DIRECTORY_PICTURES, "/JustLike/online/" + name);
        // 设置通知栏点击跳转
        request.setMimeType("image/jpeg");

        // 获取DownloadManager
        mDownloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        // 将下载请求加入下载队列，加入下载队列后会给该任务返回一个long型的id，通过该id可以取消任务，重启任务、获取下载的文件等等
        mDownloadId = mDownloadManager.enqueue(request);
        Toast.makeText(mContext, "开始下载", Toast.LENGTH_SHORT).show();

        // 注册广播接收者，监听下载状态
        mContext.registerReceiver(receiver,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    //检查下载状态
    private void checkStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        //通过下载的id查找
        query.setFilterById(mDownloadId);
        Cursor c = mDownloadManager.query(query);
        if (c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                //下载暂停
                case DownloadManager.STATUS_PAUSED:
                    break;
                //下载延迟
                case DownloadManager.STATUS_PENDING:
                    break;
                //正在下载
                case DownloadManager.STATUS_RUNNING:
                    break;
                //下载完成
                case DownloadManager.STATUS_SUCCESSFUL:
                    if (mFabType == SET_WALLPAPER) {
                        String path = Environment.getExternalStoragePublicDirectory
                                (Environment.DIRECTORY_PICTURES) + "/JustLike/online/" + mPhotoName;
                        int i = FileUtils.setWallpaper(mContext, path);
                        if (i == FAILURE) {
                            Toast.makeText(mContext, "改造失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    mContext.unregisterReceiver(receiver);
                    break;
                //下载失败
                case DownloadManager.STATUS_FAILED:
                    Toast.makeText(mContext, "下载失败", Toast.LENGTH_SHORT).show();
                    mContext.unregisterReceiver(receiver);
                    break;
            }
        }
        c.close();
    }
}
