package com.aaron.justlike.common.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

/**
 * @author Aaron aaronzheng9603@gmail.com
 */
public final class SelectorUtils {

    public static Drawable createCheckedSelector(Context context, Drawable normal, Drawable checked) {
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[]{android.R.attr.state_checked}, checked); // 状态，设置选中的图片
        drawable.addState(new int[]{}, normal); // 默认状态,默认状态下的图片
        return drawable;
    }

    private SelectorUtils() {}
}
