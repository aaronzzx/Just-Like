package com.aaron.justlike.module.online.model;

import android.util.Log;

import com.aaron.justlike.http.Order;
import com.aaron.justlike.http.Unsplash;
import com.aaron.justlike.http.entity.Photo;
import com.aaron.justlike.http.interfaces.PhotoCallback;

import java.util.List;

public class OnlineModel implements IModel<Photo> {

    private static final String TAG = "OnlineModel";
    private Unsplash mUnsplash;

    private int mPage = 1;

    public OnlineModel() {
        mUnsplash = Unsplash.getInstance();
        Log.d(TAG, "Unsplash: " + mUnsplash);
    }

    @Override
    public void findImage(boolean refreshMode, Callback<Photo> callback) {
        if (refreshMode) {
            mPage = 1;
        }
        mUnsplash.getPhotos(mPage, 30, Order.LATEST, new PhotoCallback<List<Photo>>() {
            @Override
            public void onSuccess(List<Photo> photos) {
                callback.onSuccess(photos);
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure();
            }
        });
        mPage++;
    }
}