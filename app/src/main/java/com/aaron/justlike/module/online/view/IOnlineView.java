package com.aaron.justlike.module.online.view;

import java.util.List;

public interface IOnlineView<T> {

    void attachPresenter();

    void onShowImage(List<T> list);

    void onShowMore(List<T> list);

    void onShowMessage(int mode, String args);

    void onHideRefresh();

    void onHideProgress();

    void onShowLoading();

    void onHideLoading();
}
