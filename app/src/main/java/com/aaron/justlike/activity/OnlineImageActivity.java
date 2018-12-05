package com.aaron.justlike.activity;

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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aaron.justlike.R;
import com.aaron.justlike.data.Downloads;
import com.aaron.justlike.data.Likes;
import com.aaron.justlike.data.Views;
import com.aaron.justlike.util.AnimationUtil;
import com.aaron.justlike.util.FileUtils;
import com.aaron.justlike.util.LogUtil;
import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kc.unsplash.models.Photo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OnlineImageActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String CLIENT_ID = "936a1449161e2845eff4da43b160cea25e234a32188cc16c981e997590c65086";
    private String mPhotoStats;
    private Toolbar mToolbar;
    private ProgressBar mProgressBar;
    private ImageView mProgressImage;
    private CircleImageView mAuthorImage;
    private TextView mAuthorName;
    private TextView mImageDownloads;
    private TextView mImageLikes;
    private PhotoView mPhotoView;
    private Photo mPhoto;
    private FloatingActionButton mFabDownload;
    private FloatingActionButton mFabWallpaper;
    private FloatingActionButton mFabImageInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_image);
        initViews();
        loadImageByGlide();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            Window window = getWindow();
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_online_image_main, menu);
        setIconEnable(menu, true);
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

    private void setIconEnable(Menu menu, boolean enable) {
        try {
            @SuppressLint("PrivateApi")
            Class<?> clazz = Class.forName("com.android.internal.view.menu.MenuBuilder");
            Method m = clazz.getDeclaredMethod("setOptionalIconsVisible", boolean.class);
            m.setAccessible(true);
            m.invoke(menu, enable);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                loadImageByGlide();
                mProgressImage.setVisibility(View.GONE);
                break;
            case R.id.fab_download:

                break;
            case R.id.fab_set_wallpaper:

                break;
            case R.id.fab_image_info:

                break;
        }
    }

    private void initViews() {
        // 获取从适配器序列化过来的值
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mPhoto = bundle.getParcelable("urls");
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
        mFabDownload = findViewById(R.id.fab_download);
        mFabWallpaper = findViewById(R.id.fab_set_wallpaper);
        mFabImageInfo = findViewById(R.id.fab_image_info);
        mFabDownload.setOnClickListener(this);
        mFabWallpaper.setOnClickListener(this);
        mFabImageInfo.setOnClickListener(this);
        AnimationUtil.exitFullScreen(this, mToolbar, 200);
    }

    @SuppressLint("SetTextI18n")
    private void loadImageByGlide() {
        mProgressBar.setVisibility(View.VISIBLE);
        mAuthorName.setText(mPhoto.getUser().getName());
        mImageLikes.setText(getPhotoStats("likes") + " Likes");
        mImageDownloads.setText(getPhotoStats("downloads") + " Downloads");
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.ic_place_holder);
        Glide.with(this)
                .load(mPhoto.getUser().getProfileImage().getLarge())
                .apply(options)
                .into(mAuthorImage);
        Glide.with(this)
                .load(mPhoto.getUrls().getFull()/* + "&fm=jpg&h=2160&q=80"*/)
                .thumbnail(Glide.with(this).load(mPhoto.getUrls().getRegular()))
                .listener(mListener)
                .into(mPhotoView);
    }

    private String getPhotoStats(final String type) {
        FileUtils.getPhotoStats(mPhoto.getId(), CLIENT_ID, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                LogUtil.w(LogUtil.TAG, responseData);
                Gson gson = new Gson();
                try {
                    switch (type) {
                        case "downloads":
                            JSONObject object = new JSONObject(responseData);
                            JSONArray array = object.getJSONArray("downloads");
                            String downloadsArray = array.getJSONObject(0).toString();
                            List<Downloads> downloads = gson.fromJson(downloadsArray, new TypeToken<List<Downloads>>(){}.getType());
                            mPhotoStats = downloads.get(0).getTotal();
                            break;
                        case "views":
                            JSONObject object1 = new JSONObject(responseData);
                            JSONArray array1 = object1.getJSONArray("views");
                            String viewsArray = array1.getJSONObject(0).toString();
                            List<Views> views = gson.fromJson(viewsArray, new TypeToken<List<Views>>(){}.getType());
                            mPhotoStats = views.get(0).getTotal();
                            break;
                        case "likes":
                            JSONObject object2 = new JSONObject(responseData);
                            JSONArray array2 = object2.getJSONArray("likes");
                            String likesArray = array2.getJSONObject(0).toString();
                            List<Likes> likes = gson.fromJson(likesArray, new TypeToken<List<Likes>>(){}.getType());
                            mPhotoStats = likes.get(0).getTotal();
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return mPhotoStats;
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