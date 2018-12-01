package com.aaron.justlike.activity;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.aaron.justlike.R;
import com.aaron.justlike.adapter.OnlineImageAdapter;
import com.aaron.justlike.util.AnimationUtil;
import com.aaron.justlike.util.LogUtil;
import com.aaron.justlike.util.SystemUtils;
import com.google.android.material.snackbar.Snackbar;
import com.jaeger.library.StatusBarUtil;
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

    private static final Unsplash unsplash = new Unsplash("936a1449161e2845eff4da43b160cea25e234a32188cc16c981e997590c65086");
    private RecyclerView mRecyclerView;
    private GridLayoutManager mLayoutManager;
    private OnlineImageAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefresh;
    private Toolbar mToolbar;
    private ProgressBar mProgressBar;
    private boolean canScrollVertical;
    private int mLoadNum = 1;
    private List<Photo> mPhotoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorBlack), 0);
        initViews(); // 初始化控件
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
        mSwipeRefresh.setColorSchemeResources(R.color.colorBlack);
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
        mSwipeRefresh.setEnabled(false);
        mRecyclerView = findViewById(R.id.recycler_view);

        mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new XItemDecoration());
        mAdapter = new OnlineImageAdapter(this, mPhotoList);
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
        unsplash.getPhotos(mLoadNum++, 10, Order.LATEST, new Unsplash.OnPhotosLoadedListener() {
            @Override
            public void onComplete(List<Photo> photos) {
//                AnimationUtil.hideProgressBar(mProgressBar);
                mProgressBar.setVisibility(View.GONE);
                mSwipeRefresh.setEnabled(true);
                mPhotoList.addAll(photos);
                mAdapter.notifyDataSetChanged();
                if (mSwipeRefresh.isRefreshing()) {
                    mSwipeRefresh.setRefreshing(false);
                }
                if ((mLoadNum - 1) % 2 == 0) {
                    return;
                }
                loadUnsplash();
            }

            @SuppressLint("ShowToast")
            @Override
            public void onError(String error) {
//                AnimationUtil.hideProgressBar(mProgressBar);
                mProgressBar.setVisibility(View.GONE);
                mSwipeRefresh.setEnabled(true);
                Snackbar.make(mRecyclerView, "加载失败，请检查网络", Snackbar.LENGTH_LONG)
                .setAction("刷新", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                            AnimationUtil.showProgressBar(mProgressBar);
                        mProgressBar.setVisibility(View.VISIBLE);
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
//                outRect.left = 0;
                outRect.right = SystemUtils.dp2px(OnlineActivity.this, 3.0F);
            } else if (parent.getChildAdapterPosition(view) % 2 == 1) {
                outRect.left = SystemUtils.dp2px(OnlineActivity.this, 3.0F);
            }
        }
    }
}
