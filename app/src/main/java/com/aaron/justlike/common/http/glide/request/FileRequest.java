package com.aaron.justlike.common.http.glide.request;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.aaron.justlike.R;
import com.aaron.justlike.common.JustLike;
import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;

public class FileRequest implements Request<File> {

    private RequestBuilder<File> mRequestBuilder;

    public FileRequest(RequestBuilder<File> requestBuilder) {
        mRequestBuilder = requestBuilder;
    }

    @Override
    public Request<File> load(String args) {
        mRequestBuilder = mRequestBuilder.load(args);
        return this;
    }

    @Override
    public Request<File> load(Drawable drawable) {
        mRequestBuilder = mRequestBuilder.load(drawable);
        return this;
    }

    @Override
    public Request<File> load(int resourceId) {
        mRequestBuilder = mRequestBuilder.load(resourceId);
        return this;
    }

    @Override
    public Request<File> load(Uri uri) {
        mRequestBuilder = mRequestBuilder.load(uri);
        return this;
    }

    @Override
    public Request<File> placeHolder(int resourceId) {
        mRequestBuilder = mRequestBuilder.placeholder(resourceId);
        return this;
    }

    @Override
    public Request<File> placeHolder(Drawable drawable) {
        mRequestBuilder = mRequestBuilder.placeholder(drawable);
        return this;
    }

    @Override
    public Request<File> centerCrop() {
        mRequestBuilder = mRequestBuilder.centerCrop();
        return this;
    }

    @Override
    public Request<File> thumbnail(String thumb) {
        mRequestBuilder = mRequestBuilder.thumbnail(Glide.with(JustLike.getContext()).asFile().load(thumb));
        return this;
    }

    @Override
    public Request<File> transition(int duration) {
        mRequestBuilder = mRequestBuilder.transition(GenericTransitionOptions.with(R.anim.fade_in));
        return this;
    }

    @Override
    public Request<File> thumbnail(float sizeMultiplier) {
        mRequestBuilder = mRequestBuilder.thumbnail(sizeMultiplier);
        return this;
    }

    @Override
    public Request<File> listener(Listener<File> listener) {
        mRequestBuilder = mRequestBuilder.listener(new RequestListener<File>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                listener.onLoadFailed();
                return false;
            }

            @Override
            public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
                listener.onResourceReady(resource, isFirstResource);
                return false;
            }
        });
        return this;
    }

    @Override
    public Request<File> override(int width, int height) {
        mRequestBuilder = mRequestBuilder.override(width, height);
        return this;
    }

    @Override
    public void into(ImageView target) {
        mRequestBuilder.into(target);
    }
}
