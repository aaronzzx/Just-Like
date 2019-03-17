package com.aaron.justlike.mvp.model.online;

import java.util.List;

public interface IModel<T> {

    void findImage(boolean refreshMode, Callback<T> callback);

    interface Callback<T> {

        void onSuccess(List<T> list);

        void onFailure();
    }
}
