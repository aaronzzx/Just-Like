package com.aaron.justlike.activity;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.aaron.justlike.R;
import com.aaron.justlike.adapter.OnlineImageAdapter;
import com.aaron.justlike.extend.MyGridLayoutManager;
import com.aaron.justlike.util.AnimationUtil;
import com.aaron.justlike.util.SystemUtils;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.kc.unsplash.Unsplash;
import com.kc.unsplash.api.Order;
import com.kc.unsplash.models.Photo;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class OnlineActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String CLIENT_ID = "18db24a3d59a1b2633897fa63f3f49455c2cbfa8a22e5b8520141cb2660fa816";
    private static final Unsplash unsplash = new Unsplash(CLIENT_ID);
    private RecyclerView mRecyclerView;
    private MyGridLayoutManager mLayoutManager;
    private OnlineImageAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefresh;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private ProgressBar mProgressBar;
    private ProgressBar mFooterProgress;
    private boolean canScrollVertical;
    private int mLoadNum = 1;
    private List<Photo> mPhotoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);
        initViews(); // 初始化控件
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        if (!SystemUtils.isViewVisible(mToolbar)) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
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
        if (firstItem >= 30) {
            mRecyclerView.scrollToPosition(22);
        }
        mRecyclerView.smoothScrollToPosition(0);
    }

    private void initViews() {
        mSwipeRefresh = findViewById(R.id.swipe_refresh);
        mSwipeRefresh.setColorSchemeResources(R.color.colorBlack);
        mAppBarLayout = findViewById(R.id.appbar_layout);
        mToolbar = findViewById(R.id.activity_online_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setOnClickListener(this);
        ActionBar actionBar = getSupportActionBar();
        // 让标题栏启用滑动菜单并设置图标
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_back);
        }
        mProgressBar = findViewById(R.id.progress_bar);
        mFooterProgress = findViewById(R.id.footer_progress);
        mSwipeRefresh.setEnabled(false);
        mRecyclerView = findViewById(R.id.recycler_view);

        mLayoutManager = new MyGridLayoutManager(this, 2);
        mLayoutManager.setScrollEnabled(false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new XItemDecoration());
        mAdapter = new OnlineImageAdapter(this, mPhotoList);
        mRecyclerView.setAdapter(mAdapter);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mAdapter.isFooterView(position) ? mLayoutManager.getSpanCount() : 1;
            }
        });
        AnimationUtil.showViewByAlpha(mRecyclerView, 0, 1, 1000);
        loadUnsplash();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    canScrollVertical = mRecyclerView.canScrollVertically(1);
                    if (!canScrollVertical) {
                        mFooterProgress.setVisibility(View.VISIBLE);
                        loadUnsplash();
                    }
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
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mLoadNum = 1;
                mPhotoList.clear();
                loadUnsplash();
            }
        });
    }

    private void loadUnsplash() {
        unsplash.getPhotos(mLoadNum++, 30, Order.LATEST, new Unsplash.OnPhotosLoadedListener() {
            @Override
            public void onComplete(List<Photo> photos) {
                mSwipeRefresh.setEnabled(true);
                mPhotoList.addAll(photos);
                if (mLoadNum == 1) {
                    mAdapter.notifyDataSetChanged();
                } else {
                    mAdapter.notifyItemRangeInserted(mPhotoList.size(), 30);
                }
                mLayoutManager.setScrollEnabled(true);
                mFooterProgress.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);
                if (mSwipeRefresh.isRefreshing()) {
                    mSwipeRefresh.setRefreshing(false);
                }
            }

            @Override
            public void onError(String error) {
                mFooterProgress.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);
                mSwipeRefresh.setEnabled(true);
                Snackbar.make(mRecyclerView, "加载失败，请检查网络", Snackbar.LENGTH_LONG)
                .setAction("刷新", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                            AnimationUtil.showProgressBar(mProgressBar);
//                        mProgressBar.setVisibility(View.VISIBLE);
                        mSwipeRefresh.setRefreshing(true);
                        loadUnsplash();
                    }
                }).show();
                if (mSwipeRefresh.isRefreshing()) {
                    mSwipeRefresh.setRefreshing(false);
                }
            }
        });
    }

    public class XItemDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) % 2 == 0) {
                outRect.right = SystemUtils.dp2px(OnlineActivity.this, 4.0F);
            } else if (parent.getChildAdapterPosition(view) % 2 == 1) {
                outRect.left = SystemUtils.dp2px(OnlineActivity.this, 4.0F);
            }
        }
    }
}
