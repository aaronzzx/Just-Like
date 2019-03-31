package com.aaron.justlike.http.unsplash.interfaces;

public interface UnsplashCallback<T> {

    void onSuccess(T t);

    void onFailure(String error);
}
