package com.aaron.justlike.module.collection.presenter;

import com.aaron.justlike.module.collection.entity.Album;
import com.aaron.justlike.module.collection.view.ICollectionView;

import java.util.List;

public interface ICollectionPresenter {

    void attachView(ICollectionView view);

    void detachView();

    void requestCollection(List<Album> list);

    int saveCollection(List<String> list, String title);

    void deleteCollection(String title);
}
