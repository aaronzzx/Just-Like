package com.aaron.justlike.common.glide;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;

import com.aaron.justlike.R;
import com.aaron.justlike.ui.ViewWrapper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import androidx.annotation.Nullable;

public class GlideApp {

    public static void loadImage(Context context, String path, int placeHolder, ImageView imageView) {
        Glide.with(context)
                .load(path)
                .placeholder(placeHolder)
                .into(imageView);
    }

    public static void loadImageByFade(Context context, String path, int placeHolder, ImageView imageView) {
        Glide.with(context)
                .load(path)
                .placeholder(placeHolder)
                .transition(DrawableTransitionOptions.withCrossFade(100))
                .into(imageView);
    }

    public static void loadImageByThumb(Context context, String urls, String thumb, ImageView imageView, RequestListener<Drawable> listener) {
        Glide.with(context)
                .load(urls)
                .thumbnail(Glide.with(context).load(thumb))
                .listener(listener)
                .into(imageView);
    }

    public static void loadImageBySaturation(Context context, String urls, Drawable placeHolder, ImageView imageView) {
        Animator animator = AnimatorInflater.loadAnimator(context, R.animator.image_saturation);
        animator.setTarget(new ViewWrapper(imageView));
        animator.setInterpolator(new AccelerateInterpolator());
        Glide.with(context)
                .load(urls)
                .placeholder(placeHolder)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
        animator.start();
    }

    public static void loadImageByColorScale(Context context, String path, int placeHolder, ImageView imageView) {
        Glide.with(context)
                .load(path)
                .placeholder(placeHolder)
                .transition(DrawableTransitionOptions.withCrossFade(100))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
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
                .transition(DrawableTransitionOptions.withCrossFade(100))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
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

    public static void loadImageNoPlaceHolder(Context context, String path, ImageView imageView) {
        Glide.with(context)
                .load(path)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
    }
}
