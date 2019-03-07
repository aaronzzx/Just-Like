package com.aaron.justlike.module.main.presenter;

import com.aaron.justlike.module.main.entity.ImageInfo;

public interface IPreviewPresenter {

    void detachView();

    void requestTitle(String path);

    ImageInfo requestImageInfo(String path);
}
