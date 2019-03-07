package com.aaron.justlike.module.collection.view;

import java.util.List;

public interface IElementView<T> {

    void onShowImage(List<T> list);

    void onShowAddImage(List<T> list);
}
