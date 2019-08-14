package com.aaron.justlike.main;

import com.aaron.justlike.common.bean.ImageInfo;

/**
 * @author Aaron aaronzheng9603@gmail.com
 */
interface IPreviewContract {

    interface V {

        void attachPresenter();

        void onShowTitle(String title);
    }

    interface P {

        void detachView();

        void requestTitle(String path);

        ImageInfo requestImageInfo(String path);
    }
}
