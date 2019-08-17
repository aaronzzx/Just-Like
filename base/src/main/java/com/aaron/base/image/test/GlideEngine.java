package com.aaron.base.image.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;

/**
 * @author Aaron aaronzheng9603@gmail.com
 */
public class GlideEngine implements ImageEngine<ImageOption> {

    @Override
    public void init(Context context) {

    }

    @Override
    public void load(Context context, ImageOption option) {
        RequestManager manager = Glide.with(context);
        if (option.isAsFile()) {
            RequestBuilder<File> builder = setListener(manager.asFile(), option);
            request(builder, option);

        } else if (option.isAsGif()) {
            RequestBuilder<GifDrawable> builder = setListener(manager.asGif(), option);
            request(builder, option);

        } else if (option.isAsBitmap()) {
            int crossFadeDuration = option.getCrossFadeDuration();
            RequestBuilder<Bitmap> builder = setListener(manager.asBitmap(), option);
            request(builder.transition(BitmapTransitionOptions.withCrossFade(crossFadeDuration)), option);
        } else {
            int crossFadeDuration = option.getCrossFadeDuration();
            RequestBuilder<Drawable> builder = setListener(manager.asDrawable(), option);
            request(builder.transition(DrawableTransitionOptions.withCrossFade(crossFadeDuration)), option);
        }
    }

    private <T> RequestBuilder<T> setListener(RequestBuilder<T> builder, ImageOption option) {
        LoadListener listener = option.getListener();
        if (listener == null) {
            return builder;
        }
        return builder.listener(new RequestListener<T>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<T> target, boolean isFirstResource) {
                return listener.onFailure(e);
            }

            @Override
            public boolean onResourceReady(T resource, Object model, Target<T> target, DataSource dataSource, boolean isFirstResource) {
                return listener.onSuccess(resource);
            }
        });
    }

    private <T> void request(RequestBuilder<T> builder, ImageOption option) {
        String string = option.getString();
        Bitmap bitmap = option.getBitmap();
        File file = option.getFile();
        Drawable drawable = option.getDrawable();
        Uri uri = option.getUri();
        int drawableId = option.getDrawableId();
        int placeholderId = option.getPlaceholderId();
        ImageOption.ScaleType scaleType = option.getScaleType();

        if (string != null) {
            builder = builder.load(string);
        } else if (bitmap != null) {
            builder = builder.load(bitmap);
        } else if (file != null) {
            builder = builder.load(file);
        } else if (drawable != null) {
            builder = builder.load(drawable);
        } else if (uri != null) {
            builder = builder.load(uri);
        } else if (drawableId != 0) {
            builder = builder.load(drawableId);
        } else {
            throw new IllegalArgumentException("Parameter cannot be null.");
        }

        if (placeholderId != 0) {
            builder = builder.placeholder(placeholderId);
        }

        switch (scaleType) {
            case FIT_CENTER:    builder = builder.fitCenter(); break;
            case CENTER_INSIDE: builder = builder.centerInside(); break;
            case CENTER_CROP:   builder = builder.centerCrop(); break;
            case CIRCLE_CROP:   builder = builder.circleCrop(); break;
        }

        builder.into(option.getTarget());
    }
}
