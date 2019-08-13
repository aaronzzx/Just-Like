package com.aaron.justlike.main.overview;

import com.aaron.baselib.base.IContract;

import java.util.List;

/**
 * @author Aaron
 * @email aaronzheng9603@gmail.com
 * @date 2019/8/13
 */
interface IOverviewContract {

    interface M<T> extends IContract.M {
        void queryImage(OnQueryImageListener<T> listener);

        void saveImage(List<String> pathList, AddImageCallback<T> callback);

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

    interface V<T> extends IContract.V {
        void onShowImage(List<T> list, int sortType, boolean ascendingOrder);

        void onShowAddImage(List<T> list);

        void onShowMessage(String args);

        void onShowEmptyView();

        void onHideEmptyView();

        void onHideRefresh();
    }

    abstract class P<T> extends IContract.P<V, M> {
        P(V v) {
            super(v);
        }

        abstract void requestImage(List<T> list, boolean refreshMode);

        abstract void addImage(List<T> list, List<String> pathList);

        abstract void deleteImage(String path, boolean isEmpty);

        abstract void setSortType(int sortType, boolean ascendingOrder);
    }
}
