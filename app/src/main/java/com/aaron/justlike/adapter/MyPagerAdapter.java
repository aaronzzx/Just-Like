package com.aaron.justlike.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

import com.aaron.justlike.R;
import com.aaron.justlike.activity.DisplayImageActivity;
import com.aaron.justlike.another.WrapperView;
import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerAdapter;

public class MyPagerAdapter extends PagerAdapter {

    private List<String> mPathList;
    private DisplayImageActivity mActivity;
    private boolean isFullScreen;

    public MyPagerAdapter(List<String> pathList, DisplayImageActivity activity) {
        mPathList = pathList;
        mActivity = activity;
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
        final RelativeLayout background = mActivity.findViewById(R.id.activity_display_image);
        final Toolbar toolbar = mActivity.findViewById(R.id.activity_display_image_toolbar);
        final WrapperView view = new WrapperView(toolbar);
        String path = mPathList.get(position);
        PhotoView photoView = new PhotoView(mActivity);
        photoView.enable();
        photoView.setMaxScale(3);
        ViewGroup parent = (ViewGroup) photoView.getParent();
        if (parent != null) {
            parent.removeView(photoView);
        }
        DrawableCrossFadeFactory factory = new DrawableCrossFadeFactory
                .Builder(200)
                .setCrossFadeEnabled(true).build();
        Glide.with(mActivity)
                .load(path)
                .transition(DrawableTransitionOptions.with(factory))
                .into(photoView);
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFullScreen) {
                    // 全屏状态下执行此代码块会退出全屏
                    exitFullScreen(background, toolbar, view);
                    isFullScreen = false;
                } else {
                    // 进入全屏,自动沉浸
                    setFullScreen(background, toolbar, view);
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

    private void setFullScreen(final RelativeLayout background, Toolbar toolbar, WrapperView view) {
        mActivity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        AnimationSet as = new AnimationSet(true);
        as.setDuration(300);
        AlphaAnimation aa = new AlphaAnimation(1, 0);
        as.addAnimation(aa);
        TranslateAnimation ta = new TranslateAnimation(0, 0, 0, -200);
        as.addAnimation(ta);
        toolbar.startAnimation(as);
        toolbar.setVisibility(View.GONE);
        // 背景切换动画
        /*ValueAnimator va = ValueAnimator.ofObject(new ArgbEvaluator(), Color.WHITE, Color.BLACK);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int color = (int) animation.getAnimatedValue();
                background.setBackgroundColor(color);
            }
        });
        va.setDuration(150);
        va.start();*/
    }

    private void exitFullScreen(final RelativeLayout background, Toolbar toolbar, WrapperView view) {
        mActivity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        AnimationSet as = new AnimationSet(true);
        as.setDuration(300);
        AlphaAnimation aa = new AlphaAnimation(0, 1);
        as.addAnimation(aa);
        TranslateAnimation ta = new TranslateAnimation(0, 0, -150, 0);
        as.addAnimation(ta);
        toolbar.startAnimation(as);
        toolbar.setVisibility(View.VISIBLE);
        // 背景切换动画
        /*ValueAnimator va = ValueAnimator.ofObject(new ArgbEvaluator(), Color.BLACK, Color.WHITE);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int color = (int) animation.getAnimatedValue();
                background.setBackgroundColor(color);
            }
        });
        va.setDuration(150);
        va.start();*/
    }
}
