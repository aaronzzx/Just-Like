package com.aaron.justlike.mvp_presenter.online;

public interface IOnlinePresenter {

    void detachView();

    void requestImage(boolean refreshMode);

    void requestLoadMore();
}
