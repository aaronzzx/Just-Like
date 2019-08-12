package com.aaron.justlike.collection;

import com.aaron.justlike.common.bean.Album;

import java.util.List;

public interface ICollectionPresenter {

    void attachView(ICollectionView view);

    void detachView();

    void requestCollection(List<Album> list);

    int saveCollection(List<String> list, String title);

    void deleteCollection(String title);
}
