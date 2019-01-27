package com.aaron.justlike.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aaron.justlike.R;
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

import java.io.File;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

public class OnlineImageActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int NORMAL = 0;
    private static final int SET_WALLPAPER = 1;

    private Toolbar mToolbar;
    private ProgressBar mProgressBar;
    private ImageView mProgressImage;
    private CircleImageView mAuthorImage;
    private TextView mAuthorName;
    private TextView mImageDownloads;
    private TextView mImageLikes;
    private PhotoView mPhotoView;
    private Photo mPhoto;
    private LinearLayout mBottomBar;
    private FloatingActionMenu mFloatingActionMenu;
    private FloatingActionButton mFabDownload;
    private FloatingActionButton mFabWallpaper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_image);
        initViews();
        loadImageByGlide();
        String dirPath = Environment.getExternalStorageDirectory().getPath() + "/JustLike/online";
        File mkDir = new File(dirPath);
        if (!mkDir.exists())
            //noinspection ResultOfMethodCallIgnored
            mkDir.mkdirs();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unbindService(mConnection);
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
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_SUBJECT, "图片分享");
                share.putExtra(Intent.EXTRA_TEXT, "来自 Unsplash: " + mPhoto.getLinks().getHtml());
                share = Intent.createChooser(share, "分享");
                startActivity(share);
                break;
            case R.id.action_go_web:
                Intent goWeb = new Intent(Intent.ACTION_VIEW);
                goWeb.setData(Uri.parse("https://unsplash.com"));
                startActivity(goWeb);
                break;
            case R.id.action_go_photographer:
                Intent goAuthor = new Intent(Intent.ACTION_VIEW);
                goAuthor.setData(Uri.parse(mPhoto.getUser().getLinks().getHtml()));
                startActivity(goAuthor);
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

    @SuppressLint("SwitchIntDef")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_progress_image:
                loadImageByGlide();
                mProgressImage.setVisibility(View.GONE);
                break;
            case R.id.activity_online_image_view:
                if (mFloatingActionMenu.isOpened()) {
                    mFloatingActionMenu.close(true);
                    break;
                }
                int flag = mToolbar.getVisibility();
                if (flag == View.GONE) {
                    AnimationUtil.showToolbar(this, mToolbar, 0);
                    AnimationUtil.handleBottomBar(mBottomBar, mFloatingActionMenu, "show", 0);
                } else {
                    AnimationUtil.hideToolbar(this, mToolbar, 0);
                    AnimationUtil.handleBottomBar(mBottomBar, mFloatingActionMenu, "hide", 0);
                }
                break;
            case R.id.fab_download:
                mFloatingActionMenu.close(true);
                String path = Environment.getExternalStoragePublicDirectory
                        (Environment.DIRECTORY_PICTURES) + "/JustLike/online/" + mPhoto.getId() + ".JPG";
                File file = new File(path);
                if (file.exists()) {
                    Toast.makeText(this, "本地已下载", Toast.LENGTH_SHORT).show();
                } else {
                    String url = mPhoto.getLinks().getDownload();
                    new FileUtils.DownloadUtils(this).downloadImage(url, mPhoto.getId() + ".JPG", NORMAL);
                }
                break;
            case R.id.fab_set_wallpaper:
                mFloatingActionMenu.close(true);
                String path1 = Environment.getExternalStoragePublicDirectory
                        (Environment.DIRECTORY_PICTURES) + "/JustLike/online/" + mPhoto.getId() + ".JPG";
                File file1 = new File(path1);
                if (file1.exists()) {
                    FileUtils.setWallpaper(this, path1);
                } else {
                    String url1 = mPhoto.getLinks().getDownload();
                    new FileUtils.DownloadUtils(this).downloadImage(url1, mPhoto.getId() + ".JPG", SET_WALLPAPER);
                }
                break;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initViews() {
        // 获取从适配器序列化过来的值
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mPhoto = bundle.getParcelable("photo");
        }
        mProgressBar = findViewById(R.id.toolbar_progress_bar);
        mProgressImage = findViewById(R.id.toolbar_progress_image);
        mProgressImage.setOnClickListener(this);
        mToolbar = findViewById(R.id.activity_online_image_toolbar);
        setSupportActionBar(mToolbar);
        // 启用标题栏的返回键
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mAuthorImage = findViewById(R.id.author_image);
        mAuthorName = findViewById(R.id.author_name);
        mImageLikes = findViewById(R.id.image_likes);
        mImageDownloads = findViewById(R.id.image_downloads);
        mPhotoView = findViewById(R.id.activity_online_image_view);
        mPhotoView.setOnClickListener(this);
        mBottomBar = findViewById(R.id.bottom_bar);
        mFloatingActionMenu = findViewById(R.id.fab_menu);
        mFabDownload = findViewById(R.id.fab_download);
        mFabWallpaper = findViewById(R.id.fab_set_wallpaper);
        mFabDownload.setOnClickListener(this);
        mFabWallpaper.setOnClickListener(this);
        AnimationUtil.showToolbar(this, mToolbar, 200);
        AnimationUtil.handleBottomBar(mBottomBar, mFloatingActionMenu, "show", 200);
    }

    @SuppressLint("SetTextI18n")
    private void loadImageByGlide() {
        mProgressBar.setVisibility(View.VISIBLE);
        mAuthorName.setText(mPhoto.getUser().getName());
        mImageLikes.setText(mPhoto.getLikes() + " Likes");
        mImageDownloads.setText(mPhoto.getCreatedAt().substring(0, 10));
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.ic_place_holder);
        Glide.with(this)
                .load(mPhoto.getUser().getProfileImage().getLarge())
                .apply(options)
                .into(mAuthorImage);
        Glide.with(this)
                .load(mPhoto.getUrls().getRaw() + "&fm=jpg&h=2160&q=80")
                .thumbnail(Glide.with(this).load(mPhoto.getUrls().getRegular()))
                .listener(mListener)
                .into(mPhotoView);
    }

    private RequestListener mListener = new RequestListener() {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
            mProgressBar.setVisibility(View.GONE);
            mProgressImage.setVisibility(View.VISIBLE);
            mProgressImage.setImageResource(R.mipmap.ic_error_circle);
            Snackbar.make(mPhotoView, "加载失败，请检查网络", Snackbar.LENGTH_LONG).show();
            return true;
        }

        @Override
        public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
            mProgressBar.setVisibility(View.GONE);
            AnimationUtil.showProgressImage(mProgressImage);
            mPhotoView.enable();
            mPhotoView.setImageDrawable((Drawable) resource);
            return true;
        }
    };
}