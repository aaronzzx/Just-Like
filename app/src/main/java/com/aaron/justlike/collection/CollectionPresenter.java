package com.aaron.justlike.collection;

import com.aaron.justlike.common.bean.Album;

import java.util.Collections;
import java.util.List;

class CollectionPresenter implements ICollectionContract.P {

    private ICollectionContract.V mView;
    private ICollectionContract.M<Album> mModel;

    CollectionPresenter(ICollectionContract.V view) {
        mView = view;
        mModel = new CollectionModel();
    }

    @Override
    public void detachView() {
        mView = null;
        mModel = null;
    }

    @Override
    public boolean isCollectionExists(String name) {
        return mModel.isCollectionExists(name);
    }

    @Override
    public void requestCollection(List<Album> albums) {
        mModel.queryCollection(new ICollectionContract.M.Callback<Album>() {
            @Override
            public void onResponse(List<Album> list) {
                if (albums.size() != list.size() || !albums.containsAll(list)) {
                    Collections.sort(list, (o1, o2) -> (int) (o2.getCreateAt() - o1.getCreateAt()));
                    mView.onShowImage(list);
                }
            }
        });
    }

    @Override
    public int saveCollection(List<String> list, String title) {
        mModel.insertCollection(list, title, new ICollectionContract.M.Callback<Album>() {
            @Override
            public void onFinish() {
            }
        });
        return 1;
    }

    @Override
    public void deleteCollection(String title) {
        mModel.deleteCollection(title);
    }
}
