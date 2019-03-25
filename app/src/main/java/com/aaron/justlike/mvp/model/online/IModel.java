package com.aaron.justlike.mvp.model.online;

import java.util.List;

public interface IModel<T> {

    void findPhotos(boolean refreshMode, Callback<T> callback);

    void findCuratedPhotos(boolean refreshMode, Callback<T> callback);

    interface Callback<T> {

        void onSuccess(List<T> list);

        void onFailure();
    }
}
