package com.aaron.justlike.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.aaron.justlike.R;
import com.aaron.justlike.activity.DisplayImageActivity;
import com.aaron.justlike.another.WrapperView;
import com.aaron.justlike.util.AnimationUtil;
import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerAdapter;

public class MyPagerAdapter extends PagerAdapter {

    private List<String> mPathList;
    private DisplayImageActivity mActivity;
    private boolean isFullScreen;

    public MyPagerAdapter(DisplayImageActivity activity, List<String> pathList) {
        mActivity = activity;
        mPathList = pathList;
    }

    @Override
    public int getCount() {
        return mPathList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container,int position) {
        final Toolbar toolbar = mActivity.findViewById(R.id.activity_display_image_toolbar);
        final WrapperView view = new WrapperView(toolbar);
        String path = mPathList.get(position);
        final PhotoView photoView = new PhotoView(mActivity);
        photoView.enable();
        photoView.setMaxScale(2.5F);
        ViewGroup parent = (ViewGroup) photoView.getParent();
        if (parent != null) {
            parent.removeView(photoView);
        }
        RequestOptions options = new RequestOptions().override(1440);
        DrawableCrossFadeFactory factory = new DrawableCrossFadeFactory
                .Builder(100)
                .setCrossFadeEnabled(true).build();
        Glide.with(mActivity)
                .load(path)
                .apply(options)
                .transition(DrawableTransitionOptions.with(factory))
                .into(photoView);
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFullScreen) {
                    // 全屏状态下执行此代码块会退出全屏
                    AnimationUtil.exitFullScreen(mActivity, toolbar, view);
                    isFullScreen = false;
                } else {
                    // 进入全屏,自动沉浸
                    AnimationUtil.setFullScreen(mActivity, toolbar, view);
                    isFullScreen = true;
                }
            }
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
        int currentPosition = mActivity.getCurrentPosition();
        if (currentPosition == (Integer) view.getTag()) {
            return POSITION_NONE;
        } else {
            return POSITION_UNCHANGED;
        }
    }
}
