package com.aaron.justlike.app.online.entity;

import com.kc.unsplash.models.Photo;

public class PhotoEvent {

    private Photo photo;

    public PhotoEvent(Photo photo) {
        this.photo = photo;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }
}
