package com.aaron.justlike.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.aaron.justlike.R;
import com.aaron.justlike.activity.mine.MineActivity;
import com.aaron.justlike.activity.online.OnlineActivity;
import com.jaeger.library.StatusBarUtil;

public class SplashActivity extends BaseActivity {

    private NetworkReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.WhiteTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        animation();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        mReceiver = new NetworkReceiver();
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        View decorView = getWindow().getDecorView();
        if (hasFocus) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                StatusBarUtil.setTranslucent(this, 70);
            }
        }
    }

    private void animation() {
        ImageView imageView = findViewById(R.id.image_view);
        AnimationSet set = new AnimationSet(true);
        set.setDuration(300);
        set.setFillAfter(true);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
//        ScaleAnimation scaleAnimation = new ScaleAnimation(0.7F, 1, 0.7F, 1,
//                Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F);
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 100, 0);
        set.addAnimation(alphaAnimation);
//        set.addAnimation(scaleAnimation);
        set.addAnimation(translateAnimation);
        imageView.startAnimation(set);
    }

    private void openActivity(Class clazz) {
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, clazz));
            finish();
        }, 1000);
    }

    private class NetworkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isAvailable()) {
                openActivity(MineActivity.class);
            } else {
                openActivity(OnlineActivity.class);
            }
        }
    }
}
