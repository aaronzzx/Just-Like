package com.aaron.justlike.app.online.presenter;

public interface IOnlinePresenter {

    void detachView();

    void requestImage(boolean refreshMode);

    void requestLoadMore();
}
