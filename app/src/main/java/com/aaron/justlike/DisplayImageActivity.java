package com.aaron.justlike;

import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DisplayImageActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private int mPosition;
    private static final int SET_WALLPAPER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
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
        finish();
        return super.onSupportNavigateUp();
    }

    /**
     * 按导航栏返回键直接终结 Activity
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /**
     * 使用透明状态栏并使状态栏字体颜色反转
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
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    /**
     * 创建删除菜单
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_display_image_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 创建菜单点击事件
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_wallpaper:
                WallpaperManager manager = WallpaperManager.getInstance(this);
                Uri uri = MainActivity.getUriList().get(mPosition);
                if (manager != null && uri != null) {
                    String path = uri.getPath();
                    String absolutePath = FileUtils.getAbsolutePath(path);
                    File file = new File(absolutePath);
                    FileInputStream fis = null;
                    try {
                        fis = new FileInputStream(file);
                        manager.setStream(fis);
                        Toast.makeText(this, "设置成功", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (fis != null) fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case R.id.action_delete:
                new AlertDialog.Builder(this)
                        .setTitle("Warning")
                        .setMessage("确定删除图片吗？")
                        .setIcon(R.mipmap.ic_warn)
                        .setCancelable(false)
                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.putExtra("position", mPosition);
                                String fileName = MainActivity.getFileNameList().get(mPosition);
                                intent.putExtra("fileName", fileName);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SET_WALLPAPER:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, "设置成功", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * 初始化界面
     */
    private void initContent() {
        mToolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(mToolbar);
        // 启用标题栏的返回键
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_back);
        }
        // 获取从适配器序列化过来的 Image 对象，并取值
        Image image = getIntent().getParcelableExtra("image");
        mPosition = getIntent().getIntExtra("position", 0);
        Uri imageUri = image.getUri();
        String path = imageUri.getPath(); // 获取原始路径
        String absolutePath = FileUtils.getAbsolutePath(path); // 获取绝对路径

        ViewPager viewPager = findViewById(R.id.activity_display_image_vp);
        viewPager.setOffscreenPageLimit(4);
        viewPager.setPageMargin(50);
        MyPagerAdapter adapter = new MyPagerAdapter(MainActivity.getUriList(),
                this, absolutePath);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(mPosition);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (mPosition > 0) {
                    mPosition = position + 1;
                }
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
}
