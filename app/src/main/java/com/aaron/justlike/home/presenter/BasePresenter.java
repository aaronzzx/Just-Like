package com.aaron.justlike.home.presenter;

import com.aaron.justlike.another.Image;
import com.aaron.justlike.home.model.BaseModel;
import com.aaron.justlike.home.model.IModel;
import com.aaron.justlike.home.view.IView;
import com.aaron.justlike.util.FileUtils;

import java.util.List;

public class BasePresenter implements IPresenter {

    public static final int SORT_BY_DATE = 0;
    public static final int SORT_BY_NAME = 1;
    public static final int SORT_BY_SIZE = 2;
    public static final boolean ASCENDING_ORDER = true;
    public static final boolean DESCENDING_ORDER = false;

    private IView mView;
    private IModel mModel;

    private List<Image> mImageList;

    public BasePresenter(IView view) {
        mView = view;
        mModel = new BaseModel();
    }

    @Override
    public void detachView() {
        mView = null;
    }

    @Override
    public void requestImage(int sortType, boolean ascendingOrder) {
        mModel.queryImage(new IModel.OnQueryListener() {

            @Override
            public void onSuccess(List<Image> list) {
                mImageList = sortList(list, sortType, ascendingOrder);
                mView.showImage(mImageList);
            }

            @Override
            public void onFail(String args) {
                mView.showToast(args);
            }
        });
    }

    private List<Image> sortList(List<Image> imageList, int sortType, boolean ascendingOrder) {
        if (sortType == SORT_BY_DATE) {
            FileUtils.sortByDate(imageList, ascendingOrder);
        } else if (sortType == SORT_BY_NAME) {
            FileUtils.sortByName(imageList, ascendingOrder);
        } else if (sortType == SORT_BY_SIZE) {
            FileUtils.sortBySize(imageList, ascendingOrder);
        }
        return imageList;
    }
}
