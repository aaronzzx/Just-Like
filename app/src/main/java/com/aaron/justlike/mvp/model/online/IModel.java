package com.aaron.justlike.mvp.model.online;

import com.aaron.justlike.http.unsplash.Order;

import java.util.List;

public interface IModel<T> {

    void findPhotos(Order order, boolean refreshMode, Callback<T> callback);

    void findCuratedPhotos(Order order, boolean refreshMode, Callback<T> callback);

    interface Callback<T> {

        void onSuccess(List<T> list);

        void onFailure();
    }
}
