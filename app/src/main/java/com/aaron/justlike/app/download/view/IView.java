package com.aaron.justlike.app.download.view;

import com.aaron.justlike.http.entity.Photo;

import java.util.List;

public interface IView<T> {

    void attachPresenter();

    void onShowImage(List<T> list);

    void onOpenPreview(Photo photo);

    void onShowSnackBar(String path);

    void onShowProgress();

    void onHideProgress();
}
