package com.aaron.justlike.collection;

import com.aaron.justlike.common.bean.Album;
import com.aaron.justlike.common.bean.Collection;
import com.aaron.justlike.common.bean.Element;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CollectionModel implements ICollectionContract.M<Album> {

    private ExecutorService mService;

    public CollectionModel() {
        mService = Executors.newSingleThreadExecutor();
    }

    @Override
    public void queryCollection(Callback<Album> callback) {
        mService.execute(() -> {
            List<Album> albumList = new ArrayList<>();
            // 取出集合的名称和元素的数量和封面图
            List<Collection> collections = LitePal.findAll(Collection.class);
            if (collections == null) {
                callback.onResponse(null);
                return;
            }
            for (Collection info : collections) {
                if (info.getTotal() == 0) {
                    deleteCollection(info.getTitle());
                    continue;
                }
                Album album = new Album();
                String title = info.getTitle();
                String total = String.valueOf(info.getTotal());
                String path = info.getPath();
                long createAt = info.getCreateAt();
                album.setCollectionTitle(title);
                album.setElementTotal(total);
                album.setImagePath(path);
                album.setCreateAt(createAt);
                albumList.add(album);
            }
            callback.onResponse(albumList);
        });
    }

    @Override
    public void insertCollection(List<String> list, String title, Callback<Album> callback) {
        mService.execute(() -> {
            Collection info = new Collection();
            info.setTitle(title);
            info.setTotal(list.size());
            info.setPath(list.get(list.size() - 1));
            info.setCreateAt(System.currentTimeMillis());
            info.save();

            for (String path : list) {
                Element element = new Element();
                element.setTitle(title);
                element.setPath(path);
                element.setCreateAt(System.currentTimeMillis());
                element.save();
            }
            try {
                Thread.sleep(600);
                callback.onFinish();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void deleteCollection(String title) {
        mService.execute(() -> {
            LitePal.deleteAll(Collection.class, "title = ?", title);
            LitePal.deleteAll(Element.class, "title = ?", title);
        });
    }
}
