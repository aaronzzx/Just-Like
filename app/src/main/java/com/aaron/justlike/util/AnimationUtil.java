package com.aaron.justlike.util;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.aaron.justlike.R;

public class AnimationUtil {

    public static void setImageSmall(View view) {
//        ScaleAnimation animation = new ScaleAnimation(1, 0.8F, 1, 0.8F,
//                Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F);
//        animation.setFillAfter(true);
//        animation.setDuration(100);
//        view.startAnimation(animation);
        ObjectAnimator scaleX_Animator = ObjectAnimator.ofFloat(view, "scaleX", 1.0F, 0.8F);
        scaleX_Animator.setDuration(100);
        scaleX_Animator.start();
        ObjectAnimator scaleY_Animator = ObjectAnimator.ofFloat(view, "scaleY", 1.0F, 0.8F);
        scaleY_Animator.setDuration(100);
        scaleY_Animator.start();
    }

    public static void setImageLarge(View view) {
//        ScaleAnimation animation = new ScaleAnimation(0.8F, 1.0F, 0.8F, 1.0F,
//                Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F);
//        animation.setFillAfter(true);
//        animation.setDuration(100);
//        view.startAnimation(animation);
        ObjectAnimator scaleX_Animator = ObjectAnimator.ofFloat(view, "scaleX", 0.8F, 1.0F);
        scaleX_Animator.setDuration(100);
        scaleX_Animator.start();
        ObjectAnimator scaleY_Animator = ObjectAnimator.ofFloat(view, "scaleY", 0.8F, 1.0F);
        scaleY_Animator.setDuration(100);
        scaleY_Animator.start();
    }

    public static void showViewByAlpha(View view, float fromAlpha, float toAlpha, long duration) {
        AlphaAnimation aa = new AlphaAnimation(fromAlpha, toAlpha);
        aa.setFillAfter(true);
        aa.setDuration(duration);
        view.startAnimation(aa);
    }

    public static void setBottomBar(final ViewGroup group, String type, long startOffset, final View... views) {
        AnimationSet as = new AnimationSet(true);
        as.setFillAfter(true);
        as.setDuration(250);
        as.setStartOffset(startOffset);
        switch (type) {
            case "hide":
                AlphaAnimation aa1 = new AlphaAnimation(1, 0);
                TranslateAnimation ta1 = new TranslateAnimation(0, 0, 0, 150);
                as.addAnimation(aa1);
                as.addAnimation(ta1);
                group.startAnimation(as);
                as.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        group.setVisibility(View.GONE);
                        // 隐藏按钮
                        views[0].setVisibility(View.GONE);
                        views[1].setVisibility(View.GONE);
                        views[2].setVisibility(View.GONE);
                        views[3].setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                break;
            case "show":
                AlphaAnimation aa2 = new AlphaAnimation(0, 1);
                TranslateAnimation ta2 = new TranslateAnimation(0, 0, 150, 0);
                as.addAnimation(aa2);
                as.addAnimation(ta2);
                group.startAnimation(as);
                group.setVisibility(View.VISIBLE);
                // 隐藏按钮
                views[0].setVisibility(View.VISIBLE);
                views[1].setVisibility(View.VISIBLE);
                views[2].setVisibility(View.VISIBLE);
                views[3].setVisibility(View.VISIBLE);
                break;
        }
    }

    public static void handleBottomBar(ViewGroup group, View view, String type, long startOffset) {
        AnimationSet as = new AnimationSet(true);
        as.setFillAfter(true);
        as.setDuration(250);
        as.setStartOffset(startOffset);
        switch (type) {
            case "hide":
                AlphaAnimation aa1 = new AlphaAnimation(1, 0);
                TranslateAnimation ta1 = new TranslateAnimation(0, 0, 0, 200);
                as.addAnimation(aa1);
                as.addAnimation(ta1);
                group.startAnimation(as);
                view.startAnimation(as);
                group.setVisibility(View.GONE);
                view.setSystemUiVisibility(View.GONE);
                break;
            case "show":
                AlphaAnimation aa2 = new AlphaAnimation(0, 1);
                TranslateAnimation ta2 = new TranslateAnimation(0, 0, 200, 0);
                as.addAnimation(aa2);
                as.addAnimation(ta2);
                group.startAnimation(as);
                view.startAnimation(as);
                group.setVisibility(View.VISIBLE);
                view.setSystemUiVisibility(View.VISIBLE);
                break;
        }
    }

