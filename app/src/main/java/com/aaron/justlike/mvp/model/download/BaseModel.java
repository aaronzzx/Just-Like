package com.aaron.justlike.mvp.model.download;

import android.util.Log;

import com.aaron.justlike.entity.Image;
import com.aaron.justlike.http.unsplash.Unsplash;
import com.aaron.justlike.http.unsplash.entity.photo.Photo;
import com.aaron.justlike.http.unsplash.interfaces.UnsplashCallback;
import com.aaron.justlike.util.FileUtil;

import java.util.ArrayList;
import java.util.List;

public class BaseModel implements IModel<Image> {

    private static final String TAG = "BaseModel";
    private static final String PATH = "/storage/emulated/0/Pictures/JustLike/online";
    private Unsplash mUnsplash;

    public BaseModel() {
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
