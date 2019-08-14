package com.aaron.justlike.main;

import com.aaron.justlike.common.bean.Collection;
import com.aaron.justlike.common.bean.Element;
import com.aaron.justlike.common.bean.Image;
import com.aaron.justlike.common.bean.SortInfo;
import com.aaron.justlike.common.util.FileUtil;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainModel implements IMainContract.M<Image> {

    private static final String PATH = "/storage/emulated/0/Pictures/JustLike";
    private static final String[] TYPE = {"jpg", "jpeg", "png", "gif"};

    private ExecutorService mExecutorService;

    public MainModel() {
        mExecutorService = Executors.newSingleThreadExecutor();
    }

    /**
     * 查询图片数据，查询结果回调实现类
     *
     * @param listener 回调接口
     */
    @Override
    public void queryImage(OnQueryImageListener<Image> listener) {
        mExecutorService.execute(() -> {
            List<Image> list = getImage();
            if (list != null && list.size() != 0) {
                listener.onSuccess(list);
            } else {
                listener.onFailure("本地没有图片缓存哦");
            }
        });
    }

    @Override
    public void saveImage(List<String> pathList, AddImageCallback<Image> callback) {
        mExecutorService.execute(() -> {
            List<Image> imageList = new ArrayList<>();
            int suffix = 1;
            for (String path : pathList) {
                String savedPath = FileUtil.saveToCache(path, suffix);
                imageList.add(new Image(savedPath));
                suffix++;
                if (suffix > 9) suffix = 1;
            }
            callback.onSavedImage(imageList);
        });
    }

    @Override
    public void deleteImage(String path) {
        mExecutorService.execute(() -> {
            FileUtil.deleteFile(path);
            List<Element> elementList = LitePal.where("path = ?", path).find(Element.class);
            for (Element element : elementList) {
                String title = element.getTitle();
                List<Collection> collectionList = LitePal.where("title = ?", title).find(Collection.class);
                Collection oldCollection = collectionList.get(0);
                Collection collection = new Collection();
                collection.setTitle(title);
                int total = oldCollection.getTotal();
                if (total == 1) {
                    collection.setToDefault("total");
                } else {
                    collection.setTotal(total - 1);
                }
                collection.setCreateAt(System.currentTimeMillis());
                collection.updateAll();
            }
            LitePal.getDatabase().delete("Element", "path = ?", new String[]{path});
        });
    }

    /**
     * 将 UI 层的排序信息插入数据库
     */
    @Override
    public void insertSortInfo(int sortType, boolean ascendingOrder) {
        mExecutorService.execute(() -> {
            LitePal.deleteAll(SortInfo.class); // 删除所有记录，保证表中只有一条数据
            SortInfo sortInfo = new SortInfo();
            sortInfo.setSortType(String.valueOf(sortType));
            sortInfo.setAscendingOrder(String.valueOf(ascendingOrder));
            sortInfo.save();
        });
    }

    /**
     * 检索数据库排序信息
     *
     * @return 返回字符串数组，第一位---排序类型，第二位---是否升序
     */
    @Override
    public String[] querySortInfo() {
        SortInfo sortInfo = LitePal.findFirst(SortInfo.class);
        if (sortInfo == null) return null;
        return new String[]{sortInfo.getSortType(), sortInfo.getAscendingOrder()};
    }

    /**
     * 查询 App 本地目录缓存
     */
    private List<Image> getImage() {
        List<Image> imageList = new ArrayList<>();
        FileUtil.getLocalFiles(imageList, PATH, TYPE);
        return imageList;
    }
}
