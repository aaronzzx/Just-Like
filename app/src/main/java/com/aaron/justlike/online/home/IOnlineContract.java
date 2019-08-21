package com.aaron.justlike.online.home;

import com.aaron.justlike.common.http.unsplash.Order;

import java.util.List;

/**
 * @author Aaron aaronzheng9603@gmail.com
 */
interface IOnlineContract {

    interface M<T> {
        void findPhotos(Order order, boolean refreshMode, Callback<T> callback);

        void findCuratedPhotos(Order order, boolean refreshMode, Callback<T> callback);

        void findRandomPhotos(int count, Callback<T> callback);

        interface Callback<T> {
            void onSuccess(List<T> list);

            void onFailure();
        }
    }

    interface V<T> {
        void attachPresenter();

        void onShowPhoto(List<T> list, boolean isDifference);

        void onShowMore(List<T> list);

        void onShowMessage(int requestMode, String args);

        void onShowRefresh();

        void onShowRefreshOnlyAnim();

        void onHideRefresh();

        void onShowProgress();

        void onHideProgress();

        void onShowErrorView();

        void onHideErrorView();

        void onShowLoading();

        void onHideLoading();
    }

    interface P<T> {
        void detachView();

        void requestPhotos(Order order, boolean isRefresh, boolean isFilter, List<T> oldList);

        void requestCuratedPhotos(Order order, boolean isRefresh, boolean isFilter, List<T> oldList);

        void requestRandomPhotos(boolean isRefresh, int count, List<T> oldList);

        void requestLoadMore(Order order);

        void requestLoadMoreCurated(Order order);

        void requestLoadMoreRandom(int count);
    }
}
