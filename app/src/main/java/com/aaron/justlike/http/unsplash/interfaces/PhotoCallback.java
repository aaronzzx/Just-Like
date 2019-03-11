package com.aaron.justlike.http.unsplash.interfaces;

public interface PhotoCallback<T> {

    void onSuccess(T t);

    void onFailure(String error);
}
