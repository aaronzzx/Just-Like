package com.aaron.justlike.app.online.model;

public interface IPreviewModel {

    void startDownload(String urls, String name, int type, Callback callback);

    interface Callback {

        void onResponse(String args);

        void onWallpaper(String path);
    }
}
