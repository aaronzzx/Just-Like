package com.aaron.justlike.main.presenter;

import com.aaron.justlike.main.entity.ImageInfo;

public interface IPreviewPresenter {

    void detachView();

    void requestTitle(String path);

    ImageInfo getImageInfo(String path);
}
