package com.aaron.justlike.app.download.view;

import com.kc.unsplash.models.Photo;

import java.util.List;

public interface IView<T> {

    void attachPresenter();

    void onShowImage(List<T> list);

    void onOpenPreview(Photo photo);

    void onShowSnackBar(String path);

    void onShowProgress();

    void onHideProgress();
}
