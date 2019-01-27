package com.aaron.justlike.main.view;

public interface IPreviewView {

    void attachPresenter();

    void onShowTitle(String title);

    void animIn();

    void animOut();
}
