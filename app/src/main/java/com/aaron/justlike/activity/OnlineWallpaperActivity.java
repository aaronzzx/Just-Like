package com.aaron.justlike.activity;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.aaron.justlike.R;
import com.aaron.justlike.adapter.OnlineImageAdapter;
import com.aaron.justlike.another.Splash;
import com.aaron.justlike.util.AnimationUtil;
import com.aaron.justlike.util.LogUtil;
import com.aaron.justlike.util.SystemUtils;
import com.kc.unsplash.Unsplash;
import com.kc.unsplash.models.Photo;
import com.kc.unsplash.models.SearchResults;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class OnlineWallpaperActivity extends AppCompatActivity implements View.OnClickListener {

    private final Unsplash unsplash = new Unsplash("936a1449161e2845eff4da43b160cea25e234a32188cc16c981e997590c65086");
    private RecyclerView mRecyclerView;
    private GridLayoutManager mLayoutManager;
    private OnlineImageAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefresh;
    private Toolbar mToolbar;
    private ProgressBar mProgressBar;
    private boolean canScrollVertical;
    private int mLoadNum = 1;
    private List<Splash> mSplashList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_wallpaper);
        initViews(); // 初始化控件
//        StatusBarUtil.setTransparentForDrawerLayout(this, mParent); // 修改状态栏
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        getWindow().getDecorView()
                .setSystemUiVisibility(/*View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | */View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * 设置可以打开菜单
     *
     * @param item item 传入的 View 实例
     * @return 返回 true 才执行
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                mParent.openDrawer(GravityCompat.START);
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_online_toolbar:
                scrollToTop();
                break;
        }
    }

    /**
     * 滑动到指定位置
     */
    public void scrollToTop() {
        int firstItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
        if (firstItem >= 15) {
            mRecyclerView.scrollToPosition(9);
        }
        mRecyclerView.smoothScrollToPosition(0);
    }

    private void initViews() {
        mSwipeRefresh = findViewById(R.id.swipe_refresh);
        mSwipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        mToolbar = findViewById(R.id.activity_online_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setOnClickListener(this);
        ActionBar actionBar = getSupportActionBar();
        /*
         * 让标题栏启用滑动菜单并设置图标
         */
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_back);
        }
        mProgressBar = findViewById(R.id.progress_bar);
        AnimationUtil.showProgressBar(mProgressBar);
        mRecyclerView = findViewById(R.id.recycler_view);
        // 将 RecyclerView 的布局风格改为网格类型,使用自定义的布局管理器，为了能修改滑动状态
        mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new XItemDecoration());
        mAdapter = new OnlineImageAdapter(this, mSplashList);
        mRecyclerView.setAdapter(mAdapter);
        loadUnsplash();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    canScrollVertical = mRecyclerView.canScrollVertically(1);
                    if (!canScrollVertical) {
                        loadUnsplash();
                    }
                }
            }
        });
    }

    private void loadUnsplash() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                unsplash.searchPhotos("wallpapers", mLoadNum, 10, new Unsplash.OnSearchCompleteListener() {
                    @Override
                    public void onComplete(SearchResults results) {
                        mLoadNum++;
                        AnimationUtil.hideProgressBar(mProgressBar);
                        List<Photo> photoList = results.getResults();
                        for (Photo photo : photoList) {
                            mSplashList.add(new Splash(photo));
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(OnlineWallpaperActivity.this,
                                "出错了", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    public class XItemDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) % 2 == 0) {
                outRect.left = 0;
                outRect.right = SystemUtils.dp2px(OnlineWallpaperActivity.this, 3.0F);
            } else {
                outRect.left = SystemUtils.dp2px(OnlineWallpaperActivity.this, 3.0F);
            }
        }
    }
}
