package com.aaron.justlike.mvp.presenter.mine;

import java.util.List;

public interface IMinePresenter<T> {

    void detachView();

    void requestImage(List<T> list, boolean refreshMode);

    void addImage(List<T> list, List<String> pathList);

    void deleteImage(String path, boolean isEmpty);

    void setSortType(int sortType, boolean ascendingOrder);
}
