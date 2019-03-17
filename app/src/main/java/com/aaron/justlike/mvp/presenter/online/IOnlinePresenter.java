package com.aaron.justlike.mvp.presenter.online;

public interface IOnlinePresenter {

    void detachView();

    void requestImage(boolean refreshMode);

    void requestLoadMore();
}
