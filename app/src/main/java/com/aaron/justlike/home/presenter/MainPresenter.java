package com.aaron.justlike.home.presenter;

import android.os.Environment;

import com.aaron.justlike.another.Image;
import com.aaron.justlike.home.model.IMainModel;
import com.aaron.justlike.home.model.MainModel;
import com.aaron.justlike.home.view.IMainView;
import com.aaron.justlike.util.FileUtils;

import java.util.ArrayList;
import java.util.List;

public class MainPresenter implements IMainPresenter {

    public static final int SORT_BY_DATE = 100;
    public static final int SORT_BY_NAME = 101;
    public static final int SORT_BY_SIZE = 102;
    public static final boolean ASCENDING_ORDER = true;
    public static final boolean DESCENDING_ORDER = false;
    private static final String[] TYPE = {"jpg", "jpeg", "png", "gif"};
    private static final String PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath() + "/JustLike";

    private List<Image> mImageList = new ArrayList<>();

    private IMainView mView;
    private IMainModel mModel;

    public MainPresenter(IMainView mainView) {
        mView = mainView;
        mModel = new MainModel();
    }

    /**
     * 销毁 MainView 引用
     */
    @Override
    public void detachView() {
        mView = null;
    }

    /**
     * 请求图片数据并进行排序
     */
    @Override
    public void requestImage(int sortType, boolean ascending) {
        FileUtils.getLocalFiles(mImageList, PATH, TYPE);
        FileUtils.sortByDate(mImageList, true);
        mView.showImage(mImageList);
    }

    /**
     * 打开图片选择器
     */
    @Override
    public void openImageSelector() {

    }

    /**
     * 打开图片预览
     */
    @Override
    public void openImagePreview() {

    }

    /**
     * 打开侧滑菜单的 - 主页
     */
    @Override
    public void openNavHome() {

    }

    /**
     * 打开侧滑菜单的 - 在线壁纸
     */
    @Override
    public void openNavOnline() {

    }

    /**
     * 打开侧滑菜单的 - 集合
     */
    @Override
    public void openNavCollection() {

    }

    /**
     * 打开侧滑菜单的 - 下载管理
     */
    @Override
    public void openNavDownloadManager() {

    }

    /**
     * 打开侧滑菜单的 - 关于
     */
    @Override
    public void openNavAbout() {

    }
}
