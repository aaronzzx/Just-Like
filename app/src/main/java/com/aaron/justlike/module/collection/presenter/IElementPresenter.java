package com.aaron.justlike.module.collection.presenter;

import com.aaron.justlike.module.collection.view.IElementView;

import java.util.List;

public interface IElementPresenter<T> {

    void attachView(IElementView<T> view);

    void detachView();

    void requestImage(String title);

    void saveImage(String title, int size, List<String> list);

    void deleteImage(String title, String path);
}
