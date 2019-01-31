package com.aaron.justlike.app.online.model;

import com.aaron.justlike.util.DownloadUtil;

import java.io.File;

public class PreviewModel implements IPreviewModel {

    private static final String PATH = "/storage/emulated/0/Pictures/JustLike/online/";

    @Override
    public void startDownload(String urls, String name, int type, Callback callback) {
        String imagePath = PATH + name;
        File file = new File(imagePath);
        if (file.exists()) {
            if (type != DownloadUtil.SET_WALLPAPER) {
                callback.onResponse("图片已下载");
                return;
            } else {
                callback.onWallpaper(imagePath);
                return;
            }
        }
        new DownloadUtil().downloadImage(urls, name, type);
    }
}
