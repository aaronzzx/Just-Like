package com.aaron.justlike.home.model;

import com.aaron.justlike.another.Image;
import com.aaron.justlike.another.SortInfo;
import com.aaron.justlike.util.FileUtils;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class BaseModel implements IModel {

    private static final String PATH = "/storage/emulated/0/Pictures/JustLike";
    private static final String[] TYPE = {"jpg", "jpeg", "png", "gif"};

    @Override
    public void queryImage(OnQueryImageListener listener) {
        List<Image> imageList = getImage();
        if (imageList != null && imageList.size() != 0) {
            listener.onSuccess(imageList);
        } else {
            listener.onFailure("本地无法查找到数据");
        }
    }

    @Override
    public void insertSortInfo(int sortType, boolean ascendingOrder) {
        LitePal.deleteAll(SortInfo.class); // 删除所有记录，保证表中只有一条数据
        SortInfo sortInfo = new SortInfo();
        sortInfo.setSortType(String.valueOf(sortType));
        sortInfo.setAscendingOrder(String.valueOf(ascendingOrder));
        sortInfo.save();
    }

    @Override
    public String[] querySortInfo() {
        SortInfo sortInfo = LitePal.findFirst(SortInfo.class);
        if (sortInfo == null) {
            return null;
        }
        return new String[]{sortInfo.getSortType(), sortInfo.getAscendingOrder()};
    }

    private List<Image> getImage() {
        List<Image> imageList = new ArrayList<>();
        boolean success = FileUtils.getLocalFiles(imageList, PATH, TYPE);
        if (success) {
            return imageList;
        }
        return null;
    }
}
