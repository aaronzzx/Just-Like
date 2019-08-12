package com.aaron.justlike.online;

import com.aaron.justlike.common.http.unsplash.entity.photo.Photo;

import java.util.List;

public class ElementPresenter implements IElementPresenter {

    private IElementView<List<Photo>> mView;
    private ISearchModel mModel;

    private int currentPage = 1;

    public ElementPresenter(IElementView view) {
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
        mModel.findPhotosFromCollection(id, currentPage, 30, new ISearchModel.Callback<List<Photo>>() {
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
