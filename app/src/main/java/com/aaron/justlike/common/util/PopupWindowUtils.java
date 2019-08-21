package com.aaron.justlike.common.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

/**
 * @author Aaron aaronzheng9603@gmail.com
 */
public final class PopupWindowUtils {

    public static PopupWindow create(Context context, View content) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return new PopupWindow(content);
    }

    private PopupWindowUtils() {}
}
