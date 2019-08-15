package com.aaron.justlike.online.preview;

import android.content.Context;
import android.content.Intent;

import com.aaron.justlike.common.http.download.DownloadIntentService;

import java.io.File;

class PreviewModel implements IPreviewContract.M {

    private static final String PATH = "/storage/emulated/0/Pictures/JustLike/online/";

    PreviewModel() {
        File mkDir = new File(PATH);
        if (!mkDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            mkDir.mkdirs();
        }
    }

    @Override
    public void startDownload(Context context, String urls, String name, int mode, Callback callback) {
        String savePath = PATH + name;
        String tempPath = savePath + ".TEMP";
        File file1 = new File(savePath);
        File file2 = new File(tempPath);
        if (file1.exists()) {
            switch (mode) {
                case IPreviewInterface.NORMAL:
                    callback.onResponse("图片已下载");
                    return;
                case IPreviewInterface.SET_WALLPAPER:
                    callback.onWallpaper(savePath);
                    return;
            }
        } else if (file2.exists()) {
            callback.onResponse("图片正在下载");
            return;
        }
        Intent intent = new Intent(context, DownloadIntentService.class);
        intent.putExtra("urls", urls);
        intent.putExtra("path", savePath);
        intent.putExtra("mode", mode);
        context.startService(intent);
    }
}
