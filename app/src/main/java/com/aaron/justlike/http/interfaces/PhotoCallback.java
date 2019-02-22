package com.aaron.justlike.http.interfaces;

import com.aaron.justlike.http.entity.Photo;

public interface PhotoCallback {

    void onSuccess(Photo photo);

    void onFailure(String error);
}
