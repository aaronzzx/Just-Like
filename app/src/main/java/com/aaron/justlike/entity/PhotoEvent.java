package com.aaron.justlike.entity;

import com.aaron.justlike.http.entity.Photo;

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
