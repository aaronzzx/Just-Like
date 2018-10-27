package com.aaron.justlike;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyPagerAdapter extends PagerAdapter {

    private List<Uri> mUriList;
    private DisplayImageActivity mActivity;
    private String mPath;
    private boolean isFullScreen;

    MyPagerAdapter(List<Uri> uriList, DisplayImageActivity activity, String path) {
        mUriList = uriList;
        mActivity = activity;
        mPath = path;
    }

    @Override
    public int getCount() {
        return mUriList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        final LinearLayout background = mActivity.findViewById(R.id.activity_display_image);
        final AppBarLayout appBarLayout = mActivity.findViewById(R.id.app_bar_layout);
        final WrapperView view = new WrapperView(appBarLayout);
        Uri uri = mUriList.get(position);
        PhotoView photoView = new PhotoView(mActivity);
        photoView.setTag(position);
        ViewGroup parent = (ViewGroup) photoView.getParent();
        if (parent != null) {
            parent.removeView(photoView);
        }
        Picasso.get()
                .load(uri)
                .resize(3000, 3000)
                .onlyScaleDown()
                .rotate(FileUtils.getBitmapDegree(FileUtils.getAbsolutePath(mPath)))
                .centerInside()
                .into(photoView);
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFullScreen) {
                    /*
                     * 全屏状态下执行此代码块会退出全屏
                     */
                    mActivity.getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    TranslateAnimation ta = new TranslateAnimation(0, 0, -223, 0);
                    ta.setDuration(200);
                    appBarLayout.startAnimation(ta);
                    ObjectAnimator animator = ObjectAnimator.ofFloat(view, "height", 0);
                    animator.setDuration(0);
                    animator.start();
                    // 背景切换动画
                    ValueAnimator va = ValueAnimator.ofObject(new ArgbEvaluator(), Color.BLACK, Color.WHITE);
                    va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            int color = (int) animation.getAnimatedValue();
                            background.setBackgroundColor(color);
                        }
                    });
                    va.setDuration(200);
                    va.start();
                    isFullScreen = false;
                } else {
                    /*
                     * 进入全屏,自动沉浸
                     */
                    mActivity.getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                    ObjectAnimator animator = ObjectAnimator.ofFloat(view, "height", -223);
                    animator.start();
                    // 背景切换动画
                    ValueAnimator va = ValueAnimator.ofObject(new ArgbEvaluator(), Color.WHITE, Color.BLACK);
                    va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            int color = (int) animation.getAnimatedValue();
                            background.setBackgroundColor(color);
                        }
                    });
                    va.setDuration(200);
                    va.start();
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
