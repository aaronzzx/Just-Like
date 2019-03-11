package com.aaron.justlike.mvp_model.online;

import android.util.Log;

import com.aaron.justlike.http.unsplash.Order;
import com.aaron.justlike.http.unsplash.Unsplash;
import com.aaron.justlike.http.unsplash.entity.Photo;
import com.aaron.justlike.http.unsplash.interfaces.PhotoCallback;

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
