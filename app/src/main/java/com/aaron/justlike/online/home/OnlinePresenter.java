package com.aaron.justlike.online.home;

import androidx.lifecycle.LifecycleOwner;

import com.aaron.justlike.common.http.unsplash.Order;
import com.aaron.justlike.common.http.unsplash.entity.photo.Photo;

import java.util.List;

class OnlinePresenter implements IOnlineContract.P<Photo> {

    private static final int REQUEST_PHOTOS = 0;
    private static final int LOAD_MORE = 1;

    private IOnlineContract.V<Photo> mView;
    private IOnlineContract.M<Photo> mModel;

    OnlinePresenter(IOnlineContract.V<Photo> view) {
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
            mView.onShowRefreshOnlyAnim();
        } else if (oldList.isEmpty() && isRefresh) {
            mView.onHideErrorView();
            mView.onShowProgress();
        }
        boolean flag = isRefresh || isFilter;
        mModel.findPhotos((LifecycleOwner) mView, order, flag, new IOnlineContract.M.Callback<Photo>() {
            @Override
            public void onSuccess(List<Photo> list) {
                if (mView == null) return;
                mView.onHideProgress();
                mView.onHideRefresh();
                if (!oldList.containsAll(list)) {
                    mView.onShowPhoto(list, true);
                } else {
                    mView.onShowPhoto(list, false);
                }
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
            mView.onShowRefreshOnlyAnim();
        } else if (oldList.isEmpty() && isRefresh) {
            mView.onHideErrorView();
            mView.onShowProgress();
        }
        boolean flag = isRefresh || isFilter;
        mModel.findCuratedPhotos((LifecycleOwner) mView, order, flag, new IOnlineContract.M.Callback<Photo>() {
            @Override
            public void onSuccess(List<Photo> list) {
                if (mView == null) return;
                mView.onHideProgress();
                mView.onHideRefresh();
                if (!oldList.containsAll(list)) {
                    mView.onShowPhoto(list, true);
                } else {
                    mView.onShowPhoto(list, false);
                }
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
        mModel.findRandomPhotos((LifecycleOwner) mView, count, new IOnlineContract.M.Callback<Photo>() {
            @Override
            public void onSuccess(List<Photo> list) {
                if (mView == null) return;
                mView.onHideProgress();
                mView.onHideRefresh();
                if (!oldList.containsAll(list)) {
                    mView.onShowPhoto(list, true);
                } else {
                    mView.onShowPhoto(list, false);
                }
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
        mModel.findPhotos((LifecycleOwner) mView, order, false, new IOnlineContract.M.Callback<Photo>() {
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
        mModel.findCuratedPhotos((LifecycleOwner) mView, order, false, new IOnlineContract.M.Callback<Photo>() {
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
        mModel.findRandomPhotos((LifecycleOwner) mView, count, new IOnlineContract.M.Callback<Photo>() {
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
