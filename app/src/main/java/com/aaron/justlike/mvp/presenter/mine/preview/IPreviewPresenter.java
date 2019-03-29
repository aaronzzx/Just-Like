package com.aaron.justlike.mvp.presenter.mine.preview;

import com.aaron.justlike.entity.ImageInfo;

public interface IPreviewPresenter {

    void detachView();

    void requestTitle(String path);

    ImageInfo requestImageInfo(String path);
}
