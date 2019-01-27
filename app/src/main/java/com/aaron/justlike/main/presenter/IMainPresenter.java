package com.aaron.justlike.main.presenter;

import android.app.Activity;

import java.util.List;

public interface IMainPresenter<T> {

    void detachView();

    void requestImage(List<T> list, boolean refreshMode);

    void addImage(List<T> list, List<String> pathList);

    void deleteImage(String path);

    void setSortType(int sortType, boolean ascendingOrder);

    void openImageSelector(Activity activity);
}
