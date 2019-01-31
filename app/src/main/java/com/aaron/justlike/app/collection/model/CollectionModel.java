package com.aaron.justlike.app.collection.model;

import com.aaron.justlike.app.collection.entity.Album;
import com.aaron.justlike.app.collection.entity.Collection;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class CollectionModel implements ICollectionModel<Album> {

    @Override
    public void queryCollection(Callback<Album> callback) {
        LitePal.getDatabase();
        List<Album> albumList = new ArrayList<>();
        // 取出集合的名称和元素的数量和封面图
        List<Collection> collections = LitePal.findAll(Collection.class);
        if (collections == null) {
            callback.onResponse(null);
            return;
        }
        for (Collection info : collections) {
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
    }
}
