package com.aaron.justlike.app.download.model;

import com.aaron.justlike.app.main.entity.Image;
import com.aaron.justlike.util.FileUtils;
import com.kc.unsplash.Unsplash;
import com.kc.unsplash.models.Photo;

import java.util.ArrayList;
import java.util.List;

public class BaseModel implements IModel<Image> {

    private static final String PATH = "/storage/emulated/0/Pictures/JustLike/online";
    private static final String CLIENT_ID = "18db24a3d59a1b2633897fa63f3f49455c2cbfa8a22" +
            "e5b8520141cb2660fa816";
    private static final Unsplash UNSPLASH = new Unsplash(CLIENT_ID);

    @Override
    public void queryImage(QueryCallback<Image> callback) {
        List<Image> imageList = new ArrayList<>();
        FileUtils.getLocalFiles(imageList, PATH, "jpg");
        callback.onResponse(imageList);
    }

    @Override
    public void searchImage(String imageId, SearchCallback callback) {
        UNSPLASH.getPhoto(imageId, new Unsplash.OnPhotoLoadedListener() {
            @Override
            public void onComplete(Photo photo) {
                callback.onSuccess(photo);
            }

            @Override
            public void onError(String error) {
                callback.onFailure();
            }
        });
    }
}
