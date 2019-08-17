package com.aaron.base.image.test;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import java.io.File;

/**
 * @author Aaron aaronzheng9603@gmail.com
 */
public class DefaultOption extends ImageOption {

    protected DefaultOption(Builder builder) {
        super(builder);
    }

    public static class Builder extends ImageOption.Builder {

        public Builder(String string) {
            super(string);
        }

        public Builder(Bitmap bitmap) {
            super(bitmap);
        }

        public Builder(File file) {
            super(file);
        }

        public Builder(Drawable drawable) {
            super(drawable);
        }

        public Builder(Uri uri) {
            super(uri);
        }

        public Builder(int drawableId) {
            super(drawableId);
        }

        @Override
        protected ImageOption.Builder actualBuilder() {
            return this;
        }

        @Override
        protected ImageOption createOptionInstance() {
            return new DefaultOption(this);
        }
    }
}
