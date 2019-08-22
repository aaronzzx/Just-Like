package com.aaron.justlike.online.search;

import androidx.lifecycle.LifecycleOwner;

import com.aaron.justlike.common.http.unsplash.entity.photo.Photo;

import java.util.List;

class ElementPresenter implements IElementContract.P {

    private IElementContract.V<List<Photo>> mView;
    private ISerachContract.M mModel;

    private int currentPage = 1;

    ElementPresenter(IElementContract.V<List<Photo>> view) {
        mView = view;
        mModel = new SearchModel();
    }

    @Override
    public void detachView() {
        mView = null;
        mModel = null;
    }

    @Override
    public void requestPhotos(int id, List<Photo> oldList, boolean isLoadMore) {
        if (currentPage == 1) {
            mView.onShowProgress();
        } else {
            mView.onShowLoading();
        }
        mView.onHideErrorView();
        mModel.findPhotosFromCollection((LifecycleOwner) mView, id, currentPage, 30, new ISerachContract.M.Callback<List<Photo>>() {
            @Override
            public void onSuccess(List<Photo> list) {
                mView.onHideProgress();
                if (!oldList.isEmpty()) mView.onHideLoading();
                if (!isLoadMore) {
                    mView.onShowPhotos(list);
                } else {
                    mView.onShowMore(list);
                }
            }

            @Override
            public void onFailure() {
                mView.onHideProgress();
                if (oldList.isEmpty()) {
                    currentPage = 1;
                    mView.onShowErrorView();
                } else {
                    mView.onHideLoading();
                }
                mView.onShowMessage("加载失败");
            }
        });
        currentPage++;
    }
}
