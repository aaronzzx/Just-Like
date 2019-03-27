package com.aaron.justlike.mvp.view.online;

import java.util.List;

public interface ISearchView<T> {

    void attachPresenter();

    void onShow(List<T> list);

    void onShowMore(List<T> list);

    void onShowMessage(String msg);

    void onShowProgress();

    void onHideProgress();

    void onHideEmptyView();

    void onShowLoading();

    void onHideLoading();
}
