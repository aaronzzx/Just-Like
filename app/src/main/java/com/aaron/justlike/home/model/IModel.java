package com.aaron.justlike.home.model;

import com.aaron.justlike.another.Image;

import java.util.List;

public interface IModel {

    void queryImage(OnQueryImageListener listener);

    void insertSortInfo(int sortType, boolean ascendingOrder);

    String[] querySortInfo();

    interface OnQueryImageListener {

        void onSuccess(List<Image> list);

        void onFailure(String args);
    }
}
