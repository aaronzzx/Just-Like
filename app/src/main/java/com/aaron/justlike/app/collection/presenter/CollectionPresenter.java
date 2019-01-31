package com.aaron.justlike.app.collection.presenter;

import com.aaron.justlike.app.collection.entity.Album;
import com.aaron.justlike.app.collection.model.CollectionModel;
import com.aaron.justlike.app.collection.model.ICollectionModel;
import com.aaron.justlike.app.collection.view.ICollectionView;

public class CollectionPresenter implements ICollectionPresenter {

    private ICollectionView mView;
    private ICollectionModel<Album> mModel;

    @Override
    public void attachView(ICollectionView view) {
        mView = view;
        mModel = new CollectionModel();
    }

    @Override
    public void detachView() {
        mView = null;
        mModel = null;
    }

    @Override
    public void requestCollection() {
        mModel.queryCollection(list -> {
            if (list != null) {
                mView.onShowImage(list);
            }
        });
    }
}
