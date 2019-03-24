package com.aaron.justlike.library.glide.request;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import com.aaron.justlike.JustLike;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import androidx.annotation.Nullable;

public class BitmapRequest implements Request<Bitmap> {

    private RequestBuilder<Bitmap> mRequestBuilder;

    public BitmapRequest(RequestBuilder<Bitmap> requestBuilder) {
        mRequestBuilder = requestBuilder;
    }

    @Override
    public Request<Bitmap> load(String args) {
        mRequestBuilder = mRequestBuilder.load(args);
        return this;
    }

    @Override
    public Request<Bitmap> load(Drawable drawable) {
        mRequestBuilder = mRequestBuilder.load(drawable);
        return this;
    }

    @Override
    public Request<Bitmap> load(int resourceId) {
        mRequestBuilder = mRequestBuilder.load(resourceId);
        return this;
    }

    @Override
    public Request<Bitmap> load(Uri uri) {
        mRequestBuilder = mRequestBuilder.load(uri);
        return this;
    }

    @Override
    public Request<Bitmap> placeHolder(int resourceId) {
        mRequestBuilder = mRequestBuilder.placeholder(resourceId);
        return this;
    }

    @Override
    public Request<Bitmap> placeHolder(Drawable drawable) {
        mRequestBuilder = mRequestBuilder.placeholder(drawable);
        return this;
    }

    @Override
    public Request<Bitmap> centerCrop() {
        mRequestBuilder = mRequestBuilder.centerCrop();
        return this;
    }

    @Override
    public Request<Bitmap> thumbnail(String thumb) {
        mRequestBuilder = mRequestBuilder.thumbnail(Glide.with(JustLike.getContext()).asBitmap().load(thumb));
        return this;
    }

    @Override
    public Request<Bitmap> transition(int duration) {
        mRequestBuilder = mRequestBuilder.transition(BitmapTransitionOptions.withCrossFade(duration));
        return this;
    }

    @Override
    public Request<Bitmap> listener(Listener<Bitmap> listener) {
        mRequestBuilder = mRequestBuilder.listener(new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                listener.onLoadFailed();
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                listener.onResourceReady(resource);
                return false;
            }
        });
        return this;
    }

    @Override
    public Request<Bitmap> override(int width, int height) {
        mRequestBuilder = mRequestBuilder.override(width, height);
        return this;
    }

    @Override
    public void into(ImageView target) {
        mRequestBuilder.into(target);
    }
}
