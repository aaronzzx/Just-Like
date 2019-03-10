package com.aaron.justlike.mvp_presenter.collection.element;

import com.aaron.justlike.entity.Image;
import com.aaron.justlike.mvp_model.collection.element.ElementModel;
import com.aaron.justlike.mvp_model.collection.element.IElementModel;
import com.aaron.justlike.mvp_view.collection.element.IElementView;

import java.util.List;

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
    public void saveImage(String title, int size, List<String> list) {
        mModel.insertImage(title, size, list, list1 -> mView.onShowAddImage(list1));
    }

    @Override
    public void deleteImage(String title, String path) {
        mModel.deleteImage(title, path);
    }
}
