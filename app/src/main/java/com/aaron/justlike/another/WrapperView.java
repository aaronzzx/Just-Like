package com.aaron.justlike.another;

import android.view.View;

public class WrapperView {

    private View mTarget;

    public WrapperView(View target) {
        mTarget = target;
    }

    public int getHeight() {
        return mTarget.getLayoutParams().height;
    }

    public void setHeight(float height) {
        mTarget.setTranslationY(height);
        mTarget.requestLayout();
    }
}
