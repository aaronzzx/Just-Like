package com.aaron.justlike.common.glide;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class GlideApp {

    public static void loadImage(Context context, String path, ImageView imageView) {
        Glide.with(context)
                .load(path)
                .into(imageView);
    }

    public static void loadImage(Context context, String path, int placeHolder, ImageView imageView) {
        Glide.with(context)
                .load(path)
                .dontAnimate()
                .placeholder(placeHolder)
                .into(imageView);
    }

    public static void loadImageByAnimation(Context context, String urls, Drawable placeHolder, ImageView imageView) {
        Glide.with(context)
                .load(urls)
                .placeholder(placeHolder)
//                .listener(new RequestListener<String, GlideDrawable>() {
//                    @Override
//                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                        imageView.setImageDrawable(resource);
//                        ObjectAnimator animator = ObjectAnimator.ofFloat(new ViewWrapper(imageView), "saturation", 0, 1)
//                                .setDuration(2000);
//                        animator.start();
//                        return false;
//                    }
//                })
                .into(imageView);
    }

    public static void loadImageByColorScale(Context context, String path, int placeHolder, ImageView imageView) {
        Glide.with(context)
                .load(path)
                .placeholder(placeHolder)
                .dontAnimate()
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        ColorMatrix matrix = new ColorMatrix();
                        matrix.setScale(0.7F, 0.7F, 0.7F, 1);
                        resource.setColorFilter(new ColorMatrixColorFilter(matrix));
                        imageView.setImageDrawable(resource);
                        return false;
                    }
                })
                .into(imageView);
    }

    public static void loadImageByColorScale(Context context, int drawable, int placeHolder, ImageView imageView) {
        Glide.with(context)
                .load(drawable)
                .placeholder(placeHolder)
                .dontAnimate()
                .listener(new RequestListener<Integer, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, Integer model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, Integer model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        ColorMatrix matrix = new ColorMatrix();
                        matrix.setScale(0.7F, 0.7F, 0.7F, 1);
                        resource.setColorFilter(new ColorMatrixColorFilter(matrix));
                        imageView.setImageDrawable(resource);
                        return false;
                    }
                })
                .into(imageView);
    }

    public static void loadImageByOverride(Context context, String path, int[] override, ImageView imageView) {
        Glide.with(context)
                .load(path)
                .override(override[0], override[1])
                .into(imageView);
    }
}
