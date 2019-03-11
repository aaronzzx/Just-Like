package com.aaron.justlike.activity.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aaron.justlike.R;
import com.aaron.justlike.adapter.main.PreviewAdapter;
import com.aaron.justlike.entity.DeletEvent;
import com.aaron.justlike.entity.DeleteEvent;
import com.aaron.justlike.entity.Image;
import com.aaron.justlike.entity.ImageInfo;
import com.aaron.justlike.entity.PreviewEvent;
import com.aaron.justlike.mvp_presenter.main.preview.IPreviewPresenter;
import com.aaron.justlike.mvp_presenter.main.preview.PreviewPresenter;
import com.aaron.justlike.mvp_view.main.IPreviewView;
import com.aaron.justlike.util.AnimationUtil;
import com.aaron.justlike.util.FileUtils;
import com.aaron.justlike.util.SystemUtils;
import com.yalantis.ucrop.UCrop;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class PreviewActivity extends AppCompatActivity implements IPreviewView,
        View.OnClickListener, ViewPager.OnPageChangeListener, PreviewAdapter.Callback {

    public static final int DELETE_EVENT = 1;
    public static final int DELET_EVENT = 2;

    private static final String FIT_SCREEN = "适应屏幕";
    private static final String FREE_CROP = "自由裁剪";
    private static final String[] CROP_TYPE = {FIT_SCREEN, FREE_CROP};

    private int mPosition;
    private int mEventFlag;
    private List<Image> mImageList;

    private IPreviewPresenter mPresenter;

    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private LinearLayout mBottomBar;

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
            case R.id.action_share:
                Intent share = new Intent(Intent.ACTION_VIEW);
                Uri shareUri = FileUtils.getImageContentUri(this, new File(mImageList.get(mPosition).getPath()));
                share.setDataAndType(shareUri, "image/*");
                startActivity(share);
                break;
            case R.id.action_set_wallpaper:
                new AlertDialog.Builder(this)
                        .setTitle("设置壁纸")
                        .setItems(CROP_TYPE, (dialog, which) -> {
                            switch (CROP_TYPE[which]) {
                                case FIT_SCREEN:
                                    openImageCrop(FIT_SCREEN);
                                    break;
                                case FREE_CROP:
                                    openImageCrop(FREE_CROP);
                                    break;
                            }
                        }).show();
                break;
            case R.id.action_info:
                @SuppressLint("InflateParams")
                View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_image_info, null);
                initImageInfo(dialogView);
                // 显示对话框
                new AlertDialog.Builder(this)
                        .setTitle("详情")
                        .setView(dialogView)
                        .show();
                break;
            case R.id.action_delete:
                new AlertDialog.Builder(this)
                        .setTitle("删除图片")
                        .setMessage("图片将会被删除，无法撤销")
                        .setPositiveButton("确定", (dialog, which) -> {
                            if (mEventFlag == DELETE_EVENT) {
                                // 回传 MainActivity
                                EventBus.getDefault().postSticky(new DeleteEvent(mPosition,
                                        mImageList.get(mPosition).getPath()));
                            } else if (mEventFlag == DELET_EVENT) {
                                // 回传 ElementActivity
                                EventBus.getDefault().postSticky(new DeletEvent(mPosition,
                                        mImageList.get(mPosition).getPath()));
                            }
                            finish();
                        })
                        .setNegativeButton("取消", (dialog, which) -> {
                        }).show();
                break;
        }
    }

    /**
     * 页面切换时回调
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mPosition = mViewPager.getCurrentItem();
        mPresenter.requestTitle(mImageList.get(mPosition).getPath());
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onPress() {
        if (mToolbar.getVisibility() == View.GONE) {
            // 全屏状态下执行此代码块会退出全屏
            animIn(0);
        } else {
            // 进入全屏,自动沉浸
            animOut(0);
        }
    }

    /**
     * 接收从 MainActivity 传过来的值
     */
    @Subscribe(threadMode = ThreadMode.POSTING, sticky = true)
    public void onPreviewEvent(PreviewEvent<Image> event) {
        mPosition = event.getPosition();
        mImageList = event.getList();
        mEventFlag = mImageList.get(0).getEventFlag();
        Log.d("PreviewActivity", "mEventFlag: " + mEventFlag);
    }

    @Override
    public void attachPresenter() {
        mPresenter = new PreviewPresenter(this);
    }

    @Override
    public void onShowTitle(String title) {
        mToolbar.setTitle(title);
    }

    private void initView() {
        mToolbar = findViewById(R.id.activity_display_image_toolbar);
        mViewPager = findViewById(R.id.activity_display_image_vp);

        // BottomBar 按钮
        mBottomBar = findViewById(R.id.bottom_bar);
        ImageView shareBtn = findViewById(R.id.action_share);
        ImageView setWallpaperBtn = findViewById(R.id.action_set_wallpaper);
        ImageView imageInfoBtn = findViewById(R.id.action_info);
        ImageView deleteBtn = findViewById(R.id.action_delete);

        shareBtn.setOnClickListener(this);
        setWallpaperBtn.setOnClickListener(this);
        imageInfoBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);
        mViewPager.addOnPageChangeListener(this);

        initToolbar();
        initViewPager();
        animIn(200);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mPresenter.requestTitle(mImageList.get(mPosition).getPath());
    }

    private void initViewPager() {
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setPageMargin(50);
        PagerAdapter adapter = new PreviewAdapter<>(mImageList, this);
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(mPosition);
    }

    private void openImageCrop(String cropType) {
        // 源文件位置
        Uri sourceUri = FileUtils.getUriFromPath(this, new File(mImageList.get(mPosition).getPath()));
        File file = new File(getCacheDir(), "Wallpaper.JPG");
        Uri destinationUri = Uri.fromFile(file); // 需要输出的位置
        // 设置裁剪页面主题
        UCrop.Options options = new UCrop.Options();
        options.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        options.setToolbarColor(getResources().getColor(R.color.colorPrimary));
        options.setActiveWidgetColor(getResources().getColor(R.color.colorPrimary));
        switch (cropType) {
            case FIT_SCREEN: // 打开默认裁剪页面
                int[] widthHeightPixels = SystemUtils.getResolution(getWindowManager());
                UCrop.of(sourceUri, destinationUri)
                        .withAspectRatio(widthHeightPixels[0], widthHeightPixels[1])
                        .withOptions(options)
                        .start(this);
                break;
            case FREE_CROP: // 打开自由裁剪页面
                UCrop.of(sourceUri, destinationUri)
                        .withOptions(options)
                        .start(this);
                break;
        }
    }

    private void initImageInfo(View dialogView) {
        TextView imageTime = dialogView.findViewById(R.id.info_time);
        TextView imageName = dialogView.findViewById(R.id.info_name);
        TextView imageSize = dialogView.findViewById(R.id.info_size);
        TextView imagePixel = dialogView.findViewById(R.id.info_resolution);
        TextView imagePath = dialogView.findViewById(R.id.info_path);
        String path = mImageList.get(mPosition).getPath();
        ImageInfo imageInfo = mPresenter.requestImageInfo(path);
        imageTime.setText(imageInfo.getTime());
        imageName.setText(imageInfo.getName());
        imageSize.setText(imageInfo.getSize());
        imagePixel.setText(imageInfo.getPixel());
        imagePath.setText(path);
    }

    private void animIn(long startOffset) {
        AnimationUtil.showToolbar(this, mToolbar, startOffset);
        AnimationUtil.showBottomBar(this, mBottomBar, startOffset);
    }

    private void animOut(long startOffset) {
        AnimationUtil.hideToolbar(this, mToolbar, startOffset);
        AnimationUtil.hideBottomBar(this, mBottomBar, startOffset);
    }
}
