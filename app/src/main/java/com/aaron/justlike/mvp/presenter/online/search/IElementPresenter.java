package com.aaron.justlike.mvp.presenter.online.search;

import com.aaron.justlike.http.unsplash.entity.photo.Photo;

import java.util.List;

public interface IElementPresenter {

    void detachView();

    void requestPhotos(int id, List<Photo> oldList);
}
