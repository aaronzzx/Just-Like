package com.aaron.justlike.mvp.presenter.collection;

import com.aaron.justlike.entity.Album;
import com.aaron.justlike.mvp.view.collection.ICollectionView;

import java.util.List;

public interface ICollectionPresenter {

    void attachView(ICollectionView view);

    void detachView();

    void requestCollection(List<Album> list);

    int saveCollection(List<String> list, String title);

    void deleteCollection(String title);
}
