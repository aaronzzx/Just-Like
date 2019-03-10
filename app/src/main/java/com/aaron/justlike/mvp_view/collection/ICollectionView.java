package com.aaron.justlike.mvp_view.collection;

import java.util.List;

public interface ICollectionView {

    <E> void onShowImage(List<E> list);
}
