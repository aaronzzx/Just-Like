package com.aaron.justlike.download.model;

import com.kc.unsplash.models.Photo;

import java.util.List;

public interface IModel<T> {

    void queryImage(QueryCallback<T> callback);

    void searchImage(String imageId, SearchCallback callback);

    interface QueryCallback<T> {

        void onResponse(List<T> list);
    }

    interface SearchCallback {

        void onSuccess(Photo photo);

        void onFailure();
    }
}
