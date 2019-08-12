package com.aaron.justlike.collection;

import java.util.List;

public interface ICollectionView {

    <E> void onShowImage(List<E> list);
}
