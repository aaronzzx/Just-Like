package com.aaron.justlike.app.download.presenter;

public interface IPresenter {

    void detachView();

    void requestImage(boolean isAscending);

    void findImageByOnline(String path);
}
