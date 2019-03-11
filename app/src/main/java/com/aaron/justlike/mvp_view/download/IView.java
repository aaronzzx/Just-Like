package com.aaron.justlike.mvp_view.download;

import com.aaron.justlike.http.unsplash.entity.Photo;

import java.util.List;

public interface IView<T> {

    void attachPresenter();

    void onShowImage(List<T> list);

    void onOpenPreview(Photo photo);

    void onShowSnackBar(String path);

    void onShowProgress();

    void onHideProgress();
}
