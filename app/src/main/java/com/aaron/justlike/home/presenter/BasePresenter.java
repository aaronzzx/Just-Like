package com.aaron.justlike.home.presenter;

import com.aaron.justlike.another.Image;
import com.aaron.justlike.home.model.BaseModel;
import com.aaron.justlike.home.model.IModel;
import com.aaron.justlike.home.view.IView;
import com.aaron.justlike.util.FileUtils;

import java.util.ArrayList;
import java.util.List;

public class BasePresenter implements IPresenter {

    public static final int SORT_BY_DATE = 1;
    public static final int SORT_BY_NAME = 2;
    public static final int SORT_BY_SIZE = 3;
    public static final boolean ASCENDING_ORDER = true;
    public static final boolean DESCENDING_ORDER = false;

    private int mSortType;
    private boolean mAscendingOrder;

    private IView mView;
    private IModel mModel;

    private List<Image> mImageList = new ArrayList<>();

    public BasePresenter(IView view) {
        mView = view;
        mModel = new BaseModel();
    }

    @Override
    public void detachView() {
        mView = null;
    }

    @Override
    public void requestImage() {
        if (mSortType == 0) {
            String[] sortArray = mModel.querySortInfo();
            if (sortArray != null) {
                mSortType = Integer.parseInt(sortArray[0]);
                mAscendingOrder = Boolean.parseBoolean(sortArray[1]);
            } else {
                mSortType = SORT_BY_DATE;
                mAscendingOrder = ASCENDING_ORDER;
            }
        }
        mModel.queryImage(new IModel.OnQueryImageListener() {

            @Override
            public void onSuccess(List<Image> list) {
                mView.onHideRefresh();
                if (mImageList.containsAll(list)) {
                    return;
                }
                mImageList.clear();
                mImageList.addAll(sortImageList(list, mSortType, mAscendingOrder));
                mView.onShowImage(mImageList, mSortType, mAscendingOrder);
            }

            @Override
            public void onFailure(String args) {
                mView.onHideRefresh();
                mView.onShowMessage(args);
            }
        });
    }

    @Override
    public void setSortType(int sortType, boolean ascendingOrder) {
        mSortType = sortType;
        mAscendingOrder = ascendingOrder;
        // TODO 待将排序结果存入数据库
    }

    private List<Image> sortImageList(List<Image> imageList, int sortType, boolean ascendingOrder) {
        switch (sortType) {
            case SORT_BY_DATE:
                FileUtils.sortByDate(imageList, ascendingOrder);
                break;
            case SORT_BY_NAME:
                FileUtils.sortByName(imageList, ascendingOrder);
                break;
            case SORT_BY_SIZE:
                FileUtils.sortBySize(imageList, ascendingOrder);
                break;
        }
        return imageList;
    }
}
