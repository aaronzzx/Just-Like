package com.aaron.justlike.home.presenter;

public interface IPresenter {

    void detachView();

    void requestImage();

    void setSortType(int sortType, boolean ascendingOrder);
}
