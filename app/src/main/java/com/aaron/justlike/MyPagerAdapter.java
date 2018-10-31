package com.aaron.justlike;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.service.wallpaper.WallpaperService;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        final RelativeLayout background = mActivity.findViewById(R.id.activity_display_image);
        final AppBarLayout appBarLayout = mActivity.findViewById(R.id.app_bar_layout);
//        final LinearLayout button = mActivity.findViewById(R.id.set_wallpaper);
        final WrapperView view = new WrapperView(appBarLayout);
//        final WrapperView view1 = new WrapperView(button);
        Uri uri = mUriList.get(position);
        /*button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WallpaperManager manager = WallpaperManager.getInstance(mActivity);
                if (manager != null && mUri != null) {
                    String path = mUri.getPath();
                    String absolutePath = FileUtils.getAbsolutePath(path);
                    File file = new File(absolutePath);
                    FileInputStream fis = null;
                    try {
                        fis = new FileInputStream(file);
                        manager.setStream(fis);
                        Toast.makeText(mActivity, "设置成功", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (fis != null) fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });*/
        PhotoView photoView = new PhotoView(mActivity);
        photoView.setScaleLevels(1, 2, 3);
        photoView.setTag(position);
        ViewGroup parent = (ViewGroup) photoView.getParent();
        if (parent != null) {
            parent.removeView(photoView);
        }
        Picasso.get()
                .load(uri)
                .config(Bitmap.Config.RGB_565)
//                .resize(3000, 3000)
//                .onlyScaleDown()
                .fit()
                .rotate(FileUtils.getBitmapDegree(FileUtils.getAbsolutePath(mPath)))
                .centerInside()
                .into(photoView);
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFullScreen) {
                    // 全屏状态下执行此代码块会退出全屏
                    exitFullScreen(background, view, appBarLayout/*, view1, button*/);
                    isFullScreen = false;
                } else {
                    // 进入全屏,自动沉浸
                    setFullScreen(background, view/*, view1*/);
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

    private void setFullScreen(final RelativeLayout background, WrapperView view/*, WrapperView view1*/) {
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

        /*ObjectAnimator animator1 = ObjectAnimator.ofFloat(view1, "height", 260);
        animator1.setDuration(380);
        animator1.start();*/
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
    }

    private void exitFullScreen(final RelativeLayout background, WrapperView view,
                                AppBarLayout appBarLayout/*, WrapperView view1, LinearLayout button*/) {
        mActivity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        TranslateAnimation ta = new TranslateAnimation(0, 0, -260, 0);
        ta.setDuration(250);
        appBarLayout.startAnimation(ta);
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "height", 0);
        animator.setDuration(0);
        animator.start();

        /*TranslateAnimation ta1 = new TranslateAnimation(0, 0, 260, 0);
        ta1.setDuration(250);
        button.startAnimation(ta1);
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(view1, "height", 0);
        animator1.setDuration(0);
        animator1.start();*/
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
    }
}
