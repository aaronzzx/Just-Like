package com.aaron.justlike.common.http.glide.request;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

public interface Request<T> {

    Request<T> load(String args);

    Request<T> load(Drawable drawable);

    Request<T> load(int resourceId);

    Request<T> load(Uri uri);

    Request<T> placeHolder(int resourceId);

    Request<T> placeHolder(Drawable drawable);

    Request<T> centerCrop();

    Request<T> thumbnail(String thumb);

    Request<T> thumbnail(float sizeMultiplier);

    Request<T> transition(int duration);

    Request<T> listener(Listener<T> listener);

    Request<T> override(int width, int height);

    void into(ImageView target);

    interface Listener<T> {

        void onLoadFailed();

        void onResourceReady(T resource, boolean isFirstResource);
    }
}
