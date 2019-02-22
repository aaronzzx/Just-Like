package com.aaron.justlike.http.interfaces;

public interface PhotoCallback<T> {

    void onSuccess(T t);

    void onFailure(String error);
}
