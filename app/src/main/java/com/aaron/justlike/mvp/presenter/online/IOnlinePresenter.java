package com.aaron.justlike.mvp.presenter.online;

import com.aaron.justlike.http.unsplash.Order;
import com.aaron.justlike.mvp.view.online.IOnlineView;

public interface IOnlinePresenter<T> {

    void attachView(IOnlineView<T> view);

    void detachView();

    void requestPhotos(Order order, boolean isRefresh, boolean isFilter);

    void requestCuratedPhotos(Order order, boolean isRefresh, boolean isFilter);

    void requestRandomPhotos(boolean isRefresh, int count);

    void requestLoadMore(Order order);

    void requestLoadMoreCurated(Order order);

    void requestLoadMoreRandom(int count);
}
