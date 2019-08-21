package com.aaron.justlike.common.manager;

import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.StringRes;

import com.aaron.justlike.R;
import com.blankj.utilcode.util.ToastUtils;

/**
 * @author Aaron aaronzheng9603@gmail.com
 */
public final class UiManager {

    public static void showShort(CharSequence text) {
        ToastUtils.setGravity(Gravity.BOTTOM, 0, 150);
        View toastView = ToastUtils.showCustomShort(R.layout.app_toast);
        TextView tv = toastView.findViewById(R.id.app_tv);
        tv.setText(text);
    }

    public static void showShort(@StringRes int strResId) {
        ToastUtils.setGravity(Gravity.BOTTOM, 0, -1000);
        View toastView = ToastUtils.showCustomShort(R.layout.app_toast);
        TextView tv = toastView.findViewById(R.id.app_tv);
        tv.setText(strResId);
    }

    public static void showShortCenter(@StringRes int strResId) {
        ToastUtils.setGravity(Gravity.CENTER, 0, 0);
        View toastView = ToastUtils.showCustomShort(R.layout.app_toast);
        TextView tv = toastView.findViewById(R.id.app_tv);
        tv.setText(strResId);
    }

    public static void showLong(CharSequence text) {
        ToastUtils.setGravity(Gravity.BOTTOM, 0, -1000);
        View toastView = ToastUtils.showCustomLong(R.layout.app_toast);
        TextView tv = toastView.findViewById(R.id.app_tv);
        tv.setText(text);
    }

    public static void showLong(@StringRes int strResId) {
        ToastUtils.setGravity(Gravity.BOTTOM, 0, -1000);
        View toastView = ToastUtils.showCustomLong(R.layout.app_toast);
        TextView tv = toastView.findViewById(R.id.app_tv);
        tv.setText(strResId);
    }

    private UiManager() {

    }
}
