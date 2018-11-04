package com.aaron.justlike.adapter;

import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

import com.aaron.justlike.R;
import com.aaron.justlike.activity.DisplayImageActivity;
import com.aaron.justlike.another.WrapperView;
import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.google.android.material.appbar.AppBarLayout;

import java.util.List;

import androidx.annotation.NonNull;
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
        final AppBarLayout appBarLayout = mActivity.findViewById(R.id.app_bar_layout);
        final WrapperView view = new WrapperView(appBarLayout);
        String path = mPathList.get(position);
        PhotoView photoView = new PhotoView(mActivity);
        photoView.enable();
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
                    exitFullScreen(background, view, appBarLayout);
                    isFullScreen = false;
                } else {
                    // 进入全屏,自动沉浸
                    setFullScreen(background, view);
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

    private void setFullScreen(final RelativeLayout background, WrapperView view) {
        mActivity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        ObjectAnimator animator = ObjectAnimator
                .ofFloat(view, "height", -260);
        animator.setDuration(380);
        animator.start();
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

    private void exitFullScreen(final RelativeLayout background, WrapperView view,
                                AppBarLayout appBarLayout) {
        mActivity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        TranslateAnimation ta = new TranslateAnimation(0, 0, -260, 0);
        ta.setDuration(200);
        appBarLayout.startAnimation(ta);
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "height", 0);
        animator.setDuration(0);
        animator.start();
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
