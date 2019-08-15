package com.aaron.justlike.collection;

import java.util.List;

/**
 * @author Aaron aaronzheng9603@gmail.com
 */
interface IElementContract {

    interface M<T> {
        void queryImage(String title, Callback<T> callback);

        void insertImage(String title, int size, List<String> list, Callback<T> callback);

        void deleteImage(String title, String path);

        interface Callback<T> {
            void onResponse(List<T> list);
        }
    }

    interface V<T> {
        void onShowImage(List<T> list);

        void onShowAddImage(List<T> list);
    }

    interface P<T> {
        void attachView(V<T> view);

        void detachView();

        void requestImage(String title);

        void saveImage(String title, int size, List<String> list);

        void deleteImage(String title, String path);
    }
}
