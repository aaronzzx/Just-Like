package com.aaron.base.image.test;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;

import java.io.File;

/**
 * @author Aaron aaronzheng9603@gmail.com
 */
public abstract class ImageOption {

    protected String mString;
    protected Bitmap mBitmap;
    protected File mFile;
    protected Drawable mDrawable;
    protected Uri mUri;
    protected int mDrawableId;

    protected boolean mAsDrawable;
    protected boolean mAsBitmap;
    protected boolean mAsFile;
    protected boolean mAsGif;

    protected int mPlaceholderId;
    protected int mCrossFadeDuration;

    protected ScaleType mScaleType;

    protected LoadListener mListener;

    protected ImageView mTarget;

    protected ImageOption(Builder builder) {
        this.mString = builder.string;
        this.mBitmap = builder.bitmap;
        this.mFile = builder.file;
        this.mDrawable = builder.drawable;
        this.mUri = builder.uri;
        this.mDrawableId = builder.drawableId;

        this.mAsDrawable = builder.asDrawable;
        this.mAsBitmap = builder.asBitmap;
        this.mAsFile = builder.asFile;
        this.mAsGif = builder.asGif;

        this.mPlaceholderId = builder.placeholderId;
        this.mCrossFadeDuration = builder.crossFadeDuration;

        this.mScaleType = builder.scaleType;

        this.mListener = builder.listener;

        this.mTarget = builder.target;
    }

    public String getString() {
        return mString;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public File getFile() {
        return mFile;
    }

    public Drawable getDrawable() {
        return mDrawable;
    }

    public Uri getUri() {
        return mUri;
    }

    public int getDrawableId() {
        return mDrawableId;
    }

    public boolean isAsDrawable() {
        return mAsDrawable;
    }

    public boolean isAsBitmap() {
        return mAsBitmap;
    }

    public boolean isAsFile() {
        return mAsFile;
    }

    public boolean isAsGif() {
        return mAsGif;
    }

    public int getPlaceholderId() {
        return mPlaceholderId;
    }

    public int getCrossFadeDuration() {
        return mCrossFadeDuration;
    }

    public ScaleType getScaleType() {
        return mScaleType;
    }

    public LoadListener getListener() {
        return mListener;
    }

    public ImageView getTarget() {
        return mTarget;
    }

    public abstract static class Builder<T extends Builder> {
        protected String string;
        protected Bitmap bitmap;
        protected File file;
        protected Drawable drawable;
        protected Uri uri;
        protected int drawableId;

        protected boolean asDrawable;
        protected boolean asBitmap;
        protected boolean asFile;
        protected boolean asGif;

        protected int placeholderId;
        protected int crossFadeDuration;

        protected ScaleType scaleType;

        protected LoadListener listener;

        protected ImageView target;

        public Builder(String string) {
            this.string = string;
        }

        public Builder(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        public Builder(File file) {
            this.file = file;
        }

        public Builder(Drawable drawable) {
            this.drawable = drawable;
        }

        public Builder(Uri uri) {
            this.uri = uri;
        }

        public Builder(@DrawableRes int drawableId) {
            this.drawableId = drawableId;
        }

        public T asDrawable() {
            this.asDrawable = true;
            return actualBuilder();
        }

        public T asBitmap() {
            this.asBitmap = true;
            return actualBuilder();
        }

        public T asFile() {
            this.asFile = true;
            return actualBuilder();
        }

        public T asGif() {
            this.asGif = true;
            return actualBuilder();
        }

        public T placeholder(@DrawableRes int drawableId) {
            this.placeholderId = drawableId;
            return actualBuilder();
        }

        public T crossFade(int duration) {
            this.crossFadeDuration = duration;
            return actualBuilder();
        }

        public T scaleType(ScaleType scaleType) {
            this.scaleType = scaleType;
            return actualBuilder();
        }

        public T addListener(LoadListener listener) {
            this.listener = listener;
            return actualBuilder();
        }

        public ImageOption into(ImageView target) {
            this.target = target;
            return createOptionInstance();
        }

        protected abstract T actualBuilder();

        protected abstract ImageOption createOptionInstance();
    }
}
