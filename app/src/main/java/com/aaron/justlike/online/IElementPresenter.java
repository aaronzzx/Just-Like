package com.aaron.justlike.online;

import com.aaron.justlike.common.http.unsplash.entity.photo.Photo;

import java.util.List;

public interface IElementPresenter {

    void detachView();

    void requestPhotos(int id, List<Photo> oldList, boolean isLoadMore);
}
