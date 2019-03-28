package com.aaron.justlike.mvp.model.online.search;

import com.aaron.justlike.http.unsplash.entity.collection.SearchCollectionResult;
import com.aaron.justlike.http.unsplash.entity.photo.SearchPhotoResult;

public interface ISearchModel {

    void findPhotos(String keyWord, int page, Callback<SearchPhotoResult> callback);

    void findCollections(String keyWord, int page, Callback<SearchCollectionResult> callback);

    interface Callback<T> {

        void onSuccess(T list);

        void onFailure();
    }
}
