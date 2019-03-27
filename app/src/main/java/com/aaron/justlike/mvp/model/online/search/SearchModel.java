package com.aaron.justlike.mvp.model.online.search;

import com.aaron.justlike.http.unsplash.Unsplash;
import com.aaron.justlike.http.unsplash.entity.collection.Collection;
import com.aaron.justlike.http.unsplash.entity.photo.Photo;
import com.aaron.justlike.http.unsplash.interfaces.PhotoCallback;

import java.util.List;

public class SearchModel implements ISearchModel {

    private Unsplash mUnsplash;

    private int mPhotos = 1;
    private int mCollections = 1;

    public SearchModel() {
        mUnsplash = Unsplash.getInstance();
    }

    @Override
    public void findPhotos(String keyWord, Callback<Photo> callback) {
        mUnsplash.searchPhotos(keyWord, mPhotos, 30, new PhotoCallback<List<Photo>>() {
            @Override
            public void onSuccess(List<Photo> list) {
                callback.onSuccess(list);
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure();
            }
        });
        mPhotos++;
    }

    @Override
    public void findCollections(String keyWord, Callback<Collection> callback) {
        mUnsplash.searchCollections(keyWord, mCollections, 15, new PhotoCallback<List<Collection>>() {
            @Override
            public void onSuccess(List<Collection> list) {
                callback.onSuccess(list);
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure();
            }
        });
    }
}
