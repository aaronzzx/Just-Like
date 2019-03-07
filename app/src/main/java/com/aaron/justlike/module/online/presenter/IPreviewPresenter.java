package com.aaron.justlike.module.online.presenter;

import com.aaron.justlike.http.entity.Photo;
import com.aaron.justlike.module.online.view.IPreviewView;

public interface IPreviewPresenter {

    void attachView(IPreviewView view);

    void detachView();

    void requestImage(Photo photo);

    void requestMode(Photo photo, int mode);
}
