package com.aaron.justlike.common.http.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.aaron.justlike.common.http.glide.request.BitmapRequest;
import com.aaron.justlike.common.http.glide.request.DrawableRequest;
import com.aaron.justlike.common.http.glide.request.FileRequest;
import com.aaron.justlike.common.http.glide.request.GifDrawableRequest;
import com.aaron.justlike.common.http.glide.request.Request;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.gif.GifDrawable;

import java.io.File;

public class GlideApp {

    private static GlideApp sGlideApp;
    private RequestManager mRequestManager;

    private GlideApp() {

    }

    public static GlideApp getInstance() {
        if (sGlideApp == null) {
            synchronized (GlideApp.class) {
                if (sGlideApp == null) {
                    sGlideApp = new GlideApp();
                }
            }
        }
        return sGlideApp;
    }

    public ResourceType with(Context context) {
        mRequestManager = Glide.with(context);
        return new ResourceType();
    }

    public class ResourceType {
        public Request<Drawable> asDrawable() {
            return new DrawableRequest(mRequestManager.asDrawable());
        }

        public Request<Bitmap> asBitmap() {
            return new BitmapRequest(mRequestManager.asBitmap());
        }

        public Request<GifDrawable> asGif() {
            return new GifDrawableRequest(mRequestManager.asGif());
        }

        public Request<File> asFile() {
            return new FileRequest(mRequestManager.asFile());
        }
    }
}
