package com.aaron.justlike.about.presenter;

public interface IAboutPresenter {

    void detachView();

    void requestMessage(int[] iconId, String[] title);

    void requestLibrary(String[] name, String[] author, String[] introduce);
}
