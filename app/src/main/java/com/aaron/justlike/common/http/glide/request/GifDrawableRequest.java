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
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class GifDrawableRequest implements Request<GifDrawable> {

    private RequestBuilder<GifDrawable> mRequestBuilder;

    public GifDrawableRequest(RequestBuilder<GifDrawable> requestBuilder) {
        mRequestBuilder = requestBuilder;
    }

    @Override
    public Request<GifDrawable> load(String args) {
        mRequestBuilder = mRequestBuilder.load(args);
        return this;
    }

    @Override
    public Request<GifDrawable> load(Drawable drawable) {
        mRequestBuilder = mRequestBuilder.load(drawable);
        return this;
    }

    @Override
    public Request<GifDrawable> load(int resourceId) {
        mRequestBuilder = mRequestBuilder.load(resourceId);
        return this;
    }

    @Override
    public Request<GifDrawable> load(Uri uri) {
        mRequestBuilder = mRequestBuilder.load(uri);
        return this;
    }

    @Override
    public Request<GifDrawable> placeHolder(int resourceId) {
        mRequestBuilder = mRequestBuilder.placeholder(resourceId);
        return this;
    }

    @Override
    public Request<GifDrawable> placeHolder(Drawable drawable) {
        mRequestBuilder = mRequestBuilder.placeholder(drawable);
        return this;
    }

    @Override
    public Request<GifDrawable> centerCrop() {
        mRequestBuilder = mRequestBuilder.centerCrop();
        return this;
    }

    @Override
    public Request<GifDrawable> thumbnail(String thumb) {
        mRequestBuilder = mRequestBuilder.thumbnail(Glide.with(JustLike.getContext()).asGif().load(thumb));
        return this;
    }

    @Override
    public Request<GifDrawable> thumbnail(float sizeMultiplier) {
        mRequestBuilder = mRequestBuilder.thumbnail(sizeMultiplier);
        return this;
    }

    @Override
    public Request<GifDrawable> transition(int duration) {
        mRequestBuilder = mRequestBuilder.transition(GenericTransitionOptions.with(R.anim.fade_in));
        return this;
    }

    @Override
    public Request<GifDrawable> listener(Listener<GifDrawable> listener) {
        mRequestBuilder = mRequestBuilder.listener(new RequestListener<GifDrawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                listener.onLoadFailed();
                return false;
            }

            @Override
            public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                listener.onResourceReady(resource, isFirstResource);
                return false;
            }
        });
        return this;
    }

    @Override
    public Request<GifDrawable> override(int width, int height) {
        mRequestBuilder = mRequestBuilder.override(width, height);
        return this;
    }

    @Override
    public void into(ImageView target) {
        mRequestBuilder.into(target);
    }
}
