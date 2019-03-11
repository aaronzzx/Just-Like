package com.aaron.justlike.mvp_presenter.online.preview;

import com.aaron.justlike.http.unsplash.entity.Photo;
import com.aaron.justlike.mvp_view.online.IPreviewView;

public interface IPreviewPresenter {

    void attachView(IPreviewView view);

    void detachView();

    void requestImage(Photo photo);

    void requestMode(Photo photo, int mode);
}
