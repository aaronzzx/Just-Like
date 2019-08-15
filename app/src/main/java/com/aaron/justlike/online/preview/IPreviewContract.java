package com.aaron.justlike.online.preview;

import android.content.Context;

import com.aaron.justlike.common.http.unsplash.entity.photo.Photo;

/**
 * @author Aaron aaronzheng9603@gmail.com
 */
interface IPreviewContract {

    interface M {
        void startDownload(Context context, String urls, String name, int type, Callback callback);

        interface Callback {
            void onResponse(String args);

            void onWallpaper(String path);
        }
    }

    interface V {
        void onShowImage(String urls, String thumbnail);

        void onShowAuthorName(String args);

        void onShowAuthorAvatar(String urls);

        void onShowImageLikes(String args);

        void onShowImageDate(String args);

        void onShowProgress();

        void onHideProgressStatus();

        void onSetWallpaper(String imagePath);

        void onShowMessage(String args);
    }

    interface P {
        void detachView();

        void requestImage(Photo photo);

        void requestMode(Context context, Photo photo, int mode);
    }
}
