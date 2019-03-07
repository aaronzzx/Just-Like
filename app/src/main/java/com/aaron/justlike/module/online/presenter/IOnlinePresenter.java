package com.aaron.justlike.module.online.presenter;

public interface IOnlinePresenter {

    void detachView();

    void requestImage(boolean refreshMode);

    void requestLoadMore();
}
