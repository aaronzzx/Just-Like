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
    public void requestPhotos(int requestMode, String keyWord, List<Photo> oldList) {
        if (keyWord.equals("")) return;

        switch (requestMode) {
            case ISearchPresenter.FIRST_REQUEST:
                if (oldList.size() == 0) {
                    mView.onHideSearchLogo();
                    mView.onShowProgress();
                } else {
                    mView.onShowRefresh();
                }
                break;
            case ISearchPresenter.LOAD_MORE:
                mView.onShowLoading();
                break;
        }
        mModel.findPhotos(keyWord, new ISearchModel.Callback<Photo>() {
            @Override
            public void onSuccess(List<Photo> list) {
                if (requestMode == ISearchPresenter.FIRST_REQUEST) {
                    mView.onHideProgress();
                    mView.onHideRefresh();
                    mView.onShow(list);
                } else {
                    mView.onHideLoading();
                    mView.onShowMore(list);
                }
            }

            @Override
            public void onFailure() {
                mView.onHideProgress();
                mView.onHideLoading();
                mView.onHideRefresh();
                if (oldList.size() == 0) {
                    mView.onShowSearchLogo();
                }
                mView.onShowMessage("网络开小差了");
            }
        });
    }

    @Override
    public void requestCollections(int requestMode, String keyWord, List<Photo> oldList) {

    }
}
