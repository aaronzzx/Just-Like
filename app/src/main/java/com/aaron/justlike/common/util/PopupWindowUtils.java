package com.aaron.justlike.common.util;

import android.view.View;
import android.widget.PopupWindow;

import com.blankj.utilcode.util.ConvertUtils;

/**
 * @author Aaron aaronzheng9603@gmail.com
 */
public final class PopupWindowUtils {

    public static PopupWindow create(View content) {
        PopupWindow pw = new PopupWindow(content);
        pw.setElevation(ConvertUtils.dp2px(4));
//        pw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return pw;
    }

    private PopupWindowUtils() {}
}
