package com.aaron.justlike.others.download;

import androidx.lifecycle.LifecycleOwner;

import com.aaron.justlike.common.http.unsplash.entity.photo.Photo;

import java.util.List;

/**
 * @author Aaron aaronzheng9603@gmail.com
 */
interface IDownloadContract {

    interface M<T> {
        void queryImage(QueryCallback<T> callback);

        void searchImage(LifecycleOwner lifecycle, String imageId, SearchCallback callback);

        interface QueryCallback<T> {
            void onResponse(List<T> list);
        }

        interface SearchCallback {
            void onSuccess(Photo photo);

            void onFailure();
        }
    }

    interface V<T> {
        void attachPresenter();

        void onShowImage(List<T> list);

        void onOpenPreview(Photo photo);

        void onShowSnackBar(String path);

        void onShowProgress();

        void onHideProgress();
    }

    interface P {
        void detachView();

        void requestImage(boolean isAscending);

        void findImageByOnline(String path);
    }
}
