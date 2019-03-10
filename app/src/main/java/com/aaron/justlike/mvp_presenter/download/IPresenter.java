package com.aaron.justlike.mvp_presenter.download;

public interface IPresenter {

    void detachView();

    void requestImage(boolean isAscending);

    void findImageByOnline(String path);
}
