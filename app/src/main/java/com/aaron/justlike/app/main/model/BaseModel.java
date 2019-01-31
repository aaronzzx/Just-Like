package com.aaron.justlike.app.main.model;

import com.aaron.justlike.app.main.entity.Image;
import com.aaron.justlike.app.main.entity.SortInfo;
import com.aaron.justlike.util.FileUtils;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class BaseModel implements IModel<Image> {

    private static final String PATH = "/storage/emulated/0/Pictures/JustLike";
    private static final String[] TYPE = {"jpg", "jpeg", "png", "gif"};

    /**
     * 查询图片数据，查询结果回调实现类
     *
     * @param listener 回调接口
     */
    @Override
    @SuppressWarnings("unchecked")
    public void queryImage(OnQueryImageListener listener) {
        List<Image> imageList = getImage();
        if (imageList != null && imageList.size() != 0) {
            listener.onSuccess(imageList);
        } else {
            listener.onFailure("本地没有图片缓存哦");
        }
    }

    @Override
    public void saveImage(List<String> pathList, AddImageCallback<Image> callback) {
        List<Image> imageList = new ArrayList<>();
        int suffix = 1;
        for (String path : pathList) {
            String savedPath = FileUtils.saveToCache(path, suffix);
            imageList.add(new Image(savedPath));
            suffix++;
            if (suffix > 9) suffix = 1;
        }
        callback.onSavedImage(imageList);
    }

    @Override
    public void deleteImage(String path) {
        FileUtils.deleteFile(path);
    }

    /**
     * 将 UI 层的排序信息插入数据库
     */
    @Override
    public void insertSortInfo(int sortType, boolean ascendingOrder) {
        LitePal.deleteAll(SortInfo.class); // 删除所有记录，保证表中只有一条数据
        SortInfo sortInfo = new SortInfo();
        sortInfo.setSortType(String.valueOf(sortType));
        sortInfo.setAscendingOrder(String.valueOf(ascendingOrder));
        sortInfo.save();
    }

    /**
     * 检索数据库排序信息
     *
     * @return 返回字符串数组，第一位---排序类型，第二位---是否升序
     */
    @Override
    public String[] querySortInfo() {
        SortInfo sortInfo = LitePal.findFirst(SortInfo.class);
        if (sortInfo == null) {
            return null;
        }
        return new String[]{sortInfo.getSortType(), sortInfo.getAscendingOrder()};
    }

    /**
     * 查询 App 本地目录缓存
     */
    private List<Image> getImage() {
        List<Image> imageList = new ArrayList<>();
        boolean success = FileUtils.getLocalFiles(imageList, PATH, TYPE);
        if (success) {
            return imageList;
        }
        return null;
    }
}
