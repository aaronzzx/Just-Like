package com.aaron.justlike.online.search;

import androidx.lifecycle.LifecycleOwner;

import com.aaron.justlike.common.http.unsplash.entity.collection.SearchCollectionResult;
import com.aaron.justlike.common.http.unsplash.entity.photo.Photo;
import com.aaron.justlike.common.http.unsplash.entity.photo.SearchPhotoResult;

import java.util.List;

/**
 * @author Aaron aaronzheng9603@gmail.com
 */
interface ISerachContract {

    interface M {
        void findPhotos(LifecycleOwner lifecycle, String keyWord, int page, Callback<SearchPhotoResult> callback);

        void findCollections(LifecycleOwner lifecycle, String keyWord, int page, Callback<SearchCollectionResult> callback);

        void findPhotosFromCollection(LifecycleOwner lifecycle, int id, int page, int perPage, Callback<List<Photo>> callback);

        interface Callback<T> {
            void onSuccess(T list);

            void onFailure();
        }
    }

    interface V<T> {
        void attachPresenter();

        void onShow(List<T> list);

        void onShowMore(List<T> list);

        void onShowMessage(String msg);

        void onShowRefresh();

        void onHideRefresh();

        void onShowProgress();

        void onHideProgress();

        void onShowSearchLogo(String msg);

        void onHideSearchLogo();

        void onShowLoading();

        void onHideLoading();
    }

    interface P<T> {
        int FIRST_REQUEST = 0;
        int LOAD_MORE = 1;

        void detachView();

        void requestPhotos(int requestMode, String keyWord, List<T> list);

        void requestCollections(int requestMode, String keyWord, List<T> list);
    }
}
