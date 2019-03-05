package com.aaron.justlike.app.collection.presenter;

import com.aaron.justlike.app.collection.model.ElementModel;
import com.aaron.justlike.app.collection.model.IElementModel;
import com.aaron.justlike.app.collection.view.IElementView;
import com.aaron.justlike.app.main.entity.Image;

public class ElementPresenter implements IElementPresenter<Image> {

    private IElementView<Image> mView;
    private IElementModel<Image> mModel;

    @Override
    public void attachView(IElementView<Image> view) {
        mView = view;
        mModel = new ElementModel();
    }

    @Override
    public void detachView() {
        mView = null;
        mModel = null;
    }

    @Override
    public void requestImage(String title) {
        mModel.queryImage(title, imageList -> mView.onShowImage(imageList));
    }

    @Override
    public void deleteImage(String title, String path) {
        mModel.deleteImage(title, path);
    }
}
