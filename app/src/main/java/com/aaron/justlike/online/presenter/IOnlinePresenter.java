package com.aaron.justlike.online.presenter;

public interface IOnlinePresenter {

    void detachView();

    void requestImage(boolean refreshMode);

    void requestLoadMore();
}
