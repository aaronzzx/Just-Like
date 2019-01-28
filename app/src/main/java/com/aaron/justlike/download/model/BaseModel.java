package com.aaron.justlike.download.model;

import com.aaron.justlike.entity.Image;
import com.aaron.justlike.util.FileUtils;

import java.util.ArrayList;
import java.util.List;

public class BaseModel implements IModel<Image> {

    private static final String PATH = "/storage/emulated/0/Pictures/JustLike/online";

    @Override
    public void queryImage(QueryCallback<Image> callback) {
        List<Image> imageList = new ArrayList<>();
        FileUtils.getLocalFiles(imageList, PATH, "jpg");
        callback.onResponse(imageList);
    }
}
