package com.aaron.justlike.app.collection.presenter;

import com.aaron.justlike.app.collection.entity.Album;
import com.aaron.justlike.app.collection.view.ICollectionView;

import java.util.List;

public interface ICollectionPresenter {

    void attachView(ICollectionView view);

    void detachView();

    void requestCollection(List<Album> list);
}
