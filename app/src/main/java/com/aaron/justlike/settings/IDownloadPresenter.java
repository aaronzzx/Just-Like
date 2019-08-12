package com.aaron.justlike.settings;

public interface IDownloadPresenter {

    void detachView();

    void requestImage(boolean isAscending);

    void findImageByOnline(String path);
}
