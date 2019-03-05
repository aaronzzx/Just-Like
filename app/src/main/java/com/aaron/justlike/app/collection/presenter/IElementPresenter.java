package com.aaron.justlike.app.collection.presenter;

import com.aaron.justlike.app.collection.view.IElementView;

public interface IElementPresenter<T> {

    void attachView(IElementView<T> view);

    void detachView();

    void requestImage(String title);

    void deleteImage(String title, String path);
}
