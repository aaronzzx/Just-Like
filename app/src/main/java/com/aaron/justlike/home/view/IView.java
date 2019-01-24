package com.aaron.justlike.home.view;

import com.aaron.justlike.another.Image;

import java.util.List;

public interface IView {

    void attachPresenter();

    void onShowImage(List<Image> imageList, int sortType, boolean ascendingOrder);

    void onShowMessage(String args);

    void onHideRefresh();
}
