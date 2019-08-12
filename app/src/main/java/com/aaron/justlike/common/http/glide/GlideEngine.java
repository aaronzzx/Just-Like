package com.aaron.justlike.common.http.glide;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import com.zhihu.matisse.engine.ImageEngine;

public class GlideEngine implements ImageEngine {

    @Override
    public void loadThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri) {
        GlideApp.getInstance()
                .with(context)
                .asBitmap()
                .load(uri)
                .placeHolder(placeholder)
                .centerCrop()
                .override(resize, resize)
                .into(imageView);

    }

    @Override
    public void loadGifThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri) {
        GlideApp.getInstance()
                .with(context)
                .asGif()
                .load(uri)
                .placeHolder(placeholder)
                .centerCrop()
                .override(resize, resize)
                .into(imageView);
    }


    @Override
    public void loadImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
        GlideApp.getInstance()
                .with(context)
                .asBitmap()
                .load(uri)
                .centerCrop()
                .override(resizeX, resizeY)
                .into(imageView);
    }

    @Override
    public void loadGifImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
        GlideApp.getInstance()
                .with(context)
                .asGif()
                .load(uri)
                .centerCrop()
                .override(resizeX, resizeY)
                .into(imageView);
    }

    @Override
    public boolean supportAnimatedGif() {
        return true;
    }
}
