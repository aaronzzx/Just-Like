package com.aaron.justlike.ui;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.widget.ImageView;

public class ViewWrapper {

    private ImageView target;

    public ViewWrapper(ImageView target) {
        this.target = target;
    }

    public void setSaturation(float saturation) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(saturation);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        target.setColorFilter(filter);
    }

    public void setAlpha(float alpha) {
        target.setAlpha(alpha);
    }
}
