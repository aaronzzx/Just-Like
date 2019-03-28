package com.aaron.justlike.mvp.model.online.search;

import com.aaron.justlike.http.unsplash.Unsplash;
import com.aaron.justlike.http.unsplash.entity.collection.SearchCollectionResult;
import com.aaron.justlike.http.unsplash.entity.photo.SearchPhotoResult;
import com.aaron.justlike.http.unsplash.interfaces.PhotoCallback;

public class SearchModel implements ISearchModel {

    private Unsplash mUnsplash;

    private int mPhotos = 1;
    private int mCollections = 1;

    public SearchModel() {
        mUnsplash = Unsplash.getInstance();
    }

    @Override
    public void findPhotos(String keyWord, int page, Callback<SearchPhotoResult> callback) {
        mUnsplash.searchPhotos(keyWord, page, 30, new PhotoCallback<SearchPhotoResult>() {
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
        mUnsplash.searchCollections(keyWord, page, 15, new PhotoCallback<SearchCollectionResult>() {
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
}
