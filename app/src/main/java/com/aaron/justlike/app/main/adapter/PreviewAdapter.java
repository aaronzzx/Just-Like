package com.aaron.justlike.app.main.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aaron.justlike.app.main.entity.Image;
import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class PreviewAdapter<T> extends PagerAdapter {

    private List<T> mList;
    private Callback mCallback;
    private int mPosition;

    public PreviewAdapter(List<T> list, Callback callback) {
        mList = list;
        mCallback = callback;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Context context = container.getContext();
        mPosition = position;
        String path = ((Image) mList.get(position)).getPath();
        // 设置 PhotoView
        PhotoView photoView = new PhotoView(context);
        photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        photoView.enable();
        photoView.setMaxScale(2.5F);
        ViewGroup parent = (ViewGroup) photoView.getParent();
        if (parent != null) {
            parent.removeView(photoView);
        }

        RequestOptions options = new RequestOptions()
                .override(3000, 3000)
                .centerInside();
        DrawableCrossFadeFactory factory = new DrawableCrossFadeFactory
                .Builder(300)
                .setCrossFadeEnabled(true)
                .build();
        Glide.with(context)
                .load(path)
                .apply(options)
                .transition(DrawableTransitionOptions.with(factory))
                .into(photoView);
        photoView.setOnClickListener(v -> {
            mCallback.onPress();
        });
        container.addView(photoView);
        return photoView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        View view = (View) object;
        if (mPosition == (Integer) view.getTag()) {
            return POSITION_NONE;
        } else {
            return POSITION_UNCHANGED;
        }
    }

    public interface Callback {

        void onPress();
    }
}
