package com.aaron.justlike.common.manager;

import androidx.annotation.StringRes;

import com.blankj.utilcode.util.ToastUtils;

/**
 * @author Aaron aaronzheng9603@gmail.com
 */
public final class UiManager {

    private UiManager() {

    }

    public static void showShort(@StringRes int stringId) {
        ToastUtils.showShort(stringId);
    }

    public static void showShort(CharSequence text) {
        ToastUtils.showShort(text);
    }

    public static void showLong(@StringRes int stringId) {
        ToastUtils.showLong(stringId);
    }

    public static void showLong(CharSequence text) {
        ToastUtils.showLong(text);
    }
}
