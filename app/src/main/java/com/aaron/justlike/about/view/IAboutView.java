package com.aaron.justlike.about.view;

import java.util.List;

public interface IAboutView<A, B> {

    void attachPresenter();

    void onShowMessage(List<A> list);

    void onShowLibrary(List<B> list);
}
