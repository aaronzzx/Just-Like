package com.aaron.justlike.home.presenter;

public interface IMainPresenter {

    void detachView();

    void requestImage(int sortType, boolean ascending);

    void openImageSelector();

    void openImagePreview();

    void openNavHome();

    void openNavOnline();

    void openNavCollection();

    void openNavDownloadManager();

    void openNavAbout();
}
