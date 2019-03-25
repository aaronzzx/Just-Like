package com.aaron.justlike.mvp.model.online;

import com.aaron.justlike.http.unsplash.Order;
import com.aaron.justlike.http.unsplash.Unsplash;
import com.aaron.justlike.http.unsplash.entity.Photo;
import com.aaron.justlike.http.unsplash.interfaces.PhotoCallback;

import java.util.List;

public class OnlineModel implements IModel<Photo> {

    private Unsplash mUnsplash;

    private int mRecommend = 1;
    private int mCurated = 1;

    public OnlineModel() {
        mUnsplash = Unsplash.getInstance();
    }

    @Override
    public void findPhotos(boolean refreshMode, Callback<Photo> callback) {
        if (refreshMode) {
            mRecommend = 1;
        }
        mUnsplash.getPhotos(mRecommend, 30, Order.LATEST, new PhotoCallback<List<Photo>>() {
            @Override
            public void onSuccess(List<Photo> photos) {
                callback.onSuccess(photos);
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure();
            }
        });
        mRecommend++;
    }

    @Override
    public void findCuratedPhotos(boolean refreshMode, Callback<Photo> callback) {
        if (refreshMode) {
            mCurated = 1;
        }
        mUnsplash.getCuratedPhotos(mCurated, 30, Order.LATEST, new PhotoCallback<List<Photo>>() {
            @Override
            public void onSuccess(List<Photo> photos) {
                callback.onSuccess(photos);
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure();
            }
        });
        mCurated++;
    }
}
