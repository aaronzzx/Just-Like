package com.aaron.justlike.settings;

import com.aaron.justlike.common.http.unsplash.entity.photo.Photo;

import java.util.List;

public interface IDownloadModel<T> {

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
