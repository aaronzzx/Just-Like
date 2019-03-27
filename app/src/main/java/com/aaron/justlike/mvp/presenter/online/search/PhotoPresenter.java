package com.aaron.justlike.mvp.presenter.online.search;

import com.aaron.justlike.http.unsplash.entity.photo.Photo;
import com.aaron.justlike.mvp.model.online.search.ISearchModel;
import com.aaron.justlike.mvp.model.online.search.SearchModel;
import com.aaron.justlike.mvp.view.online.ISearchView;

import java.util.List;

public class PhotoPresenter implements ISearchPresenter<Photo> {

    private ISearchView<Photo> mView;
    private ISearchModel mModel;

    @Override
    public void attachView(ISearchView<Photo> view) {
        mView = view;
        mModel = new SearchModel();
    }

    @Override
    public void detachView() {
        mView = null;
        mModel = null;
    }

    @Override
    public void requestPhotos(int requestMode, String keyWord) {
        if (requestMode == FIRST_REQUEST) {
            mView.onShowProgress();
        } else {
            mView.onShowLoading();
        }
        mModel.findPhotos(keyWord, new ISearchModel.Callback<Photo>() {
            @Override
            public void onSuccess(List<Photo> list) {
                mView.onHideProgress();
                mView.onHideLoading();
                mView.onHideEmptyView();
                mView.onShow(list);
            }

            @Override
            public void onFailure() {
                mView.onHideProgress();
                mView.onShowMessage("网络开小差了");
            }
        });
    }

    @Override
    public void requestCollections(int requestMode, String keyWord) {

    }
}
