package com.aaron.base.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.util.concurrent.ExecutionException;

/**
 * @author Aaron aaronzheng9603@gmail.com
 */
public final class ImageLoader {

    public static File download(Context context, String url) throws ExecutionException, InterruptedException {
        return Glide.with(context)
                .downloadOnly()
                .load(url)
                .submit()
                .get();
    }

    public static Bitmap getBitmapByUrl(Context context, String url) throws ExecutionException, InterruptedException {
        return Glide.with(context)
                .asBitmap()
                .load(url)
                .submit()
                .get();
    }

    public static void loadWithPlaceholder(Context context, String string, @DrawableRes int placeholder, ImageView target) {
        Glide.with(context)
                .load(string)
                .placeholder(placeholder)
                .into(target);
    }

    public static void loadWithPlaceholder(Context context, String string, Drawable drawable, ImageView target) {
        Glide.with(context)
                .load(string)
                .placeholder(drawable)
                .into(target);
    }

    public static void loadWithoutAnim(Context context, String string, ImageView target) {
        Glide.with(context)
                .load(string)
                .into(target);
    }

    public static void loadWithoutAnim(Context context, Uri uri, ImageView target) {
        Glide.with(context)
                .load(uri)
                .into(target);
    }

    public static void loadWithAnim(Context context, String string, ImageView target) {
        Glide.with(context)
                .load(string)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(target);
    }

    public static void loadWithAnim(Context context, String string, ImageView target, LoadCallback<Drawable> callback) {
        Glide.with(context)
                .load(string)
                .transition(DrawableTransitionOptions.withCrossFade())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        callback.onFailure(e);
                        return false; // return true 不会 into
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        callback.onSuccess(resource);
                        return false; // return true 不会 into
                    }
                })
                .into(target);
    }

    private ImageLoader() {

    }

    public interface LoadCallback<T> {
        void onSuccess(T result);

        void onFailure(Throwable e);
    }
}
