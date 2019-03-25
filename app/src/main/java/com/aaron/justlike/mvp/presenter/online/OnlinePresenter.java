package com.aaron.justlike.mvp.presenter.online;

import com.aaron.justlike.http.unsplash.entity.Photo;
import com.aaron.justlike.mvp.model.online.IModel;
import com.aaron.justlike.mvp.model.online.OnlineModel;
import com.aaron.justlike.mvp.view.online.IOnlineView;

import java.util.List;

public class OnlinePresenter implements IOnlinePresenter<Photo> {

    public static final int REQUEST_IMAGE = 0;
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
    public void requestPhotos(boolean isRefresh) {
        mModel.findPhotos(isRefresh, new IModel.Callback<Photo>() {
            @Override
            public void onSuccess(List<Photo> list) {
                if (mView == null) {
                    return;
                }
                mView.onHideProgress();
                mView.onHideRefresh();
                mView.onShowImage(list);
            }

            @Override
            public void onFailure() {
                if (mView == null) {
                    return;
                }
                mView.onHideProgress();
                mView.onHideRefresh();
                mView.onShowMessage(REQUEST_IMAGE, "网络开小差了");
            }
        });
    }

    @Override
    public void requestCuratedPhotos(boolean isRefresh) {
        mModel.findCuratedPhotos(isRefresh, new IModel.Callback<Photo>() {
            @Override
            public void onSuccess(List<Photo> list) {
                if (mView == null) {
                    return;
                }
                mView.onHideProgress();
                mView.onHideRefresh();
                mView.onShowImage(list);
            }

            @Override
            public void onFailure() {
                if (mView == null) {
                    return;
                }
                mView.onHideProgress();
                mView.onHideRefresh();
                mView.onShowMessage(REQUEST_IMAGE, "网络开小差了");
            }
        });
    }

    @Override
    public void requestLoadMore() {
        mView.onShowLoading();
        mModel.findPhotos(false, new IModel.Callback<Photo>() {
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
    public void requestLoadMoreCurated() {
        mView.onShowLoading();
        mModel.findCuratedPhotos(false, new IModel.Callback<Photo>() {
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
