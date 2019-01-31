package com.aaron.justlike.online.presenter;

import com.aaron.justlike.online.view.IPreviewView;
import com.kc.unsplash.models.Photo;

public interface IPreviewPresenter {

    void attachView(IPreviewView view);

    void detachView();

    void requestImage(Photo photo);

    void requestMode(Photo photo, int mode);
}
