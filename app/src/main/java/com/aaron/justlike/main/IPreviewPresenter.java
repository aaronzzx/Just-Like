package com.aaron.justlike.main;

import com.aaron.justlike.common.bean.ImageInfo;

public interface IPreviewPresenter {

    void detachView();

    void requestTitle(String path);

    ImageInfo requestImageInfo(String path);
}
