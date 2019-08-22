package com.aaron.justlike.common;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.aaron.base.image.DefaultOption;
import com.aaron.base.image.ImageLoader;
import com.aaron.base.image.LoadListener;
import com.aaron.base.impl.OnClickListenerImpl;
import com.aaron.base.util.StatusBarUtils;
import com.aaron.justlike.R;
import com.aaron.justlike.common.event.PhotoEvent;
import com.aaron.justlike.common.http.unsplash.Unsplash;
import com.aaron.justlike.common.http.unsplash.entity.photo.Photo;
import com.aaron.justlike.common.http.unsplash.interfaces.UnsplashCallback;
import com.aaron.justlike.main.MainActivity;
import com.aaron.justlike.online.home.OnlineActivity;
import com.aaron.justlike.online.preview.PreviewActivity;
import com.aaron.ui.widget.RoundProgressBar;
import com.blankj.utilcode.util.LogUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SplashActivity extends CommonActivity {

    private static final int PROGRESS_COUNT_DOWN = 4000;
    private static final int DEFAULT_COUNT_DOWN  = 2000;

    private ImageView mIvRandom;
    private CircleImageView mCircleIvAvatar;
    private TextView mTvName;
    private RoundProgressBar mRoundProgressBar;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.WhiteTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        fullScreen();
        startActivityDelay();

        mIvRandom = findViewById(R.id.app_iv_unsplash);
        mCircleIvAvatar = findViewById(R.id.app_circleiv_avatar);
        mTvName = findViewById(R.id.app_tv_name);
        mRoundProgressBar = findViewById(R.id.app_round_progress);
        mRoundProgressBar.setOnPressListener(new RoundProgressBar.OnPressListener() {
            @Override
            public void onPress(View view) {
                mHandler.removeCallbacksAndMessages(null);
                openActivity();
            }
        });
        requestRandomImage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    private void requestRandomImage() {
        Unsplash.getInstance().getRandomPhotos(this, 1, new UnsplashCallback<List<Photo>>() {
            @Override
            public void onSuccess(List<Photo> photos) {
                Photo photo = photos.get(0);
                String url = photo.getUrls().getRegular();
                String name = photo.getUser().getName();
                String avatar = photo.getUser().getProfileImage().getLarge();
                if (!SplashActivity.this.isFinishing()) {
                    ImageLoader.load(SplashActivity.this, new DefaultOption.Builder(url)
                            .crossFade(300)
                            .addListener(new LoadListener() {
                                @Override
                                public boolean onSuccess(Object resource) {
                                    mHandler.removeCallbacksAndMessages(null);
                                    mIvRandom.setOnClickListener(new OnClickListenerImpl() {
                                        @Override
                                        public void onViewClick(View v, long interval) {
                                            EventBus.getDefault().postSticky(new PhotoEvent(photo));
                                            Intent intent = new Intent(SplashActivity.this, PreviewActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                    mTvName.setText(name);
                                    ImageLoader.load(SplashActivity.this, new DefaultOption.Builder(avatar).into(mCircleIvAvatar));
                                    mRoundProgressBar.setVisibility(View.VISIBLE);
                                    mRoundProgressBar.startSlide(PROGRESS_COUNT_DOWN, (curProgress, maxProgress) -> {
                                        if (curProgress == maxProgress) openActivity();
                                    });
                                    return false;
                                }

                                @Override
                                public boolean onFailure(Throwable e) {
                                    LogUtils.d(e.getMessage());
                                    return false;
                                }
                            })
                            .into(mIvRandom));
                }
            }

            @Override
            public void onFailure(String error) {
                LogUtils.d(error);
            }
        });
    }

    private void fullScreen() {
        StatusBarUtils.setTransparent(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void startActivityDelay() {
        mHandler = new Handler();
        mHandler.postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
            overridePendingTransition(R.anim.app_scale_in_delay, R.anim.app_scale_out_delay);
        }, DEFAULT_COUNT_DOWN);
    }

    private void openActivity() {
        startActivity(new Intent(SplashActivity.this, OnlineActivity.class));
        finish();
        overridePendingTransition(R.anim.app_scale_in_delay, R.anim.app_scale_out_delay);
    }
}
