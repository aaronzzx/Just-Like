package com.aaron.ui.util;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;

import com.aaron.ui.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class DialogUtil {

    public static Dialog createDialog(Context context, int layoutId) {
        Dialog dialog = new Dialog(context);
        Window window = dialog.getWindow();
        if (window != null) window.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(layoutId);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    public static Dialog createDialog(Context context, View view) {
        Dialog dialog = new Dialog(context);
        Window window = dialog.getWindow();
        if (window != null) window.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(view);
        dialog.setCancelable(true);
//        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    public static BottomSheetDialog createBottomSheetDialog(Context context, View view, boolean transparentBg) {
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        dialog.setContentView(view);
        View rootView = dialog.getDelegate().findViewById(R.id.design_bottom_sheet);
        if (rootView != null) {
            BottomSheetBehavior behavior = BottomSheetBehavior.from(rootView);
            behavior.setHideable(false);
        }
        Window window = dialog.getWindow();
        if (window != null) {
            window.setWindowAnimations(R.style.UIBottomDialogAnim);
            if (transparentBg) {
                window.findViewById(R.id.design_bottom_sheet)
                        .setBackgroundResource(android.R.color.transparent);
            }
        }
        return dialog;
    }
}
