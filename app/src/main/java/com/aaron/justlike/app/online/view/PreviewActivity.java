package com.aaron.justlike.app.online.view;

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

import com.aaron.justlike.R;
import com.aaron.justlike.app.online.entity.PhotoEvent;
import com.aaron.justlike.app.online.presenter.IPreviewPresenter;
import com.aaron.justlike.app.online.presenter.PreviewPresenter;
import com.aaron.justlike.util.AnimationUtil;
import com.aaron.justlike.util.FileUtils;
import com.aaron.justlike.util.SystemUtils;
import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.snackbar.Snackbar;
import com.kc.unsplash.models.Photo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

public class PreviewActivity extends AppCompatActivity implements IPreviewView,
        View.OnClickListener, RequestListener<Drawable> {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_image);
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
        getMenuInflater().inflate(R.menu.activity_online_image_menu, menu);
        SystemUtils.setIconEnable(menu, true);
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
        finish();
        return super.onSupportNavigateUp();
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
                mPresenter.requestMode(mPhoto, PreviewPresenter.NORMAL);
                break;
            case R.id.fab_set_wallpaper:
                mFloatingActionMenu.close(true);
                mPresenter.requestMode(mPhoto, PreviewPresenter.SET_WALLPAPER);
                break;
        }
    }

    /**
     * Glide 加载图像的回调函数
     */
    @Override
    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
        mProgressBar.setVisibility(View.GONE);
        AnimationUtil.showProgressImage(mProgressImage);
        mPhotoView.enable();
        mPhotoView.setImageDrawable(resource);
        return true;
    }

    /**
     * Glide 加载图像的回调函数
     */
    @Override
    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
        mProgressBar.setVisibility(View.GONE);
        mProgressImage.setVisibility(View.VISIBLE);
        mProgressImage.setImageResource(R.mipmap.ic_error_circle);
        Snackbar.make(mPhotoView, "加载失败，请检查网络", Snackbar.LENGTH_LONG).show();
        return true;
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
        Glide.with(this)
                .load(urls)
                .thumbnail(Glide.with(this).load(thumbnail))
                .listener(this)
                .into(mPhotoView);
    }

    @Override
    public void onShowAuthorName(String args) {
        mAuthorName.setText(args);
    }

    @Override
    public void onShowAuthorAvatar(String urls) {
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.ic_place_holder);
        Glide.with(this)
                .load(urls)
                .apply(options)
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
    public void onSetWallpaper(String imagePath) {
        FileUtils.setWallpaper(this, imagePath);
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
