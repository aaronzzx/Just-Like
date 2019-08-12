package com.aaron.justlike.online;

import com.aaron.justlike.common.http.unsplash.Order;

import java.util.List;

public interface IModel<T> {

    void findPhotos(Order order, boolean refreshMode, Callback<T> callback);

    void findCuratedPhotos(Order order, boolean refreshMode, Callback<T> callback);

    void findRandomPhotos(int count, Callback<T> callback);

    interface Callback<T> {

        void onSuccess(List<T> list);

        void onFailure();
    }
}
