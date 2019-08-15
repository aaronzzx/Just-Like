package com.aaron.justlike.collection;

import com.aaron.justlike.common.bean.Album;

import java.util.List;

/**
 * @author Aaron aaronzheng9603@gmail.com
 */
interface ICollectionContract {

    interface M<T> {
        void queryCollection(Callback<T> callback);

        void insertCollection(List<String> list, String title, Callback<T> callback);

        void deleteCollection(String title);

        interface Callback<T> {
            default void onResponse(List<T> list) {
            }

            default void onFinish() {
            }
        }
    }

    interface V {
        <T> void onShowImage(List<T> list);
    }

    interface P {
        void attachView(V view);

        void detachView();

        void requestCollection(List<Album> list);

        int saveCollection(List<String> list, String title);

        void deleteCollection(String title);
    }
}
