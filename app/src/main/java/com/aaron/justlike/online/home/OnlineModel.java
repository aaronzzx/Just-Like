package com.aaron.justlike.online.home;

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
    public void findPhotos(Order order, boolean refreshMode, Callback<Photo> callback) {
        if (refreshMode) {
            mRecommend = 1;
        }
        mUnsplash.getPhotos(mRecommend, 30, order, new UnsplashCallback<List<Photo>>() {
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
    public void findCuratedPhotos(Order order, boolean refreshMode, Callback<Photo> callback) {
        if (refreshMode) {
            mCurated = 1;
        }
        mUnsplash.getCuratedPhotos(mCurated, 30, order, new UnsplashCallback<List<Photo>>() {
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
    public void findRandomPhotos(int count, Callback<Photo> callback) {
        mUnsplash.getRandomPhotos(count, new UnsplashCallback<List<Photo>>() {
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
