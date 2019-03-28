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
    public void requestCollections(int requestMode, String keyWord, List<Collection> oldList) {
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
        mModel.findCollections(keyWord, new ISearchModel.Callback<Collection>() {
            @Override
            public void onSuccess(List<Collection> list) {
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
    public void requestPhotos(int requestMode, String keyWord, List<Collection> oldList) {

    }
}
