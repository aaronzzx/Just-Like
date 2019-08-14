package com.aaron.justlike.main;

import java.util.List;

/**
 * @author Aaron aaronzheng9603@gmail.com
 */
interface IMainContract {

    interface M<T> {

        void queryImage(M.OnQueryImageListener<T> listener);

        void saveImage(List<String> pathList, M.AddImageCallback<T> callback);

        void deleteImage(String path);

        void insertSortInfo(int sortType, boolean ascendingOrder);

        String[] querySortInfo();

        interface OnQueryImageListener<T> {

            void onSuccess(List<T> list);

            void onFailure(String args);
        }

        interface AddImageCallback<T> {

            void onSavedImage(List<T> savedList);
        }
    }

    interface V<T> {

        void attachPresenter();

        void onShowImage(List<T> list, int sortType, boolean ascendingOrder);

        void onShowAddImage(List<T> list);

        void onShowMessage(String args);

        void onShowEmptyView();

        void onHideEmptyView();

        void onHideRefresh();
    }

    interface P<T> {

        void detachView();

        void requestImage(List<T> list, boolean refreshMode);

        void addImage(List<T> list, List<String> pathList);

        void deleteImage(String path, boolean isEmpty);

        void setSortType(int sortType, boolean ascendingOrder);
    }
}
