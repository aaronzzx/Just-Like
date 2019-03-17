package com.aaron.justlike.mvp.view.collection;

import java.util.List;

public interface ICollectionView {

    <E> void onShowImage(List<E> list);
}
