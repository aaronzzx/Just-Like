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
        // 同时持有 IView 和 IModel 引用
        mView = view;
        mModel = new BaseModel();
    }

    /**
     * 断开与 IView 的连接
     */
    @Override
    public void detachView() {
        mView = null;
    }

    /**
     * 请求数据并回调 IView 函数显示图片
     */
    @Override
    public void requestImage() {
        // Part 1, 请求排序状态
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
        // Part 2, 向 IModel 请求数据
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

    /**
     * 设置排序类型，设立标志位，并将排序结果插入数据库
     */
    @Override
    public void setSortType(int sortType, boolean ascendingOrder) {
        mSortType = sortType;
        mAscendingOrder = ascendingOrder;
        mModel.insertSortInfo(sortType, ascendingOrder);
    }

    /**
     * 对 List 进行排序，根据传入参数决定排序类型
     *
     * @return 将排序后的 List 返回
     */
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
