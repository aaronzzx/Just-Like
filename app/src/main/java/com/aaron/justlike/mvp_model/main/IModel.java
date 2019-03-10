package com.aaron.justlike.mvp_model.main;

import java.util.List;

public interface IModel<T> {

    void queryImage(OnQueryImageListener<T> listener);

    void saveImage(List<String> pathList, AddImageCallback<T> callback);

    void deleteImage(String path);

    void insertSortInfo(int sortType, boolean ascendingOrder);

    String[] querySortInfo();

    interface OnQueryImageListener<T> {

        void onSuccess(List<T> list);

        void onFailure(String args);
    }

    interface AddImageCallback<T> {

        void onSavedImage(List<T> savedList);
    }
}
