package com.aaron.justlike.custom.image_selector;

import android.content.Context;
import android.content.Intent;

import java.lang.ref.WeakReference;
import java.util.List;

public class ImageSelector {

    private static WeakReference<Context> mWeakReference;
    private String mTitle;
    private String mFilePath;
    private List<String> mSelectedList;
    private ImageCallback mCallback;

    private ImageSelector() {

    }

    public static ImageSelector getInstance(Context context) {
        mWeakReference = new WeakReference<>(context);
        return Holder.INSTANCE;
    }

    public String getTitle() {
        return mTitle;
    }

    public ImageSelector setTitle(String title) {
        mTitle = title;
        return Holder.INSTANCE;
    }

    public ImageSelector setFilePath(String path) {
        mFilePath = path;
        return Holder.INSTANCE;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public ImageSelector setSelectedImage(List<String> selectedList) {
        mSelectedList = selectedList;
        return Holder.INSTANCE;
    }

    public List<String> getSelectedImage() {
        return mSelectedList;
    }

    public ImageCallback getCallback() {
        return mCallback;
    }

    public ImageSelector setCallback(ImageCallback callback) {
        mCallback = callback;
        return Holder.INSTANCE;
    }

    public void start() {
        if (mWeakReference.get() != null) {
            Context context = mWeakReference.get();
            context.startActivity(new Intent(context, SelectorActivity.class));
        }
    }

    private static class Holder {

        private static final ImageSelector INSTANCE = new ImageSelector();
    }

    public interface ImageCallback {

        default void onResponse(List<String> response) {
        }

        default void onResponse(List<String> response, String title) {
        }
    }
}
