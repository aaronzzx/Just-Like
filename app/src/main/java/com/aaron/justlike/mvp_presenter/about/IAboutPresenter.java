package com.aaron.justlike.mvp_presenter.about;

public interface IAboutPresenter {

    void detachView();

    void requestMessage(int[] iconId, String[] title);

    void requestLibrary(String[] name, String[] author, String[] introduce);
}
