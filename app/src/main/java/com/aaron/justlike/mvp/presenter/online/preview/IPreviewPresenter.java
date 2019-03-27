package com.aaron.justlike.mvp.presenter.online.preview;

import android.content.Context;

import com.aaron.justlike.http.unsplash.entity.photo.Photo;
import com.aaron.justlike.mvp.view.online.IPreviewView;

public interface IPreviewPresenter {

    void attachView(IPreviewView view);

    void detachView();

    void requestImage(Photo photo);

    void requestMode(Context context, Photo photo, int mode);
}
