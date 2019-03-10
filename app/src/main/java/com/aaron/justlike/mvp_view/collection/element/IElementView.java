package com.aaron.justlike.mvp_view.collection.element;

import java.util.List;

public interface IElementView<T> {

    void onShowImage(List<T> list);

    void onShowAddImage(List<T> list);
}
