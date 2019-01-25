package com.aaron.justlike.home.model;

import java.util.List;

public interface IModel {

    void queryImage(OnQueryImageListener listener);

    void saveImage(List<String> pathList);

    void deleteImage(String path);

    void insertSortInfo(int sortType, boolean ascendingOrder);

    String[] querySortInfo();

    interface OnQueryImageListener<T> {

        void onSuccess(List<T> list);

        void onFailure(String args);
    }
}
