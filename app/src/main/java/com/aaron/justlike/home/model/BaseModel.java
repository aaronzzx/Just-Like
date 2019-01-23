package com.aaron.justlike.home.model;

import android.util.Log;

import com.aaron.justlike.another.Image;
import com.aaron.justlike.util.FileUtils;

import java.util.ArrayList;
import java.util.List;

public class BaseModel implements IModel {

    private static final String PATH = "/storage/emulated/0/Pictures/JustLike";
    private static final String[] TYPE = {"jpg", "jpeg", "png", "gif"};

    @Override
    public void queryImage(OnQueryListener listener) {
        List<Image> imageList = getImage();
        if (imageList != null && imageList.size() != 0) {
            listener.onSuccess(imageList);
        } else {
            listener.onFail("本地无法查找到数据");
        }
    }

    private List<Image> getImage() {
        List<Image> imageList = new ArrayList<>();
        boolean success = FileUtils.getLocalFiles(imageList, PATH, TYPE);
        Log.d("BaseModel", "success: " + success);
        if (success) {
            return imageList;
        }
        return null;
    }
}
