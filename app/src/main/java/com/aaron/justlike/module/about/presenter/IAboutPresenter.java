package com.aaron.justlike.module.about.presenter;

public interface IAboutPresenter {

    void detachView();

    void requestMessage(int[] iconId, String[] title);

    void requestLibrary(String[] name, String[] author, String[] introduce);
}
