package com.aaron.justlike.collection;

import com.aaron.justlike.common.bean.Image;

import java.util.List;

public class ElementPresenter implements IElementContract.P<Image> {

    private IElementContract.V<Image> mView;
    private IElementContract.M<Image> mModel;

    @Override
    public void attachView(IElementContract.V<Image> view) {
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
