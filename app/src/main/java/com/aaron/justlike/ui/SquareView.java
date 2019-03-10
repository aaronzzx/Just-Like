package com.aaron.justlike.ui;

import android.content.Context;
import android.util.AttributeSet;

import com.bm.library.PhotoView;

public class SquareView extends PhotoView {

    public SquareView(Context context) {
        super(context);
    }

    public SquareView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 将原本的测量宽高改为只测量宽度，从而使 View 变成正方形。
     *
     * @param widthMeasureSpec   宽
     * @param heightMeasureSpec  高
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //noinspection SuspiciousNameCombination
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
