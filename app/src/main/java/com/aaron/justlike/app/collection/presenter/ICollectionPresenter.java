package com.aaron.justlike.app.collection.presenter;

import com.aaron.justlike.app.collection.view.ICollectionView;

public interface ICollectionPresenter {

    void attachView(ICollectionView view);

    void detachView();

    void requestCollection();
}
