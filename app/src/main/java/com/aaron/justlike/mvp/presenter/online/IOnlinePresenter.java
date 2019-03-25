package com.aaron.justlike.mvp.presenter.online;

import com.aaron.justlike.mvp.view.online.IOnlineView;

public interface IOnlinePresenter<T> {

    void attachView(IOnlineView<T> view);

    void detachView();

    void requestPhotos(boolean isRefresh);

    void requestCuratedPhotos(boolean isRefresh);

    void requestLoadMore();

    void requestLoadMoreCurated();
}
