package com.aaron.justlike.util;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadTask extends AsyncTask<String, Integer, Integer> {

    public static final int TYPE_SUCCESS = 0;
    public static final int TYPE_FAILED = 1;
    public static final int TYPE_PAUSED = 2;

    private boolean isPaused = false;
    private int latestProgress;

    private DownloadListener mDownloadListener;

    public DownloadTask(DownloadListener listener) {
        mDownloadListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(String... params) {
        InputStream is = null;
        RandomAccessFile savedFile = null;
        File file;
        try {
            long downloadedLength = 0; // 已下载的文件长度
            String downloadUrl = params[0];
            String fileName = "/" + params[1] + ".JPG";
            String directory = Environment.getExternalStorageDirectory().getPath() + "/JustLike/online";
            File dir = new File(directory);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            file = new File(directory + fileName);
            if (file.exists()) {
                downloadedLength = file.length();
            }
            long contentLength = getContentLength(downloadUrl);
            if (contentLength == 0) {
                return TYPE_FAILED;
            } else if (contentLength == downloadedLength) {
                return TYPE_SUCCESS;
            }
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .addHeader("RANGE", "byte=" + downloadedLength + "-")
                    .url(downloadUrl)
                    .build();
            Response response = client.newCall(request).execute();
            if (response != null) {
                is = response.body().byteStream();
                savedFile = new RandomAccessFile(file, "rw");
                savedFile.seek(downloadedLength);
                byte[] b = new byte[1024];
                int total = 0;
                int length;
                while ((length = is.read(b)) != -1) {
                    if (isPaused) {
                        return TYPE_PAUSED;
                    } else {
                        total += length;
                        savedFile.write(b, 0, length);
                        // 计算已下载的百分比
                        int progress = (int) ((total + downloadedLength) * 100.0 / contentLength);
                        publishProgress(progress);
                    }
                }
                response.body().close();
                return TYPE_SUCCESS;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (savedFile != null) {
                    savedFile.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return TYPE_FAILED;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        int progress = values[0];
        if (progress > latestProgress) {
            mDownloadListener.onProgress(progress);
            latestProgress = progress;
        }
    }

    @Override
    protected void onPostExecute(Integer status) {
        switch (status) {
            case TYPE_SUCCESS:
                mDownloadListener.onSuccess();
                break;
            case TYPE_FAILED:
                mDownloadListener.onFailed();
                break;
            case TYPE_PAUSED:
                mDownloadListener.onPaused();
                break;
        }
    }

    public void pauseDownload() {
        isPaused = true;
    }

    private long getContentLength(String downloadUrl) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(downloadUrl)
                .build();
        Response response = client.newCall(request).execute();
        if (response != null && response.isSuccessful()) {
            return response.body().contentLength();
        }
        return 0;
    }
}
