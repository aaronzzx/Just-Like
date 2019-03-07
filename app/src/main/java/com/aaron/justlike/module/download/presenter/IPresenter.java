package com.aaron.justlike.module.download.presenter;

public interface IPresenter {

    void detachView();

    void requestImage(boolean isAscending);

    void findImageByOnline(String path);
}
