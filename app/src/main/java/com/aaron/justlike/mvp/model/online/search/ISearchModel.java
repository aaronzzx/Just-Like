package com.aaron.justlike.mvp.model.online.search;

import com.aaron.justlike.http.unsplash.entity.collection.Collection;
import com.aaron.justlike.http.unsplash.entity.photo.Photo;

import java.util.List;

public interface ISearchModel {

    void findPhotos(String keyWord, Callback<Photo> callback);

    void findCollections(String keyWord, Callback<Collection> callback);

    interface Callback<T> {

        void onSuccess(List<T> list);

        void onFailure();
    }
}
