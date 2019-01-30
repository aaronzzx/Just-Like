package com.aaron.justlike.online.model;

import com.kc.unsplash.Unsplash;
import com.kc.unsplash.api.Order;
import com.kc.unsplash.models.Photo;

import java.util.List;

public class BaseModel implements IModel<Photo> {

    private static final String CLIENT_ID = "18db24a3d59a1b2633897fa63f3f49455c2cbfa8a22e5b8520141cb2660fa816";
    private Unsplash mUnsplash = new Unsplash(CLIENT_ID);

    private int mPage = 1;

    @Override
    public void findImage(boolean refreshMode, Callback<Photo> callback) {
        if (refreshMode) {
            mPage = 1;
        }
        mUnsplash.getPhotos(mPage, 30, Order.LATEST, new Unsplash.OnPhotosLoadedListener() {
            @Override
            public void onComplete(List<Photo> photos) {
                callback.onSuccess(photos);
            }

            @Override
            public void onError(String error) {
                callback.onFailure();
            }
        });
        mPage++;
    }
}
