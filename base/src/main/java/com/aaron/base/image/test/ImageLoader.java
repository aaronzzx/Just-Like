package com.aaron.base.image.test;

import android.content.Context;

/**
 * @author Aaron aaronzheng9603@gmail.com
 */
public final class ImageLoader implements ILoader {

    private ImageEngine mImageEngine;

    public static ILoader get() {
        return Holder.INSTANCE;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ImageOption> void load(Context context, T config) {
        if (mImageEngine == null) {
            mImageEngine = new GlideEngine();
        }
        mImageEngine.load(context, config);
    }

    @Override
    public void setImageEngine(ImageEngine engine) {
        mImageEngine = engine;
    }

    @Override
    public ImageEngine getImageEngine() {
        return mImageEngine;
    }

    private ImageLoader() {}

    private static class Holder {
        private static final ImageLoader INSTANCE = new ImageLoader();
    }
}
