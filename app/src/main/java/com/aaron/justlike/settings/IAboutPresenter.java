package com.aaron.justlike.settings;

public interface IAboutPresenter {

    void detachView();

    void requestMessage(int[] iconId, String[] title);

    void requestLibrary(String[] name, String[] author, String[] introduce);
}
