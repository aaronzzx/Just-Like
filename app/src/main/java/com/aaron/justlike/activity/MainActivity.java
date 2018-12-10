package com.aaron.justlike.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aaron.justlike.R;
import com.aaron.justlike.adapter.MainImageAdapter;
import com.aaron.justlike.another.Image;
import com.aaron.justlike.extend.GlideEngine;
import com.aaron.justlike.extend.MyGridLayoutManager;
import com.aaron.justlike.util.FileUtils;
import com.aaron.justlike.util.LogUtil;
import com.aaron.justlike.util.SystemUtils;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.jaeger.library.StatusBarUtil;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_CHOOSE = 10;
    private static final int REQUEST_PERMISSION = 1;
    private static final int DELETE_PHOTO = 2;
    private static MyGridLayoutManager mLayoutManager;
    private static List<String> mFileNameList = new ArrayList<>(); // 详情页删除图片时的图片名称集合
    private static List<Image> mImageList = new ArrayList<>(); // 定义存放 Image 实例的 List 集合
    private int mAsciiNum = 64; // 相当于大写 A
    private int mIsFinish = 0; // 用于判断返回键退出程序
    private int mScrollFlags = 0; // AppBar 滑动属性
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefresh;
    private MainImageAdapter mAdapter; // 声明一个 Image 适配器
    private DrawerLayout mParent;
    private NavigationView mNavView;
    private MenuItem mSortByDate;
    private MenuItem mSortByName;
    private MenuItem mSortBySize;
    private MenuItem mSortByOrder;
    private String[] type = {"jpg", "jpeg", "png", "gif"};

    public static List<String> getFileNameList() {
        return mFileNameList;
    }

    public static List<Image> getImageList() {
        return mImageList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme); // 由于设置了启动页，需要在这里将主题改回来
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews(); // 初始化控件
        StatusBarUtil.setTransparentForDrawerLayout(this, mParent); // 修改状态栏
        requestWritePermission(); // 申请存储权限
        // 加载存储在程序外部目录的图片
        FileUtils.getLocalCache(this, mImageList, true, type);
        FileUtils.getLocalCache(this, mImageList, false, type);
        sortForInit();
        mAdapter.notifyDataSetChanged();
        LinearLayout parentOfToolbar = findViewById(R.id.activity_main_linear_layout);
        AppBarLayout.LayoutParams layoutParams = (AppBarLayout.LayoutParams) parentOfToolbar.getLayoutParams();
        if (mImageList.size() < 19) {
            layoutParams.setScrollFlags(0);
        }
        mScrollFlags = layoutParams.getScrollFlags();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mNavView.setCheckedItem(R.id.nav_home);
