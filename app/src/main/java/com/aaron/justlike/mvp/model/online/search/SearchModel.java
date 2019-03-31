package com.aaron.justlike.mvp.model.online.search;

import com.aaron.justlike.http.unsplash.Unsplash;
import com.aaron.justlike.http.unsplash.entity.collection.SearchCollectionResult;
import com.aaron.justlike.http.unsplash.entity.photo.Photo;
import com.aaron.justlike.http.unsplash.entity.photo.SearchPhotoResult;
import com.aaron.justlike.http.unsplash.interfaces.UnsplashCallback;

import java.util.List;

public class SearchModel implements ISearchModel {

    private Unsplash mUnsplash;

    private int mPhotos = 1;
    private int mCollections = 1;

    public SearchModel() {
        mUnsplash = Unsplash.getInstance();
    }

    @Override
    public void findPhotos(String keyWord, int page, Callback<SearchPhotoResult> callback) {
        mUnsplash.searchPhotos(keyWord, page, 30, new UnsplashCallback<SearchPhotoResult>() {
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
    public void findCollections(String keyWord, int page, Callback<SearchCollectionResult> callback) {
        mUnsplash.searchCollections(keyWord, page, 15, new UnsplashCallback<SearchCollectionResult>() {
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
    public void findPhotosFromCollection(int id, int page, int perPage, Callback<List<Photo>> callback) {
        mUnsplash.searchPhotosFromCollection(id, page, perPage, new UnsplashCallback<List<Photo>>() {
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
