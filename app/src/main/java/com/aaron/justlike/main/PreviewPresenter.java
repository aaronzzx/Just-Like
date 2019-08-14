package com.aaron.justlike.main;

import com.aaron.justlike.common.bean.ImageInfo;
import com.aaron.justlike.common.util.FileUtil;

public class PreviewPresenter implements IPreviewContract.P {

    private IPreviewContract.V mView;

    public PreviewPresenter(IPreviewContract.V view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
    }

    @Override
    public void requestTitle(String path) {
        String date = FileUtil.getLastModified(path, "yyyy-MM-dd HH:mm:ss");
        String[] array = date.split(" ");
        String title = array[0];
        mView.onShowTitle(title);
    }

    @Override
    public ImageInfo requestImageInfo(String path) {
        String time = FileUtil.getLastModified(path, "yyyy-MM-dd HH:mm:ss");
        String name = FileUtil.getImageName(path);
        String size = FileUtil.getImageSizeByMb(path);
        int[] resolution = FileUtil.getImageWidthHeight(path);
        String pixel = resolution[0] + " x " + resolution[1];
        return new ImageInfo(time, name, size, pixel, path);
    }
}
