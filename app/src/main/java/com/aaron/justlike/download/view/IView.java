package com.aaron.justlike.download.view;

import java.util.List;

public interface IView<T> {

    void attachPresenter();

    void onShowImage(List<T> list);
}
