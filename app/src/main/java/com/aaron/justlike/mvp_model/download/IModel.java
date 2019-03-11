package com.aaron.justlike.mvp_model.download;

import com.aaron.justlike.http.unsplash.entity.Photo;

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
