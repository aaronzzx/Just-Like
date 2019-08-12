package com.aaron.justlike.settings;

import com.aaron.justlike.common.bean.Image;
import com.aaron.justlike.common.http.unsplash.entity.photo.Photo;
import com.aaron.justlike.common.util.FileUtil;

import java.util.List;

public class DownloadPresenter implements IDownloadPresenter {

    public static final boolean ASCENDING = true;
    public static final boolean DESCENDING = false;

    private IDownloadView<Image> mView;
    private IDownloadModel<Image> mModel;

    public DownloadPresenter(IDownloadView<Image> view) {
        mView = view;
        mModel = new DownloadModel();
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
        mModel.searchImage(imageId, new IDownloadModel.SearchCallback() {

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
            FileUtil.sortByDate(list, ASCENDING);
        } else {
            FileUtil.sortByDate(list, DESCENDING);
        }
        return list;
    }
}
