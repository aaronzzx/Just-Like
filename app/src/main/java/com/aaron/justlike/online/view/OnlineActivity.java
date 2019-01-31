package com.aaron.justlike.online.view;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.aaron.justlike.R;
import com.aaron.justlike.extend.MyGridLayoutManager;
import com.aaron.justlike.online.OnlineAdapter.OnlineAdapter;
import com.aaron.justlike.online.entity.PhotoEvent;
import com.aaron.justlike.online.presenter.IOnlinePresenter;
import com.aaron.justlike.online.presenter.OnlinePresenter;
import com.aaron.justlike.util.SystemUtils;
import com.google.android.material.snackbar.Snackbar;
import com.kc.unsplash.models.Photo;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class OnlineActivity extends AppCompatActivity implements IOnlineView<Photo>,
        View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
        OnlineAdapter.Callback<Photo> {

    private IOnlinePresenter mPresenter;

    private Toolbar mToolbar;
    private SwipeRefreshLayout mSwipeRefresh;
    private RecyclerView mRecyclerView;
    private MyGridLayoutManager mLayoutManager;
    private OnlineAdapter mAdapter;
    private ProgressBar mProgressBar;
    private ProgressBar mFooterProgress;

    private List<Photo> mPhotoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);
        initView();
        attachPresenter();
        mPresenter.requestImage(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_online_toolbar:
                backToTop();
                break;
        }
    }

    @Override
    public void onRefresh() {
        mPresenter.requestImage(true);
    }

    /**
     * OnlineAdapter 回调
     */
    @Override
    public void onPress(Photo photo) {
        EventBus.getDefault().postSticky(new PhotoEvent(photo));
        startActivity(new Intent(this, PreviewActivity.class));
    }

    @Override
    public void attachPresenter() {
        mPresenter = new OnlinePresenter(this);
    }

    @Override
    public void onShowImage(List<Photo> list) {
        mPhotoList.clear();
        mPhotoList.addAll(list);
        mAdapter.notifyItemRangeChanged(0, mPhotoList.size());
    }

    @Override
    public void onShowMore(List<Photo> list) {
        mPhotoList.addAll(list);
        mAdapter.notifyItemRangeInserted(mPhotoList.size() - list.size(), list.size());
    }

    @Override
    public void onShowMessage(int mode, String args) {
        Snackbar.make(mToolbar, args, Snackbar.LENGTH_SHORT).setAction("刷新", v -> {
            switch (mode) {
                case OnlinePresenter.REQUEST_IMAGE:
                    mPresenter.requestImage(true);
                    break;
                case OnlinePresenter.LOAD_MORE:
                    mPresenter.requestLoadMore();
                    break;
            }
        }).show();
    }

    @Override
    public void onHideRefresh() {
        mSwipeRefresh.setRefreshing(false);
    }

    @Override
    public void onHideProgress() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onShowLoading() {
        mFooterProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void onHideLoading() {
        mFooterProgress.setVisibility(View.GONE);
    }

    private void initView() {
        mToolbar = findViewById(R.id.activity_online_toolbar);
        mSwipeRefresh = findViewById(R.id.swipe_refresh_home_activity_main);
        mRecyclerView = findViewById(R.id.rv_home_activity_main);
        mProgressBar = findViewById(R.id.progress_bar);
        mFooterProgress = findViewById(R.id.footer_progress);

        mToolbar.setOnClickListener(this);
        mSwipeRefresh.setOnRefreshListener(this);

        initToolbar();
        mSwipeRefresh.setColorSchemeResources(R.color.colorBlack);
        initRecyclerView();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_back);
        }
    }

    private void initRecyclerView() {
        mLayoutManager = new MyGridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new XItemDecoration());
        mAdapter = new OnlineAdapter(mPhotoList, this);
        mRecyclerView.setAdapter(mAdapter);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mAdapter.isFooterView(position) ? mLayoutManager.getSpanCount() : 1;
            }
        });
        // 监听是否滑到底部
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    boolean canScrollVertical = mRecyclerView.canScrollVertically(1);
                    if (!canScrollVertical) {
                        mPresenter.requestLoadMore();
                    }
                }
            }
        });
    }

    private void backToTop() {
        int firstItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
        if (firstItem >= 30) {
            mRecyclerView.scrollToPosition(22);
        }
        mRecyclerView.smoothScrollToPosition(0);
    }

    private class XItemDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) % 2 == 0) {
                outRect.right = SystemUtils.dp2px(OnlineActivity.this, 4.0F);
            } else if (parent.getChildAdapterPosition(view) % 2 == 1) {
                outRect.left = SystemUtils.dp2px(OnlineActivity.this, 4.0F);
            }
        }
    }
}