//        addHintOnBackground();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mIsFinish == 1) {
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        View decorView = getWindow().getDecorView();
        if (!SystemUtils.isViewVisible(mToolbar)) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    /**
     * 浮动按钮的点击事件
     *
     * @param v 传入的 View 实例
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                openAlbum();
                break;
            case R.id.activity_main_toolbar:
                scrollToTop();
                break;
        }
    }

    /**
     * 滑动到指定位置
     */
    public void scrollToTop() {
        int firstItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
        if (firstItem >= 48) {
            mRecyclerView.scrollToPosition(25);
        }
        mRecyclerView.smoothScrollToPosition(0);
    }

    /**
     * 创建菜单
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        mSortByDate = menu.findItem(R.id.sort_date);
        mSortByName = menu.findItem(R.id.sort_name);
        mSortBySize = menu.findItem(R.id.sort_size);
        mSortByOrder = menu.findItem(R.id.sort_order);
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        int mode_sort = preferences.getInt("mode_sort", 0);
        int order_or_reverse = preferences.getInt("order_or_reverse", 0);
        if (mode_sort == 1) {
            mSortByDate.setChecked(true);
        } else if (mode_sort == 2) {
            mSortByName.setChecked(true);
        } else if (mode_sort == 3) {
            mSortBySize.setChecked(true);
        } else {
            mSortByDate.setChecked(true);
        }
        if (order_or_reverse == 1) {
            mSortByOrder.setChecked(true);
        } else if (order_or_reverse == 2) {
            mSortByOrder.setChecked(false);
        } else {
            mSortByOrder.setChecked(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 设置可以打开菜单
     *
     * @param item item 传入的 View 实例
     * @return 返回 true 才执行
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        switch (item.getItemId()) {
            case android.R.id.home:
                mParent.openDrawer(GravityCompat.START);
                break;
            case R.id.sort_date:
                item.setChecked(true);
                if (mSortByOrder.isChecked()) {
                    FileUtils.sortByDate(mImageList, true);
                } else {
                    FileUtils.sortByDate(mImageList, false);
                }
                mAdapter.notifyDataSetChanged();
                editor.putInt("mode_sort", 1);
                editor.apply();
                break;
            case R.id.sort_name:
                item.setChecked(true);
                if (mSortByOrder.isChecked()) {
                    FileUtils.sortByName(mImageList, true);
                } else {
                    FileUtils.sortByName(mImageList, false);
                }
                mAdapter.notifyDataSetChanged();
                editor.putInt("mode_sort", 2);
                editor.apply();
                break;
            case R.id.sort_size:
                item.setChecked(true);
                if (mSortByOrder.isChecked()) {
                    FileUtils.sortBySize(mImageList, true);
                } else {
                    FileUtils.sortBySize(mImageList, false);
                }
                mAdapter.notifyDataSetChanged();
                editor.putInt("mode_sort", 3);
                editor.apply();
                break;
            case R.id.sort_order:
                if (item.isChecked()) {
                    item.setChecked(false);
                    if (mSortByDate.isChecked()) {
                        FileUtils.sortByDate(mImageList, false);
                    } else if (mSortByName.isChecked()) {
                        FileUtils.sortByName(mImageList, false);
                    } else {
                        FileUtils.sortBySize(mImageList, false);
                    }
                    mAdapter.notifyDataSetChanged();
                    editor.putInt("order_or_reverse", 2);
                    editor.apply();
                } else {
                    item.setChecked(true);
                    if (mSortByDate.isChecked()) {
                        FileUtils.sortByDate(mImageList, true);
                    } else if (mSortByName.isChecked()) {
                        FileUtils.sortByName(mImageList, true);
                    } else {
                        FileUtils.sortBySize(mImageList, true);
                    }
                    mAdapter.notifyDataSetChanged();
                    editor.putInt("order_or_reverse", 1);
                    editor.apply();
                }
                break;
        }
        return true;
    }

    /**
     * 设置当滑动菜单打开时按返回键不会直接退出程序
     */
    @Override
    public void onBackPressed() {
        if (mParent.isDrawerOpen(GravityCompat.START)) {
            mParent.closeDrawer(GravityCompat.START);
        } else {
            if (mIsFinish == 1) {
                super.onBackPressed();
            }
            mIsFinish = 1;
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                        mIsFinish = 0;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    /**
     * 在点击按钮后首先检查是否已赋予权限，没有则申请，有则直接打开文件管理器
     */
    private void requestWritePermission() {
        // 判断是否已经获得权限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 申请读写存储的权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        }
    }

    /**
     * 申请权限后的回调方法，根据请求码判断是哪个权限的申请，然后根据传入的申请结果判断
     * 是否打开文件管理器，如果没赋予权限，程序将直接退出。
     *
     * @param requestCode  请求码
     * @param permissions  所申请的权限
     * @param grantResults 申请结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION:
                if (!(grantResults.length > 0 && grantResults[0]
                        == PackageManager.PERMISSION_GRANTED)) {
                    finish(); // 如果不授予权限，程序直接退出。
                }
                break;
        }
    }

    /**
     * 将打开文件管理器的代码封装在此方法内，方便调用和使代码简洁。
     */
    private void openAlbum() {
        Matisse.from(MainActivity.this)
                .choose(MimeType.ofImage())
                .showSingleMediaType(true)
                .countable(true)
                .maxSelectable(9)
//                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(SystemUtils.dp2px(this, 120.0F))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .theme(R.style.MatisseTheme)
                .forResult(REQUEST_CODE_CHOOSE);
    }

    /**
     * startActivityForResult() 的回调方法
     *
     * @param requestCode 请求码
     * @param resultCode  结果码
     * @param data        传回来的数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_CHOOSE:
                if (resultCode == Activity.RESULT_OK) {
                    mAsciiNum = 0;
                    final List<String> selectList = Matisse.obtainPathResult(data);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (String path : selectList) {
                                String fileName = path.substring(path.lastIndexOf("/") + 1);

                                mFileNameList.add(fileName);
                                // 通知适配器更新并将文件添加至缓存
                                mImageList.add(new Image(path));
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mAdapter.notifyDataSetChanged();
                                    }
                                });
                                if (mAsciiNum > 9) {
                                    mAsciiNum = 0;
                                }
                                mAsciiNum++;
                                FileUtils.saveToCache(MainActivity.this, path, mAsciiNum);
                            }
                        }
                    }).start();
                }
                break;
            case DELETE_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    int position = data.getIntExtra("position", 0);
                    String fileName = data.getStringExtra("fileName");
                    LogUtil.d("MainActivity", fileName);
                    mImageList.remove(position);
                    mFileNameList.remove(position);
                    mAdapter.notifyItemRemoved(position);
                    FileUtils.deleteFile(this, "/" + fileName);
                }
                break;
        }
    }

    private void sortForInit() {
        int mode_sort = getPreferences(MODE_PRIVATE).getInt("mode_sort", 0);
        int order_or_reverse = getPreferences(MODE_PRIVATE).getInt("order_or_reverse", 0);
        if (mode_sort != 0 | order_or_reverse != 0) {
            switch (mode_sort) {
                case 1:
                    if (order_or_reverse == 1) {
                        FileUtils.sortByDate(mImageList, true);
                    } else if (order_or_reverse == 2) {
                        FileUtils.sortByDate(mImageList, false);
                    } else {
                        FileUtils.sortByDate(mImageList, true);
                    }
                    break;
                case 2:
                    if (order_or_reverse == 1) {
                        FileUtils.sortByName(mImageList, true);
                    } else if (order_or_reverse == 2) {
                        FileUtils.sortByName(mImageList, false);
                    } else {
                        FileUtils.sortByName(mImageList, true);
                    }
                    break;
                case 3:
                    if (order_or_reverse == 1) {
                        FileUtils.sortBySize(mImageList, true);
                    } else if (order_or_reverse == 2) {
                        FileUtils.sortBySize(mImageList, false);
                    } else {
                        FileUtils.sortBySize(mImageList, true);
                    }
                    break;
            }
        } else {
            FileUtils.sortByDate(mImageList, true);
        }
    }

    private void sort() {
        if (mSortByOrder.isChecked()) {
            if (mSortByDate.isChecked()) {
                FileUtils.sortByDate(mImageList, true);
            } else if (mSortByName.isChecked()) {
                FileUtils.sortByName(mImageList, true);
            } else {
                FileUtils.sortBySize(mImageList, true);
            }
        } else {
            if (mSortByDate.isChecked()) {
                FileUtils.sortByDate(mImageList, false);
            } else if (mSortByName.isChecked()) {
                FileUtils.sortByName(mImageList, false);
            } else {
                FileUtils.sortBySize(mImageList, false);
            }
        }
    }

    /**
     * 将控件的初始化代码封装在此方法中，方便调用并使代码简洁。
     */
    private void initViews() {
        mAppBarLayout = findViewById(R.id.activity_main_appbar_layout);
        mToolbar = findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setOnClickListener(this);
        mParent = findViewById(R.id.drawer_layout);
        mNavView = findViewById(R.id.nav_view);
        ActionBar actionBar = getSupportActionBar();
        /*
         * 让标题栏启用滑动菜单并设置图标
         */
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_drawer_menu);
        }
        final FloatingActionButton fab = findViewById(R.id.fab); // 浮动按钮
        fab.setOnClickListener(this);

        mRecyclerView = findViewById(R.id.recycler_view);
        // 将 RecyclerView 的布局风格改为网格类型,使用自定义的布局管理器，为了能修改滑动状态
        mLayoutManager = new MyGridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MainImageAdapter(this, mImageList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new XItemDecoration());
        mRecyclerView.addItemDecoration(new YItemDecoration());
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                View decorView = getWindow().getDecorView();
                if (dy > 0) {
                    if (mScrollFlags == 21) {
                        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                    }
                    fab.hide();
                } else if (dy < 0) {
                    if (mScrollFlags == 21) {
                        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                    }
                    fab.show();
                }
            }
        });

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                View decorView = getWindow().getDecorView();
                if (SystemUtils.isViewVisible(mToolbar)) {
                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                } else {
                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                }
            }
        });

        mNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        mParent.closeDrawers();
                        break;
                    case R.id.nav_online_wallpaper:
                        mParent.closeDrawers();
                        mParent.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                            @Override
                            public void onDrawerClosed(View drawerView) {
                                Intent intent = new Intent(MainActivity.this,
                                        OnlineActivity.class);
                                startActivity(intent);
                                mParent.removeDrawerListener(this);
                            }
                        });
                        break;
                    case R.id.nav_about:
                        mParent.closeDrawers();
                        mParent.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                            @Override
                            public void onDrawerClosed(View drawerView) {
                                startActivity(new Intent(MainActivity.this,
                                        AboutActivity.class));
                                mParent.removeDrawerListener(this);
                            }
                        });
                        break;
                    default:
                        Toast.makeText(MainActivity.this,
                                "暂未开放", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });
        mSwipeRefresh = findViewById(R.id.swipe_refresh);
        mSwipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mLayoutManager.setScrollEnabled(false);
                mAdapter.setBanClick(true);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(600);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refreshRecyclerFirst();
                                refreshRecyclerLast();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Thread.sleep(400);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        mLayoutManager.setScrollEnabled(true);
                                        mAdapter.setBanClick(false);
                                    }
                                }).start();
                            }
                        });
                    }
                }).start();
            }
        });
    }

    private void refreshRecyclerFirst() {
        TranslateAnimation ta = new TranslateAnimation(0, 0, 0, 2160);
        ta.setDuration(250);
        ta.setFillAfter(true);
        mRecyclerView.startAnimation(ta);
    }

    private void refreshRecyclerLast() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mImageList.clear();
                    mFileNameList.clear();
                    FileUtils.getLocalCache(MainActivity.this, mImageList, true, type);
                    FileUtils.getLocalCache(MainActivity.this, mImageList, false, type);
                    sort();
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                        mSwipeRefresh.setRefreshing(false);
                        TranslateAnimation ta = new TranslateAnimation(0, 0, 2160, 0);
                        ta.setDuration(250);
                        mRecyclerView.startAnimation(ta);
