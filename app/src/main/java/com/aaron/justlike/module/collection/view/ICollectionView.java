package com.aaron.justlike.module.collection.view;

import java.util.List;

public interface ICollectionView {

    <E> void onShowImage(List<E> list);
}
