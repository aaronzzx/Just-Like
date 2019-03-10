package com.aaron.justlike.mvp_view.about;

import java.util.List;

public interface IAboutView<A, B> {

    void attachPresenter();

    void onShowMessage(List<A> list);

    void onShowLibrary(List<B> list);
}
