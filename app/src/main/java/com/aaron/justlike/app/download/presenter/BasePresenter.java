package com.aaron.justlike.app.download.presenter;

import com.aaron.justlike.app.download.model.BaseModel;
import com.aaron.justlike.app.download.model.IModel;
import com.aaron.justlike.app.download.view.IView;
import com.aaron.justlike.app.main.entity.Image;
import com.aaron.justlike.http.entity.Photo;
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
        mModel = null;
    }

    @Override
    public void requestImage(boolean isAscending) {
        mModel.queryImage(list -> {
            List<Image> imageList = sortList(list, isAscending);
            mView.onShowImage(imageList);
        });
    }

    @Override
    public void findImageByOnline(String path) {
        String fileName = path.substring(path.lastIndexOf("/") + 1);
        String imageId = fileName.substring(0, fileName.indexOf("."));
        // 显示加载框
        mView.onShowProgress();
        mModel.searchImage(imageId, new IModel.SearchCallback() {

            @Override
            public void onSuccess(Photo photo) {
                mView.onHideProgress();
                mView.onOpenPreview(photo);
            }

            @Override
            public void onFailure() {
                mView.onHideProgress();
                mView.onShowSnackBar(path);
            }
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
