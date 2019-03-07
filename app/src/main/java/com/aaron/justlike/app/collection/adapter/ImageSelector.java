package com.aaron.justlike.app.collection.adapter;

import android.content.Context;
import android.content.Intent;

import com.aaron.justlike.app.collection.view.SelectorActivity;

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

    public void setTitle(String title) {
        mTitle = title;
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
