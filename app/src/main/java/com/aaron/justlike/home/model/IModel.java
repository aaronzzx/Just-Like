package com.aaron.justlike.home.model;

import com.aaron.justlike.another.Image;

import java.util.List;

public interface IModel {

    void queryImage(OnQueryImageListener listener);

    String[] querySortInfo();

    interface OnQueryImageListener {

        void onSuccess(List<Image> list);

        void onFailure(String args);
    }
}
