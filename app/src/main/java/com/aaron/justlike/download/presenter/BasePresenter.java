package com.aaron.justlike.download.presenter;

import com.aaron.justlike.download.model.BaseModel;
import com.aaron.justlike.download.model.IModel;
import com.aaron.justlike.download.view.IView;
import com.aaron.justlike.entity.Image;
import com.aaron.justlike.util.FileUtils;

import java.util.List;

public class BasePresenter implements IPresenter {

    public static final boolean ASCENDING = true;
    public static final boolean DESCENDING = false;

    private IView<Image> mView;
    private IModel<Image> mModel;

    public BasePresenter(IView<Image> view) {
        mView = view;
        mModel = new BaseModel();
    }

    @Override
    public void detachView() {
        mView = null;
    }

    @Override
    public void requestImage(boolean isAscending) {
        mModel.queryImage(list -> {
            List<Image> imageList = sortList(list, isAscending);
            mView.onShowImage(imageList);
        });
    }

    private List<Image> sortList(List<Image> list, boolean isAscending) {
        if (isAscending) {
            FileUtils.sortByDate(list, ASCENDING);
        } else {
            FileUtils.sortByDate(list, DESCENDING);
        }
        return list;
    }
}
