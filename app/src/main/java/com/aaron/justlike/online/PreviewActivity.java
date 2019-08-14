package com.aaron.justlike.online;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;

import com.aaron.justlike.R;
import com.aaron.justlike.common.CommonActivity;
import com.aaron.justlike.common.event.PhotoEvent;
import com.aaron.justlike.common.http.glide.GlideApp;
import com.aaron.justlike.common.http.glide.request.Request;
import com.aaron.justlike.common.http.unsplash.entity.photo.Photo;
import com.aaron.justlike.common.impl.ObserverImpl;
import com.aaron.justlike.common.util.AnimationUtil;
import com.aaron.justlike.common.util.FileUtil;
import com.aaron.justlike.common.util.SystemUtil;
import com.bm.library.PhotoView;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class PreviewActivity extends CommonActivity implements IPreviewView, View.OnClickListener {

    private IPreviewPresenter mPresenter;

    private Toolbar mToolbar;
    private ProgressBar mProgressBar;
    private ImageView mProgressImage;
    private CircleImageView mAuthorImage;
    private TextView mAuthorName;
    private TextView mImageDate;
    private TextView mImageLikes;
    private PhotoView mPhotoView;
    private Photo mPhoto;
    private FrameLayout mBottomBar;
    private FloatingActionMenu mFloatingActionMenu;

    private long mPressTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_preview);
        EventBus.getDefault().register(this);
        initView();
        mPresenter = new PreviewPresenter();
        mPresenter.attachView(this);
        mPresenter.requestImage(mPhoto);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mPresenter.detachView();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            Window window = getWindow();
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_online_preview_menu, menu);
        SystemUtil.setIconEnable(menu, true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                openShare();
                break;
            case R.id.action_go_web:
                openUnsplash();
                break;
            case R.id.action_go_photographer:
                openAuthor();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 按标题栏返回键直接终结 Activity
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_progress_image:
                mPresenter.requestImage(mPhoto);
                break;
            case R.id.activity_online_image_view:
                if (mFloatingActionMenu.isOpened()) {
                    mFloatingActionMenu.close(true);
                    break;
                }
                if (mToolbar.getVisibility() == View.GONE) {
                    animIn(0);
                } else {
                    animOut(0);
                }
                break;
            case R.id.fab_download:
                mFloatingActionMenu.close(true);
                if (System.currentTimeMillis() - mPressTime < (3 * 1000)) break;
                mPresenter.requestMode(this, mPhoto, PreviewPresenter.NORMAL);
                mPressTime = System.currentTimeMillis();
                break;
            case R.id.fab_set_wallpaper:
                mFloatingActionMenu.close(true);
                if (System.currentTimeMillis() - mPressTime < (3 * 1000)) break;
                mPresenter.requestMode(this, mPhoto, PreviewPresenter.SET_WALLPAPER);
                mPressTime = System.currentTimeMillis();
                break;
        }
    }

    /**
     * 与 OnlineActivity 通信
     */
    @Subscribe(threadMode = ThreadMode.POSTING, sticky = true)
    public void onPhotoEvent(PhotoEvent event) {
        mPhoto = event.getPhoto();
    }

    @Override
    public void onShowImage(String urls, String thumbnail) {
        GlideApp.getInstance()
                .with(this)
                .asDrawable()
                .load(urls)
                .thumbnail(thumbnail)
                .transition(200)
                .listener(new Request.Listener<Drawable>() {
                    @Override
                    public void onLoadFailed() {
                        mProgressBar.setVisibility(View.GONE);
                        mProgressImage.setVisibility(View.VISIBLE);
                        mProgressImage.setImageResource(R.drawable.ic_error_circle);
                        Toast.makeText(PreviewActivity.this, "网络开小差了", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResourceReady(Drawable resource, boolean isFirstResource) {
                        mProgressBar.setVisibility(View.GONE);
                        mProgressImage.setVisibility(View.VISIBLE);
                        mProgressImage.setImageResource(R.drawable.ic_done_circle);
                        AnimationUtil.showProgressImage(mProgressImage);
                        mPhotoView.enable();
                    }
                })
                .into(mPhotoView);
    }

    @Override
    public void onShowAuthorName(String args) {
        mAuthorName.setText(args);
    }

    @Override
    public void onShowAuthorAvatar(String urls) {
        GlideApp.getInstance()
                .with(this)
                .asDrawable()
                .load(urls)
                .placeHolder(R.drawable.ic_place_holder)
                .into(mAuthorImage);
    }

    @Override
    public void onShowImageLikes(String args) {
        mImageLikes.setText(args);
    }

    @Override
    public void onShowImageDate(String args) {
        mImageDate.setText(args);
    }

    @Override
    public void onShowProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onHideProgressStatus() {
        mProgressImage.setVisibility(View.GONE);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onSetWallpaper(String imagePath) {
        Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            emitter.onNext(0);
            emitter.onNext(FileUtil.setWallpaper(PreviewActivity.this, imagePath));
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ObserverImpl<Integer>() {
                    @Override
                    public void onNext(Integer flag) {
                        if (flag == 0) {
                            onShowMessage("稍等片刻");
                        } else if (flag == 1) {
                            onShowMessage("设置成功");
                        } else {
                            onShowMessage("设置失败");
                        }
                    }
                });
    }

    @Override
    public void onShowMessage(String args) {
        Toast.makeText(this, args, Toast.LENGTH_SHORT).show();
    }

    private void initView() {
        mToolbar = findViewById(R.id.activity_online_image_toolbar);
        mProgressBar = findViewById(R.id.toolbar_progress_bar);
        mProgressImage = findViewById(R.id.toolbar_progress_image);
        mAuthorImage = findViewById(R.id.author_image);
        mAuthorName = findViewById(R.id.author_name);
        mImageLikes = findViewById(R.id.image_likes);
        mImageDate = findViewById(R.id.image_downloads);
        mPhotoView = findViewById(R.id.activity_online_image_view);
        mBottomBar = findViewById(R.id.bottom_bar);
        mFloatingActionMenu = findViewById(R.id.fab_menu);
        FloatingActionButton fabDownload = findViewById(R.id.fab_download);
        FloatingActionButton fabWallpaper = findViewById(R.id.fab_set_wallpaper);

        mProgressImage.setOnClickListener(this);
        mPhotoView.setOnClickListener(this);
        fabDownload.setOnClickListener(this);
        fabWallpaper.setOnClickListener(this);

        initToolbar();
        animIn(200);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        // 启用标题栏的返回键
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            Drawable iconBack = getResources().getDrawable(R.drawable.ic_back);
            DrawableCompat.setTint(iconBack, getResources().getColor(R.color.colorPrimaryWhite));
            actionBar.setHomeAsUpIndicator(iconBack);
        }
    }

    private void openShare() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT, "图片分享");
        share.putExtra(Intent.EXTRA_TEXT, "来自 Unsplash: " + mPhoto.getLinks().getHtml());
        share = Intent.createChooser(share, "分享");
        startActivity(share);
    }

    private void openUnsplash() {
        Intent goWeb = new Intent(Intent.ACTION_VIEW);
        goWeb.setData(Uri.parse("https://unsplash.com"));
        startActivity(goWeb);
    }

    private void openAuthor() {
        Intent goAuthor = new Intent(Intent.ACTION_VIEW);
        goAuthor.setData(Uri.parse(mPhoto.getUser().getLinks().getHtml()));
        startActivity(goAuthor);
    }

    private void animIn(long startOffset) {
        AnimationUtil.showToolbar(this, mToolbar, startOffset);
        AnimationUtil.showBottomBar(mBottomBar, startOffset);
    }

    private void animOut(long startOffset) {
        AnimationUtil.hideToolbar(this, mToolbar, startOffset);
        AnimationUtil.hideBottomBar(mBottomBar, startOffset);
    }
}
