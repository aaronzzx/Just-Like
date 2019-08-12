package com.aaron.justlike.online;

import android.content.Context;

import com.aaron.justlike.common.http.unsplash.entity.photo.Photo;

public interface IPreviewPresenter {

    void attachView(IPreviewView view);

    void detachView();

    void requestImage(Photo photo);

    void requestMode(Context context, Photo photo, int mode);
}
