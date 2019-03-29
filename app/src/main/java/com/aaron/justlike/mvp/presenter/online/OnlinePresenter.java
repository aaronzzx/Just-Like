package com.aaron.justlike.mvp.presenter.online;

import com.aaron.justlike.http.unsplash.Order;
import com.aaron.justlike.http.unsplash.entity.photo.Photo;
import com.aaron.justlike.mvp.model.online.IModel;
import com.aaron.justlike.mvp.model.online.OnlineModel;
import com.aaron.justlike.mvp.view.online.IOnlineView;

import java.util.List;

public class OnlinePresenter implements IOnlinePresenter<Photo> {

    public static final int REQUEST_PHOTOS = 0;
    public static final int LOAD_MORE = 1;

    private IOnlineView<Photo> mView;
    private IModel<Photo> mModel;

    @Override
    public void attachView(IOnlineView<Photo> view) {
        mView = view;
        mModel = new OnlineModel();
    }

    @Override
    public void detachView() {
        mView = null;
        mModel = null;
    }

    @Override
    public void requestPhotos(Order order, boolean isRefresh, boolean isFilter, List<Photo> oldList) {
        if (isFilter) {
            mView.onShowRefresh();
        } else if (oldList.isEmpty() && isRefresh) {
            mView.onHideErrorView();
            mView.onShowProgress();
        }
        mModel.findPhotos(order, isRefresh, new IModel.Callback<Photo>() {
            @Override
            public void onSuccess(List<Photo> list) {
                if (mView == null) return;
                mView.onHideProgress();
                mView.onHideRefresh();
                mView.onShowPhoto(list);
            }

            @Override
            public void onFailure() {
                if (mView == null) return;
                if (oldList.isEmpty()) mView.onShowErrorView();
                mView.onHideProgress();
                mView.onHideRefresh();
                mView.onShowMessage(REQUEST_PHOTOS, "网络开小差了");
            }
        });
    }

    @Override
    public void requestCuratedPhotos(Order order, boolean isRefresh, boolean isFilter, List<Photo> oldList) {
        if (isFilter) {
            mView.onShowRefresh();
        } else if (oldList.isEmpty() && isRefresh) {
            mView.onHideErrorView();
            mView.onShowProgress();
        }
        mModel.findCuratedPhotos(order, isRefresh, new IModel.Callback<Photo>() {
            @Override
            public void onSuccess(List<Photo> list) {
                if (mView == null) return;
                mView.onHideProgress();
                mView.onHideRefresh();
                mView.onShowPhoto(list);
            }

            @Override
            public void onFailure() {
                if (mView == null) return;
                if (oldList.isEmpty()) mView.onShowErrorView();
                mView.onHideProgress();
                mView.onHideRefresh();
                mView.onShowMessage(REQUEST_PHOTOS, "网络开小差了");
            }
        });
    }

    @Override
    public void requestRandomPhotos(boolean isRefresh, int count, List<Photo> oldList) {
        if (oldList.isEmpty() && isRefresh) {
            mView.onHideErrorView();
            mView.onShowProgress();
        }
        mModel.findRandomPhotos(count, new IModel.Callback<Photo>() {
            @Override
            public void onSuccess(List<Photo> list) {
                if (mView == null) return;
                mView.onHideProgress();
                mView.onHideRefresh();
                mView.onShowPhoto(list);
            }

            @Override
            public void onFailure() {
                if (mView == null) return;
                if (oldList.isEmpty()) mView.onShowErrorView();
                mView.onHideProgress();
                mView.onHideRefresh();
                mView.onShowMessage(REQUEST_PHOTOS, "网络开小差了");
            }
        });
    }

    @Override
    public void requestLoadMore(Order order) {
        mView.onShowLoading();
        mModel.findPhotos(order, false, new IModel.Callback<Photo>() {
            @Override
            public void onSuccess(List<Photo> list) {
                if (mView == null) {
                    return;
                }
                mView.onHideLoading();
                mView.onShowMore(list);
            }

            @Override
            public void onFailure() {
                if (mView == null) {
                    return;
                }
                mView.onHideLoading();
                mView.onShowMessage(LOAD_MORE, "加载失败");
            }
        });
    }

    @Override
    public void requestLoadMoreCurated(Order order) {
        mView.onShowLoading();
        mModel.findCuratedPhotos(order, false, new IModel.Callback<Photo>() {
            @Override
            public void onSuccess(List<Photo> list) {
                if (mView == null) {
                    return;
                }
                mView.onHideLoading();
                mView.onShowMore(list);
            }

            @Override
            public void onFailure() {
                if (mView == null) {
                    return;
                }
                mView.onHideLoading();
                mView.onShowMessage(LOAD_MORE, "加载失败");
            }
        });
    }

    @Override
    public void requestLoadMoreRandom(int count) {
        mView.onShowLoading();
        mModel.findRandomPhotos(count, new IModel.Callback<Photo>() {
            @Override
            public void onSuccess(List<Photo> list) {
                if (mView == null) {
                    return;
                }
                mView.onHideLoading();
                mView.onShowMore(list);
            }

            @Override
            public void onFailure() {
                if (mView == null) {
                    return;
                }
                mView.onHideLoading();
                mView.onShowMessage(LOAD_MORE, "加载失败");
            }
        });
    }
}
