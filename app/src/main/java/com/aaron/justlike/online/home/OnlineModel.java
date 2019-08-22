package com.aaron.justlike.online.home;

import androidx.lifecycle.LifecycleOwner;

import com.aaron.justlike.common.http.unsplash.Order;
import com.aaron.justlike.common.http.unsplash.Unsplash;
import com.aaron.justlike.common.http.unsplash.entity.photo.Photo;
import com.aaron.justlike.common.http.unsplash.interfaces.UnsplashCallback;

import java.util.List;

class OnlineModel implements IOnlineContract.M<Photo> {

    private Unsplash mUnsplash;

    private int mRecommend = 1;
    private int mCurated = 1;

    OnlineModel() {
        mUnsplash = Unsplash.getInstance();
    }

    @Override
    public void findPhotos(LifecycleOwner lifecycle, Order order, boolean refreshMode, Callback<Photo> callback) {
        if (refreshMode) {
            mRecommend = 1;
        }
        mUnsplash.getPhotos(lifecycle, mRecommend, 30, order, new UnsplashCallback<List<Photo>>() {
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
    public void findCuratedPhotos(LifecycleOwner lifecycle, Order order, boolean refreshMode, Callback<Photo> callback) {
        if (refreshMode) {
            mCurated = 1;
        }
        mUnsplash.getCuratedPhotos(lifecycle, mCurated, 30, order, new UnsplashCallback<List<Photo>>() {
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

    @Override
    public void findRandomPhotos(LifecycleOwner lifecycle, int count, Callback<Photo> callback) {
        mUnsplash.getRandomPhotos(lifecycle, count, new UnsplashCallback<List<Photo>>() {
            @Override
            public void onSuccess(List<Photo> photos) {
                callback.onSuccess(photos);
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure();
            }
        });
    }
}
