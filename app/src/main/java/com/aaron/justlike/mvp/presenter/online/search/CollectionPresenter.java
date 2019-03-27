package com.aaron.justlike.mvp.presenter.online.search;

import com.aaron.justlike.http.unsplash.entity.collection.Collection;
import com.aaron.justlike.mvp.model.online.search.ISearchModel;
import com.aaron.justlike.mvp.model.online.search.SearchModel;
import com.aaron.justlike.mvp.view.online.ISearchView;

import java.util.List;

public class CollectionPresenter implements ISearchPresenter<Collection> {

    private ISearchView<Collection> mView;
    private ISearchModel mModel;

    @Override
    public void attachView(ISearchView<Collection> view) {
        mView = view;
        mModel = new SearchModel();
    }

    @Override
    public void detachView() {
        mView = null;
        mModel = null;
    }

    @Override
    public void requestCollections(int requestMode, String keyWord) {
        if (requestMode == ISearchPresenter.FIRST_REQUEST) {
            mView.onShowProgress();
        } else {
            mView.onShowLoading();
        }
        mModel.findCollections(keyWord, new ISearchModel.Callback<Collection>() {
            @Override
            public void onSuccess(List<Collection> list) {
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
    public void requestPhotos(int requestMode, String keyWord) {

    }
}
