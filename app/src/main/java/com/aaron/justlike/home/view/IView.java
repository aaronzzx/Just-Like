package com.aaron.justlike.home.view;

import java.util.List;

public interface IView<T> {

    void attachPresenter();

    void onShowImage(List<T> list, int sortType, boolean ascendingOrder);

    void onShowMessage(String args);

    void onHideRefresh();
}
