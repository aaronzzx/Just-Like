package com.aaron.justlike.home.presenter;

import java.util.List;

public interface IPresenter<T> {

    void detachView();

    void requestImage(List<T> list, boolean refreshMode);

    void setSortType(int sortType, boolean ascendingOrder);
}
