package com.aaron.justlike.home.presenter;

public interface IPresenter {

    void detachView();

    void requestImage(int sortType, boolean ascendingOrder);
}
