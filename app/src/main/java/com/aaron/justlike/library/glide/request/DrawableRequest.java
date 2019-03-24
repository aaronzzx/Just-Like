package com.aaron.justlike.library.glide.request;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import com.aaron.justlike.JustLike;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import androidx.annotation.Nullable;

public class DrawableRequest implements Request<Drawable> {

    private RequestBuilder<Drawable> mRequestBuilder;

    public DrawableRequest(RequestBuilder<Drawable> requestBuilder) {
        mRequestBuilder = requestBuilder;
    }

    @Override
    public Request<Drawable> load(String args) {
        mRequestBuilder = mRequestBuilder.load(args);
        return this;
    }

    @Override
    public Request<Drawable> load(Drawable drawable) {
        mRequestBuilder = mRequestBuilder.load(drawable);
        return this;
    }

    @Override
    public Request<Drawable> load(int resourceId) {
        mRequestBuilder = mRequestBuilder.load(resourceId);
        return this;
    }

    @Override
    public Request<Drawable> load(Uri uri) {
        mRequestBuilder = mRequestBuilder.load(uri);
        return this;
    }

    @Override
    public Request<Drawable> placeHolder(int resourceId) {
        mRequestBuilder = mRequestBuilder.placeholder(resourceId);
        return this;
    }

    @Override
    public Request<Drawable> placeHolder(Drawable drawable) {
        mRequestBuilder = mRequestBuilder.placeholder(drawable);
        return this;
    }

    @Override
    public Request<Drawable> centerCrop() {
        mRequestBuilder = mRequestBuilder.centerCrop();
        return this;
    }

    @Override
    public Request<Drawable> thumbnail(String thumb) {
        mRequestBuilder = mRequestBuilder.thumbnail(Glide.with(JustLike.getContext()).load(thumb));
        return this;
    }

    @Override
    public Request<Drawable> transition(int duration) {
        mRequestBuilder = mRequestBuilder.transition(DrawableTransitionOptions.withCrossFade(duration));
        return this;
    }

    @Override
    public Request<Drawable> listener(Listener<Drawable> listener) {
        mRequestBuilder = mRequestBuilder.listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                listener.onLoadFailed();
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                listener.onResourceReady(resource);
                return false;
            }
        });
        return this;
    }

    @Override
    public Request<Drawable> override(int width, int height) {
        mRequestBuilder = mRequestBuilder.override(width, height);
        return this;
    }

    @Override
    public void into(ImageView target) {
        mRequestBuilder.into(target);
    }
}
