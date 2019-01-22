package com.aaron.justlike.home.view;

import com.aaron.justlike.another.Image;
import com.aaron.justlike.home.presenter.IMainPresenter;

import java.util.List;

public interface IMainView {

    void attachPresenter(IMainPresenter presenter);

    void initView();

    void showImage(List<Image> imageList);

    void showSlideMenu();

    void showRefresh();

    void enableFullScreen();

    void exitFullScreen();
}
