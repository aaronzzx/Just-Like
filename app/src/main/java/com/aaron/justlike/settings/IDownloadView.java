package com.aaron.justlike.settings;

import com.aaron.justlike.common.http.unsplash.entity.photo.Photo;

import java.util.List;

public interface IDownloadView<T> {

    void attachPresenter();

    void onShowImage(List<T> list);

    void onOpenPreview(Photo photo);

    void onShowSnackBar(String path);

    void onShowProgress();

    void onHideProgress();
}
