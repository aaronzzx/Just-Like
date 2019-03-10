package com.aaron.justlike.mvp_model.collection;

import java.util.List;

public interface ICollectionModel<T> {

    void queryCollection(Callback<T> callback);

    void insertCollection(List<String> list, String title, Callback<T> callback);

    void deleteCollection(String title);

    interface Callback<T> {

        default void onResponse(List<T> list) {
        }

        default void onFinish() {
        }
    }
}
