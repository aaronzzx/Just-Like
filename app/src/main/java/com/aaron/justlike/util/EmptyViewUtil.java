package com.aaron.justlike.util;

import android.view.View;

public class EmptyViewUtil {

    public static void showEmptyView(View emptyView) {
        emptyView.setVisibility(View.VISIBLE);
    }

    public static void hideEmptyView(View emptyView) {
        emptyView.setVisibility(View.GONE);
    }
}
