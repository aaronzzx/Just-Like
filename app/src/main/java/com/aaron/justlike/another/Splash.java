package com.aaron.justlike.another;

import com.kc.unsplash.models.Photo;

public class Splash {

    private Photo mPhoto;

    public Splash(Photo photo) {
        mPhoto = photo;
    }

    public Photo getPhoto() {
        return mPhoto;
    }

    public void setPhoto(Photo photo) {
        mPhoto = photo;
    }
}
