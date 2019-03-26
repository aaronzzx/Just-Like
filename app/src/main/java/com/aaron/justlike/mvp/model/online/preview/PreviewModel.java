package com.aaron.justlike.mvp.model.online.preview;

import android.content.Context;
import android.content.Intent;

import com.aaron.justlike.mvp.presenter.online.preview.PreviewPresenter;
import com.aaron.justlike.service.DownloadService;

import java.io.File;

public class PreviewModel implements IPreviewModel {

    private static final String PATH = "/storage/emulated/0/Pictures/JustLike/online/";

    public PreviewModel() {
        File mkDir = new File(PATH);
        if (!mkDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            mkDir.mkdirs();
        }
    }

    @Override
    public void startDownload(Context context, String urls, String name, int mode, Callback callback) {
        String savePath = PATH + name;
        File file = new File(savePath);
        if (file.exists()) {
            switch (mode) {
                case PreviewPresenter.NORMAL:
                    callback.onResponse("图片已下载");
                    return;
                case PreviewPresenter.SET_WALLPAPER:
                    callback.onWallpaper(savePath);
                    return;
            }
        }
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra("urls", urls);
        intent.putExtra("path", savePath);
        intent.putExtra("mode", mode);
        context.startService(intent);
    }
}
