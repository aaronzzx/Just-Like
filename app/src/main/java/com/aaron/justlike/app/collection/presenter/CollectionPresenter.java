package com.aaron.justlike.app.collection.presenter;

import com.aaron.justlike.app.collection.entity.Album;
import com.aaron.justlike.app.collection.model.CollectionModel;
import com.aaron.justlike.app.collection.model.ICollectionModel;
import com.aaron.justlike.app.collection.view.ICollectionView;

import java.util.Collections;
import java.util.List;

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
    public void requestCollection(List<Album> albums) {
        mModel.queryCollection(list -> {
            if (list != null && !albums.containsAll(list)) {
                Collections.sort(list, (o1, o2) -> (int) (o2.getCreateAt() - o1.getCreateAt()));
                mView.onShowImage(list);
            }
        });
    }
}
