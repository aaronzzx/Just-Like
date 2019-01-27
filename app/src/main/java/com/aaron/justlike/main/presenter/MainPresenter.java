package com.aaron.justlike.main.presenter;

import com.aaron.justlike.another.Image;
import com.aaron.justlike.main.model.BaseModel;
import com.aaron.justlike.main.model.IModel;
import com.aaron.justlike.main.view.IMainView;
import com.aaron.justlike.util.FileUtils;

import java.util.List;

public class MainPresenter implements IMainPresenter<Image> {

    public static final int SORT_BY_DATE = 1;
    public static final int SORT_BY_NAME = 2;
    public static final int SORT_BY_SIZE = 3;
    public static final boolean ASCENDING_ORDER = true;
    private static final int NO_SORT_STATUS = 0;
    public static final boolean DESCENDING_ORDER = false;

    private int mSortType;
    private boolean mAscendingOrder;

    private IMainView<Image> mView;
    private IModel mModel;

    public MainPresenter(IMainView<Image> view) {
        // 同时持有 IMainView 和 IModel 引用
        mView = view;
        mModel = new BaseModel();
    }

    /**
     * 断开与 IMainView 的连接
     */
    @Override
    public void detachView() {
        mView = null;
    }

    /**
     * 请求数据并回调 IMainView 函数显示图片
     */
    @Override
    public void requestImage(List<Image> imageList, boolean refreshMode) {
        // Part 1, 请求排序状态
        if (mSortType == NO_SORT_STATUS) {
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
        mModel.queryImage(new IModel.OnQueryImageListener<Image>() {

            @Override
            public void onSuccess(List<Image> list) {
                mView.onHideRefresh();
                if (refreshMode) {
                    if (imageList.containsAll(list)) {
                        onFailure("暂时没有新增的图片");
                        return;
                    }
                }
                imageList.clear();
                imageList.addAll(sortImageList(list, mSortType, mAscendingOrder));
                mView.onShowImage(sortImageList(list, mSortType, mAscendingOrder), mSortType, mAscendingOrder);
            }

            @Override
            public void onFailure(String args) {
                mView.onHideRefresh();
                mView.onShowMessage(args);
            }
        });
    }

    @Override
    public void addImage(List<Image> list, List<String> pathList) {
        for (String path : pathList) {
            list.add(new Image(path));
        }
        mView.onShowImage(list, mSortType, mAscendingOrder);
        mModel.saveImage(pathList);
    }

    @Override
    public void deleteImage(String path) {
        mModel.deleteImage(path);
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
