package com.aaron.justlike.app.download.model;

import com.aaron.justlike.app.main.entity.Image;
import com.aaron.justlike.http.PhotoCallback;
import com.aaron.justlike.http.Unsplash;
import com.aaron.justlike.http.entity.Photo;
import com.aaron.justlike.util.FileUtils;

import java.util.ArrayList;
import java.util.List;

public class BaseModel implements IModel<Image> {

    private static final String PATH = "/storage/emulated/0/Pictures/JustLike/online";
    private Unsplash mUnsplash;

    public BaseModel() {
        mUnsplash = Unsplash.getInstance();
    }

    @Override
    public void queryImage(QueryCallback<Image> callback) {
        List<Image> imageList = new ArrayList<>();
        FileUtils.getLocalFiles(imageList, PATH, "jpg");
        callback.onResponse(imageList);
    }

    @Override
    public void searchImage(String imageId, SearchCallback callback) {
        mUnsplash.getPhoto(imageId, new PhotoCallback() {
            @Override
            public void onSuccess(Photo photo) {
                callback.onSuccess(photo);
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure();
            }
        });
    }
}
