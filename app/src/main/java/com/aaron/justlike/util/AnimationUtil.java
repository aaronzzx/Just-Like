package com.aaron.justlike.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.aaron.justlike.R;

import androidx.appcompat.widget.Toolbar;

public class AnimationUtil {

    public static void setFullScreen(Activity activity, Toolbar toolbar, long startOffset) {
        activity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        // Toolbar 动画
        AnimationSet as = new AnimationSet(true);
        as.setDuration(200);
        AlphaAnimation aa = new AlphaAnimation(1, 0);
        as.addAnimation(aa);
        TranslateAnimation ta = new TranslateAnimation(0, 0, 0, -150);
        as.addAnimation(ta);
        as.setStartOffset(startOffset);
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

    public static void exitFullScreen(Activity activity, Toolbar toolbar, long startOffset) {
        activity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        // Toolbar 动画
        AnimationSet as = new AnimationSet(true);
        as.setDuration(200);
        AlphaAnimation aa = new AlphaAnimation(0, 1);
        as.addAnimation(aa);
        TranslateAnimation ta = new TranslateAnimation(0, 0, -150, 0);
        as.addAnimation(ta);
        as.setStartOffset(startOffset);
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
