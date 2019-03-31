package com.aaron.justlike.mvp.presenter.online.search;

import com.aaron.justlike.mvp.view.online.search.ISearchView;

import java.util.List;

public interface ISearchPresenter<T> {

    int FIRST_REQUEST = 0;
    int LOAD_MORE = 1;

    void attachView(ISearchView<T> view);

    void detachView();

    void requestPhotos(int requestMode, String keyWord, List<T> list);

    void requestCollections(int requestMode, String keyWord, List<T> list);
}
