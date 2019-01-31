package com.aaron.justlike.app.online.presenter;

import com.aaron.justlike.app.online.view.IPreviewView;
import com.kc.unsplash.models.Photo;

public interface IPreviewPresenter {

    void attachView(IPreviewView view);

    void detachView();

    void requestImage(Photo photo);

    void requestMode(Photo photo, int mode);
}
