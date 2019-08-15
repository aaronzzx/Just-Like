package com.aaron.justlike.others.download;

import android.util.Log;

import com.aaron.justlike.common.bean.Image;
import com.aaron.justlike.common.http.unsplash.Unsplash;
import com.aaron.justlike.common.http.unsplash.entity.photo.Photo;
import com.aaron.justlike.common.http.unsplash.interfaces.UnsplashCallback;
import com.aaron.justlike.common.util.FileUtil;

import java.util.ArrayList;
import java.util.List;

class DownloadModel implements IDownloadContract.M<Image> {

    private static final String TAG = "DownloadModel";
    private static final String PATH = "/storage/emulated/0/Pictures/JustLike/online";
    private Unsplash mUnsplash;

    DownloadModel() {
        mUnsplash = Unsplash.getInstance();
        Log.d(TAG, "Unsplash: " + mUnsplash);
    }

    @Override
    public void queryImage(QueryCallback<Image> callback) {
        List<Image> imageList = new ArrayList<>();
        FileUtil.getLocalFiles(imageList, PATH, "jpg");
        callback.onResponse(imageList);
    }

    @Override
    public void searchImage(String imageId, SearchCallback callback) {
        mUnsplash.getPhoto(imageId, new UnsplashCallback<Photo>() {
            @Override
            public void onSuccess(Photo photo) {
                callback.onSuccess(photo);
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure();
            }
        });
    }
}
