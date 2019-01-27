package com.aaron.justlike.main.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.aaron.justlike.R;
import com.aaron.justlike.another.Image;
import com.aaron.justlike.main.entity.PreviewEvent;
import com.aaron.justlike.main.presenter.IPreviewPresenter;
import com.aaron.justlike.main.presenter.PreviewPresenter;
import com.aaron.justlike.util.AnimationUtil;
import com.aaron.justlike.util.FileUtils;
import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.yalantis.ucrop.UCrop;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class PreviewActivity extends AppCompatActivity implements IPreviewView,
        View.OnClickListener, ViewPager.OnPageChangeListener {

    private int mPosition;
    private List<Image> mImageList;

    private IPreviewPresenter mPresenter;

    private Toolbar mToolbar;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_image);
        EventBus.getDefault().register(this);
        attachPresenter();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mPresenter.detachView();
    }

    /**
     * 按标题栏返回键直接终结 Activity
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    /**
     * 使用透明状态栏
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    /**
     * TODO 是否显示标题栏菜单：选择修改工具、打开文件管理器
     */
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_main_image_menu, menu);
//        SystemUtils.setIconEnable(menu, true);
//        return super.onCreateOptionsMenu(menu);
//    }

    /**
     * TODO 编写标题栏菜单的响应逻辑
     */
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.open_file_manager:
//
//                break;
//            case R.id.edit_by_tools:
//
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case UCrop.REQUEST_CROP:
                if (resultCode == Activity.RESULT_OK) {
                    Uri resultUri = null;
                    if (data != null) {
                        resultUri = UCrop.getOutput(data);
                    }
                    if (resultUri != null) {
                        FileUtils.setWallpaper(this, FileUtils.getPath(this, resultUri));
                    }
                } else if (resultCode == UCrop.RESULT_ERROR) {
                    Toast.makeText(this, "设置失败", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mPosition = mViewPager.getCurrentItem();
        mPresenter.requestTitle(mImageList.get(mPosition).getPath());
    }

    @Override
    public void onPageSelected(int position) {
        mPosition = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * 接收从 MainActivity 传过来的值
     */
    @Subscribe(threadMode = ThreadMode.POSTING, sticky = true)
    public void onPreviewEvent(PreviewEvent<Image> event) {
        mPosition = event.getPosition();
        mImageList = event.getList();
    }

    @Override
    public void attachPresenter() {
        mPresenter = new PreviewPresenter(this);
    }

    @Override
    public void onShowImage() {

    }

    @Override
    public void onShowTitle(String title) {
        mToolbar.setTitle(title);
    }

    @Override
    public void animIn() {

    }

    @Override
    public void animOut() {

    }

    private void initView() {
        mToolbar = findViewById(R.id.activity_display_image_toolbar);
        mViewPager = findViewById(R.id.activity_display_image_vp);

        mViewPager.addOnPageChangeListener(this);

        mPresenter.requestTitle(mImageList.get(mPosition).getPath());
        initToolbar();
        initViewPager();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initViewPager() {
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setPageMargin(50);
        PreviewAdapter adapter = new PreviewAdapter();
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(mPosition);
    }

    public class PreviewAdapter extends PagerAdapter {

        private boolean isFullScreen;

        public PreviewAdapter() {

        }

        @Override
        public int getCount() {
            return mImageList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            final Toolbar toolbar = PreviewActivity.this.findViewById(R.id.activity_display_image_toolbar);
            final LinearLayout bottomBar = PreviewActivity.this.findViewById(R.id.bottom_bar);
            final ImageView share = PreviewActivity.this.findViewById(R.id.action_share);
            final ImageView info = PreviewActivity.this.findViewById(R.id.action_info);
            final ImageView set_wallpaper = PreviewActivity.this.findViewById(R.id.action_set_wallpaper);
            final ImageView delete = PreviewActivity.this.findViewById(R.id.action_delete);
            String path = mImageList.get(position).getPath();
            final PhotoView photoView = new PhotoView(PreviewActivity.this);
            photoView.enable();
            photoView.setMaxScale(2.5F);
            ViewGroup parent = (ViewGroup) photoView.getParent();
            if (parent != null) {
                parent.removeView(photoView);
            }
            RequestOptions options = new RequestOptions()
                    .override(3000, 3000)
                    .centerInside();
            DrawableCrossFadeFactory factory = new DrawableCrossFadeFactory
                    .Builder(300)
                    .setCrossFadeEnabled(true).build();
            Glide.with(PreviewActivity.this)
                    .load(path)
                    .apply(options)
                    .transition(DrawableTransitionOptions.with(factory))
                    .into(photoView);
            photoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isFullScreen) {
                        // 全屏状态下执行此代码块会退出全屏
                        AnimationUtil.exitFullScreen(PreviewActivity.this, toolbar, 0);
                        AnimationUtil.setBottomBar(bottomBar, "show", 0, share,
                                info, set_wallpaper, delete);
                        isFullScreen = false;
                    } else {
                        // 进入全屏,自动沉浸
                        AnimationUtil.setFullScreen(PreviewActivity.this, toolbar, 0);
                        AnimationUtil.setBottomBar(bottomBar, "hide", 0, share,
                                info, set_wallpaper, delete);
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
            if (PreviewActivity.this.mPosition == (Integer) view.getTag()) {
                return POSITION_NONE;
            } else {
                return POSITION_UNCHANGED;
            }
        }
    }
}
