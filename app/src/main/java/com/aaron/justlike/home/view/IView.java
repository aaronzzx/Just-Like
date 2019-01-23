package com.aaron.justlike.home.view;

import com.aaron.justlike.another.Image;

import java.util.List;

public interface IView {

    void attachPresenter();

    void showImage(List<Image> imageList);

    void showToast(String args);

    void openPreviewActivity();

    void openSelectorActivity();

    void openMainActivity();

    void openOnlineActivity();

    void openCollectionActivity();

    void openDownloadManagerActivity();

    void openAboutActivity();
}