    public static Bitmap handleImageEffect(Bitmap bm, float saturation) {
        Bitmap bmp = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        ColorMatrix saturationMatrix = new ColorMatrix();
        saturationMatrix.setSaturation(saturation);
        paint.setColorFilter(new ColorMatrixColorFilter(saturationMatrix));
        canvas.drawBitmap(bm, 0, 0, paint);
        return bmp;
    }

    public static void showFakeView(ImageView imageView, Drawable drawable) {
        imageView.setAlpha(0.0F);
        imageView.setImageDrawable(drawable);
        imageView.animate().setDuration(2000).setInterpolator(new DecelerateInterpolator()).alpha(1.0F).start();
    }

    public static void hideToolbar(Activity activity, View view, long startOffset) {
        activity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        AnimationSet as = new AnimationSet(true);
        as.setDuration(250);
        AlphaAnimation aa = new AlphaAnimation(1, 0);
        as.addAnimation(aa);
        TranslateAnimation ta = new TranslateAnimation(0, 0, 0, -130);
        as.addAnimation(ta);
        as.setStartOffset(startOffset);
        view.startAnimation(as);
        view.setVisibility(View.GONE);
    }

    public static void showToolbar(Activity activity, View view, long startOffset) {
        activity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        AnimationSet as = new AnimationSet(true);
        as.setDuration(250);
        AlphaAnimation aa = new AlphaAnimation(0, 1);
        as.addAnimation(aa);
        TranslateAnimation ta = new TranslateAnimation(0, 0, -130, 0);
        as.addAnimation(ta);
        as.setStartOffset(startOffset);
        view.startAnimation(as);
        view.setVisibility(View.VISIBLE);
    }

    public static void hideBottomBar(Activity activity, View view, long startOffset) {
        activity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        AnimationSet as = new AnimationSet(true);
        as.setDuration(250);
        AlphaAnimation aa = new AlphaAnimation(1, 0);
        as.addAnimation(aa);
        TranslateAnimation ta = new TranslateAnimation(0, 0, 0, 130);
        as.addAnimation(ta);
        as.setStartOffset(startOffset);
        view.startAnimation(as);
        view.setVisibility(View.GONE);
    }

    public static void showBottomBar(Activity activity, View view, long startOffset) {
        activity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        AnimationSet as = new AnimationSet(true);
        as.setDuration(250);
        AlphaAnimation aa = new AlphaAnimation(0, 1);
        as.addAnimation(aa);
        TranslateAnimation ta = new TranslateAnimation(0, 0, 130, 0);
        as.addAnimation(ta);
        as.setStartOffset(startOffset);
        view.startAnimation(as);
        view.setVisibility(View.VISIBLE);
    }

    public static void showProgressBar(final ProgressBar progressBar) {
        TranslateAnimation animation = new TranslateAnimation(0, 1, 0, 1);
        animation.setDuration(300);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        progressBar.startAnimation(animation);
    }

    public static void hideProgressBar(final ProgressBar progressBar, final ImageView img) {
        TranslateAnimation animation = new TranslateAnimation(1, 0, 1, 0);
        animation.setDuration(500);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                progressBar.setVisibility(View.GONE);
                showProgressImage(img);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        progressBar.startAnimation(animation);
    }

    /**
     * 用于在线图片详情页的进度显示
     *
     * @param img
     */
    public static void showProgressImage(final ImageView img) {
        img.setImageResource(R.mipmap.ic_done_circle);
        img.setVisibility(View.VISIBLE);
        AlphaAnimation aa = new AlphaAnimation(0, 1);
        aa.setDuration(300);
        aa.setFillAfter(true);
        aa.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                AlphaAnimation aa1 = new AlphaAnimation(1, 0);
                aa1.setDuration(300);
                aa1.setFillAfter(true);
                aa1.setStartOffset(600);
                img.startAnimation(aa1);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        img.startAnimation(aa);
    }
}
