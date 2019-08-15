package com.aaron.justlike.online.search;

import com.aaron.justlike.common.http.unsplash.entity.photo.Photo;

import java.util.List;

/**
 * @author Aaron aaronzheng9603@gmail.com
 */
interface IElementContract {

    interface V<T> {
        void onShowProgress();

        void onHideProgress();

        void onShowLoading();

        void onHideLoading();

        void onShowErrorView();

        void onHideErrorView();

        void onShowMessage(String msg);

        void onShowPhotos(T t);

        void onShowMore(T t);
    }

    interface P {
        void detachView();

        void requestPhotos(int id, List<Photo> oldList, boolean isLoadMore);
    }
}
