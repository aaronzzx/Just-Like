package com.aaron.justlike.app.main.presenter;

import com.aaron.justlike.app.main.entity.ImageInfo;
import com.aaron.justlike.app.main.view.IPreviewView;
import com.aaron.justlike.util.FileUtils;

public class PreviewPresenter implements IPreviewPresenter {

    private IPreviewView mView;

    public PreviewPresenter(IPreviewView view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
    }

    @Override
    public void requestTitle(String path) {
        String date = FileUtils.getLastModified(path, "yyyy-MM-dd HH:mm:ss");
        String[] array = date.split(" ");
        String title = array[0];
        mView.onShowTitle(title);
    }

    @Override
    public ImageInfo requestImageInfo(String path) {
        String time = FileUtils.getLastModified(path, "yyyy-MM-dd HH:mm:ss");
        String name = FileUtils.getImageName(path);
        String size = FileUtils.getImageSizeByMb(path);
        int[] resolution = FileUtils.getImageWidthHeight(path);
        String pixel = resolution[0] + " x " + resolution[1];
        return new ImageInfo(time, name, size, pixel, path);
    }
}
