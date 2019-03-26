package com.aaron.justlike.mvp.model.online.preview;

import android.content.Context;

public interface IPreviewModel {

    void startDownload(Context context, String urls, String name, int type, Callback callback);

    interface Callback {

        void onResponse(String args);

        void onWallpaper(String path);
    }
}
