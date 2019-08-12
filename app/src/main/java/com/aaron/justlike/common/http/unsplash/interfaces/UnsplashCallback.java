package com.aaron.justlike.common.http.unsplash.interfaces;

public interface UnsplashCallback<T> {

    void onSuccess(T t);

    void onFailure(String error);
}
