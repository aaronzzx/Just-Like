package com.aaron.justlike.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aaron.justlike.R;
import com.aaron.justlike.adapter.MainImageAdapter;
import com.aaron.justlike.util.AnimationUtil;
import com.aaron.justlike.util.FileUtils;
import com.aaron.justlike.util.SystemUtils;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

public class MainImageActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager mViewPager;
    private LinearLayout mBottomBar;
    private ImageView mShare;
    private ImageView mInfo;
    private ImageView mSetWallpaper;
    private ImageView mDelete;
    private Toolbar mToolbar;
    private int mPosition;
    private static final String[] CROP_ARRAY = {"适应屏幕", "自由裁剪"};
    private String mImageTime;
    private String mImageName;
    private String mImageSize;
    private String mImageResolution;
    private String mImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_image);
        initContent();
    }

    public int getCurrentPosition() {
        return mPosition;
    }

    public Toolbar getToolbar() {
        return mToolbar;
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
     * 按导航栏返回键直接终结 Activity
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_share:
                Intent share = new Intent(Intent.ACTION_VIEW);
                share.setDataAndType(FileUtils
                        .getImageContentUri(this, new File(mImagePath)), "image/*");
                startActivity(share);
                break;
            case R.id.action_info:
                // 设置图片详情的初始化
                View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_image_info, null);
                TextView imageTime = dialogView.findViewById(R.id.info_time);
                TextView imageName = dialogView.findViewById(R.id.info_name);
                TextView imageSize = dialogView.findViewById(R.id.info_size);
                TextView imageResolution = dialogView.findViewById(R.id.info_resolution);
                TextView imagePath = dialogView.findViewById(R.id.info_path);
                imageTime.setText(mImageTime);
                imageName.setText(mImageName);
                imageSize.setText(mImageSize);
                imageResolution.setText(mImageResolution);
                imagePath.setText(mImagePath);

                // 显示对话框
                new AlertDialog.Builder(this)
                        .setTitle("详情")
                        .setView(dialogView)
                        .show();
                break;
            case R.id.action_set_wallpaper:
                new AlertDialog.Builder(this)
                        .setTitle("设置壁纸")
                        .setItems(CROP_ARRAY, (dialog, which) -> {
                            switch (CROP_ARRAY[which]) {
                                case "适应屏幕":
                                    cropImage("default");
                                    break;
                                case "自由裁剪":
                                    cropImage("free");
                                    break;
                            }
                        }).show();
                break;
            case R.id.action_delete:
                new AlertDialog.Builder(this)
                        .setTitle("删除图片")
                        .setMessage("图片将从设备中删除")
                        .setPositiveButton("确定", (dialog, which) -> {
                            Intent intent = new Intent();
                            intent.putExtra("position", mPosition);
                            String path = MainActivity.getImageList().get(mPosition).getPath();
                            intent.putExtra("path", path);
                            setResult(RESULT_OK, intent);
                            finish();
                        })
                        .setNegativeButton("取消", (dialog, which) -> {
                        }).show();
                break;
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

    /**
     * 初始化界面
     */
    private void initContent() {
        // 获取从适配器序列化过来的值
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mPosition = bundle.getInt("position");
        }
        mToolbar = findViewById(R.id.activity_display_image_toolbar);
        mBottomBar = findViewById(R.id.bottom_bar);
        mShare = findViewById(R.id.action_share);
        mInfo = findViewById(R.id.action_info);
        mSetWallpaper = findViewById(R.id.action_set_wallpaper);
        mDelete = findViewById(R.id.action_delete);
        mShare.setOnClickListener(this);
        mInfo.setOnClickListener(this);
        mSetWallpaper.setOnClickListener(this);
        mDelete.setOnClickListener(this);
        setTitle();
        setSupportActionBar(mToolbar);
        // 启用标题栏的返回键
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        AnimationUtil.exitFullScreen(this, mToolbar, 200);
        AnimationUtil.setBottomBar(mBottomBar, "show", 200, mShare,
                mInfo, mSetWallpaper, mDelete);

        mViewPager = findViewById(R.id.activity_display_image_vp);
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setPageMargin(50);
        MainImageAdapter pagerAdapter = new MainImageAdapter(this, MainActivity.getImageList());
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setCurrentItem(mPosition);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mPosition = mViewPager.getCurrentItem();
                setTitle();
            }

            @Override
            public void onPageSelected(int position) {
                mPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void setTitle() {
        ExifInterface exif = null;
        mImagePath = MainActivity.getImageList().get(mPosition).getPath();
        mImageName = mImagePath.substring(mImagePath.lastIndexOf("/") + 1);
        float size = (float) FileUtils.getFileSize(mImagePath) / 1024 / 1024;
        mImageSize = String.valueOf(size);
        mImageSize = mImageSize.substring(0, 4) + " MB";
        int[] resolution = FileUtils.getImageWidthHeight(mImagePath);
        mImageResolution = String.valueOf(resolution[0]) + " x " + String.valueOf(resolution[1]);
        try {
            exif = new ExifInterface(MainActivity.getImageList().get(mPosition).getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] dateArray;
        String originalDate = null;
        if (exif != null) {
            originalDate = exif.getAttribute(ExifInterface.TAG_DATETIME);
        }
        if (!TextUtils.isEmpty(originalDate)) {
            dateArray = originalDate.split(" ");
            mImageTime = originalDate;
        } else {
            mImageTime = SystemUtils.getLastModified(mImagePath, "yyyy-MM-dd HH:mm:ss");
            dateArray = mImageTime.split(" ");
        }
        mToolbar.setTitle(dateArray[0]);
    }

    private void cropImage(String type) {
        // 源文件位置
        Uri sourceUri = FileUtils.getUriFromPath(this, new File(mImagePath));
        File file = new File(getCacheDir(), "Cropped-Wallpaper.JPG");
        try {
            if (file.exists()) {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }
            //noinspection ResultOfMethodCallIgnored
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 需要输出的位置
        Uri destinationUri = Uri.fromFile(file);
        // 设置裁剪页面主题
        UCrop.Options options = new UCrop.Options();
        options.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        options.setToolbarColor(getResources().getColor(R.color.colorPrimary));
        options.setActiveWidgetColor(getResources().getColor(R.color.colorPrimary));
        if (type.equals("default")) {
            // 获取设备分辨率
            int[] resolutionArray = SystemUtils.getResolution(getWindowManager());
            // 打开默认裁剪页面
            UCrop.of(sourceUri, destinationUri)
                    .withAspectRatio(resolutionArray[0], resolutionArray[1])
                    .withOptions(options)
                    .start(this);
        } else if (type.equals("free")) {
            // 打开自由裁剪页面
            UCrop.of(sourceUri, destinationUri)
                    .withOptions(options)
                    .start(this);
        }
    }
}
