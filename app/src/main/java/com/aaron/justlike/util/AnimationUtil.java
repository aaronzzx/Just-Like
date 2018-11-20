package com.aaron.justlike.util;

import android.app.Activity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;

import com.aaron.justlike.another.WrapperView;

import androidx.appcompat.widget.Toolbar;

public class AnimationUtil {

    public static void setFullScreen(Activity activity, Toolbar toolbar, WrapperView view) {
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

    public static void exitFullScreen(Activity activity, Toolbar toolbar, WrapperView view) {
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