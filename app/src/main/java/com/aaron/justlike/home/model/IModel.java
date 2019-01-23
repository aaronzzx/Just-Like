package com.aaron.justlike.home.model;

import com.aaron.justlike.another.Image;

import java.util.List;

public interface IModel {

    void queryImage(OnQueryListener listener);

    interface OnQueryListener {

        void onSuccess(List<Image> list);

        void onFail(String args);
    }
}
