package com.aaron.ui.util;

import android.app.Activity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.lang.reflect.Method;

public class KeyboardUtil {
    
    private KeyboardUtil() {}

    public static void showSoftInput(View view) {
        com.blankj.utilcode.util.KeyboardUtils.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }
    
    public static void hideSoftInput(View view) {
        com.blankj.utilcode.util.KeyboardUtils.hideSoftInput(view);
    }

    public static void setSoftInputEnabled(Activity activity, EditText et, boolean enable) {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        try {
            Class<EditText> cls = EditText.class;
            Method setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
            setShowSoftInputOnFocus.setAccessible(true);
            setShowSoftInputOnFocus.invoke(et, enable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
