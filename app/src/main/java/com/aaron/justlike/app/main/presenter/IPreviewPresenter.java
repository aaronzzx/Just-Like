package com.aaron.justlike.app.main.presenter;

import com.aaron.justlike.app.main.entity.ImageInfo;

public interface IPreviewPresenter {

    void detachView();

    void requestTitle(String path);

    ImageInfo requestImageInfo(String path);
}