//                        addHintOnBackground();
                    }
                });
            }
        }).start();
    }

    public void addHintOnBackground() {
        TextView hint = findViewById(R.id.hint);
        if (mImageList.isEmpty()) {
            AnimationSet as = new AnimationSet(true);
            as.setStartOffset(150);
            as.setDuration(500);
            RotateAnimation ra = new RotateAnimation(0, 720,
                    Animation.RELATIVE_TO_SELF, 0.5F,
                    Animation.RELATIVE_TO_SELF, 0.5F);
            ScaleAnimation sa = new ScaleAnimation(0, 1, 0, 1);
            as.addAnimation(ra);
            as.addAnimation(sa);
            hint.startAnimation(as);
            hint.setVisibility(View.VISIBLE);
        } else {
            hint.setVisibility(View.GONE);
        }
    }

    public class XItemDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) % 3 == 0) {
                outRect.left = 0;
                outRect.right = SystemUtils.dp2px(MainActivity.this, 2.5F);
            } else if (parent.getChildAdapterPosition(view) % 3 == 1) {
                outRect.left = SystemUtils.dp2px(MainActivity.this, 1.5F);
                outRect.right = SystemUtils.dp2px(MainActivity.this, 1.5F);
            } else if (parent.getChildAdapterPosition(view) % 3 == 2) {
                outRect.left = SystemUtils.dp2px(MainActivity.this, 2.5F);
                outRect.right = 0;
            }
        }
    }

    public class YItemDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int size = mImageList.size();
            outRect.top = 0;
            outRect.bottom = SystemUtils.dp2px(MainActivity.this, 4.0F);
            if (parent.getChildAdapterPosition(view) == size - 1) {
                outRect.bottom = -1;
            } else if (parent.getChildAdapterPosition(view) == size - 2) {
                outRect.bottom = -1;
            } else if (parent.getChildAdapterPosition(view) == size - 3) {
                outRect.bottom = -1;
            }
        }
    }
}
