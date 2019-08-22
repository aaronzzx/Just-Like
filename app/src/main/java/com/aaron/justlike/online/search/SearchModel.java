package com.aaron.justlike.online.search;

import androidx.lifecycle.LifecycleOwner;

import com.aaron.justlike.common.http.unsplash.Unsplash;
import com.aaron.justlike.common.http.unsplash.entity.collection.SearchCollectionResult;
import com.aaron.justlike.common.http.unsplash.entity.photo.Photo;
import com.aaron.justlike.common.http.unsplash.entity.photo.SearchPhotoResult;
import com.aaron.justlike.common.http.unsplash.interfaces.UnsplashCallback;

import java.util.List;

class SearchModel implements ISerachContract.M {

    private Unsplash mUnsplash;

    private int mPhotos = 1;
    private int mCollections = 1;

    SearchModel() {
        mUnsplash = Unsplash.getInstance();
    }

    @Override
    public void findPhotos(LifecycleOwner lifecycle, String keyWord, int page, Callback<SearchPhotoResult> callback) {
        mUnsplash.searchPhotos(lifecycle, keyWord, page, 30, new UnsplashCallback<SearchPhotoResult>() {
            @Override
            public void onSuccess(SearchPhotoResult result) {
                callback.onSuccess(result);
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure();
            }
        });
        mPhotos++;
    }

    @Override
    public void findCollections(LifecycleOwner lifecycle, String keyWord, int page, Callback<SearchCollectionResult> callback) {
        mUnsplash.searchCollections(lifecycle, keyWord, page, 15, new UnsplashCallback<SearchCollectionResult>() {
            @Override
            public void onSuccess(SearchCollectionResult result) {
                callback.onSuccess(result);
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure();
            }
        });
    }

    @Override
    public void findPhotosFromCollection(LifecycleOwner lifecycle, int id, int page, int perPage, Callback<List<Photo>> callback) {
        mUnsplash.searchPhotosFromCollection(lifecycle, id, page, perPage, new UnsplashCallback<List<Photo>>() {
            @Override
            public void onSuccess(List<Photo> list) {
                callback.onSuccess(list);
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure();
            }
        });
    }
}
