package com.aaron.justlike.download.model;

import java.util.List;

public interface IModel<T> {

    void queryImage(QueryCallback<T> callback);

    interface QueryCallback<T> {

        void onResponse(List<T> list);
    }
}
