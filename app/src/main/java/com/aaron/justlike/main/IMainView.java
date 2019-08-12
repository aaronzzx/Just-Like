package com.aaron.justlike.main;

import java.util.List;

public interface IMainView<T> {

    void attachPresenter();

    void onShowImage(List<T> list, int sortType, boolean ascendingOrder);

    void onShowAddImage(List<T> list);

    void onShowMessage(String args);

    void onShowEmptyView();

    void onHideEmptyView();

    void onHideRefresh();
}
