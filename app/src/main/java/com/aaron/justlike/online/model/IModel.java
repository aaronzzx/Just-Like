package com.aaron.justlike.online.model;

import java.util.List;

public interface IModel<T> {

    void findImage(boolean refreshMode, Callback<T> callback);

    interface Callback<T> {

        void onSuccess(List<T> list);

        void onFailure();
    }
}
