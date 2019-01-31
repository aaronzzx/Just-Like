package com.aaron.justlike.app.collection.model;

import java.util.List;

public interface ICollectionModel<T> {

    void queryCollection(Callback<T> callback);

    interface Callback<T> {

        void onResponse(List<T> list);
    }
}
