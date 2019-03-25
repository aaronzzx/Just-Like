package com.aaron.justlike.activity.online;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ProgressBar;

import com.aaron.justlike.R;
import com.aaron.justlike.adapter.online.OnlineAdapter;
import com.aaron.justlike.common.ThemeManager;
import com.aaron.justlike.entity.PhotoEvent;
import com.aaron.justlike.http.unsplash.entity.Photo;
import com.aaron.justlike.mvp.presenter.online.IOnlinePresenter;
import com.aaron.justlike.mvp.presenter.online.OnlinePresenter;
import com.aaron.justlike.mvp.view.online.IOnlineView;
import com.aaron.justlike.ui.MyGridLayoutManager;
import com.aaron.justlike.util.SystemUtils;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class OnlineActivity extends AppCompatActivity implements IOnlineView<Photo>,
        View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
        OnlineAdapter.Callback<Photo> {

    private IOnlinePresenter mPresenter;

    private ThemeManager.Theme mCurrentTheme;
    private int mColorPrimary;

    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ActionBar mActionBar;
    private Drawable mIconBack;
    private Drawable mIconSearch;
    private Drawable mIconFilter;
    private SwipeRefreshLayout mSwipeRefresh;
    private RecyclerView mRecyclerView;
    private View mErrorView;
    private MyGridLayoutManager mLayoutManager;
    private OnlineAdapter mAdapter;
    private ProgressBar mProgressBar;
    private ViewGroup mFooterProgress;

    private List<Photo> mPhotoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.getInstance().setTheme(this);
        mCurrentTheme = ThemeManager.getInstance().getCurrentTheme();
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
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Window window = getWindow();
        View decorView = window.getDecorView();
        if (hasFocus) {
            ThemeManager.Theme theme = ThemeManager.getInstance().getCurrentTheme();
            if (theme == null || theme == ThemeManager.Theme.WHITE) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                } else {
                    window.setStatusBarColor(getResources().getColor(R.color.status_bar_background));
                }
                mToolbar.setTitleTextColor(getResources().getColor(R.color.colorGreyText));
                mActionBar.setHomeAsUpIndicator(mIconBack);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_online_menu, menu);
        SystemUtils.setIconEnable(menu, true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
        if (mProgressBar.getVisibility() == View.GONE) {
            mPresenter.requestImage(true);
        }
    }

    /**
     * OnlineAdapter 回调
     */
    @Override
    public void onPress(Photo photo) {
        EventBus.getDefault().postSticky(new PhotoEvent(photo));
        startActivity(new Intent(this, PreviewActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void attachPresenter() {
        mPresenter = new OnlinePresenter(this);
    }

    @Override
    public void onShowImage(List<Photo> list) {
        mErrorView.setVisibility(View.GONE);
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
        if (mPhotoList.size() == 0) {
            mErrorView.setVisibility(View.VISIBLE);
        }
        Snackbar snackbar = Snackbar.make(mToolbar, args, Snackbar.LENGTH_SHORT);
        if (mCurrentTheme == null
                || mCurrentTheme == ThemeManager.Theme.BLACK) {
            snackbar.setActionTextColor(getResources().getColor(R.color.colorPrimaryWhite));
        } else if (mCurrentTheme == ThemeManager.Theme.WHITE) {
            snackbar.setActionTextColor(getResources().getColor(R.color.colorPrimaryWhite));
        }
        snackbar.setAction("刷新", v -> {
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
        if (!mSwipeRefresh.isEnabled())
            mSwipeRefresh.setEnabled(true);
        mSwipeRefresh.setRefreshing(false);
    }

    @Override
    public void onHideProgress() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onShowLoading() {
        mFooterProgress.setVisibility(View.VISIBLE);
        ScaleAnimation animation = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F);
        animation.setFillAfter(true);
        animation.setDuration(250);
        mFooterProgress.startAnimation(animation);
    }

    @Override
    public void onHideLoading() {
        new Handler().postDelayed(() -> {
            ScaleAnimation animation = new ScaleAnimation(1, 0, 1, 0, Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F);
            animation.setFillAfter(true);
            animation.setDuration(250);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mFooterProgress.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mFooterProgress.startAnimation(animation);
        }, 500);
    }

    private void initView() {
        mToolbar = findViewById(R.id.activity_online_toolbar);
        mTabLayout = findViewById(R.id.tab_layout);
        mSwipeRefresh = findViewById(R.id.swipe_refresh_home_activity_main);
        mRecyclerView = findViewById(R.id.recycler_view);
        mErrorView = findViewById(R.id.error_view);
        mProgressBar = findViewById(R.id.progress_bar);
        mFooterProgress = findViewById(R.id.footer_progress);

        mToolbar.setOnClickListener(this);
        mSwipeRefresh.setOnRefreshListener(this);

        initIconColor();
        initTheme();
        initToolbar();
        initRecyclerView();
        mSwipeRefresh.setColorSchemeColors(mColorPrimary);
        mSwipeRefresh.setEnabled(false);
    }

    private void initIconColor() {
        if (ThemeManager.getInstance().getCurrentTheme() == null
                || ThemeManager.getInstance().getCurrentTheme() == ThemeManager.Theme.WHITE) {
            mTabLayout.setTabTextColors(getResources().getColor(R.color.colorAccentWhite), getResources().getColor(R.color.colorAccentWhite));
            mTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorAccentWhite));
            mIconBack = getResources().getDrawable(R.drawable.ic_back);
            mIconSearch = getResources().getDrawable(R.drawable.ic_search);
            mIconFilter = getResources().getDrawable(R.drawable.ic_filter);
            DrawableCompat.setTint(mIconBack, getResources().getColor(R.color.colorAccentWhite));
            DrawableCompat.setTint(mIconSearch, getResources().getColor(R.color.colorAccentWhite));
            DrawableCompat.setTint(mIconFilter, getResources().getColor(R.color.colorAccentWhite));
        }
    }

    private void initTheme() {
        Resources resources = getResources();
        ThemeManager.Theme theme = ThemeManager.getInstance().getCurrentTheme();
        if (theme == null) {
            mColorPrimary = resources.getColor(R.color.colorPrimaryBlack);
            return;
        }
        switch (theme) {
            case JUST_LIKE:
                mColorPrimary = resources.getColor(R.color.colorPrimary);
                break;
            case WHITE:
                mColorPrimary = resources.getColor(R.color.colorPrimaryBlack);
                break;
            case BLACK:
                mColorPrimary = resources.getColor(R.color.colorPrimaryBlack);
                break;
            case GREY:
                mColorPrimary = resources.getColor(R.color.colorPrimaryGrey);
                break;
            case GREEN:
                mColorPrimary = resources.getColor(R.color.colorPrimaryGreen);
                break;
            case RED:
                mColorPrimary = resources.getColor(R.color.colorPrimaryRed);
                break;
            case PINK:
                mColorPrimary = resources.getColor(R.color.colorPrimaryRed);
                break;
            case BLUE:
                mColorPrimary = resources.getColor(R.color.colorPrimaryBlue);
                break;
            case PURPLE:
                mColorPrimary = resources.getColor(R.color.colorPrimaryPurple);
                break;
            case ORANGE:
                mColorPrimary = resources.getColor(R.color.colorPrimaryOrange);
                break;
        }
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
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
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mPhotoList.size() != 0) {
                    boolean canScrollVertical = mRecyclerView.canScrollVertically(1);
                    if (!canScrollVertical && mFooterProgress.getVisibility() == View.GONE) {
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
                outRect.right = SystemUtils.dp2px(OnlineActivity.this, 2.5F);
            } else if (parent.getChildAdapterPosition(view) % 2 == 1) {
                outRect.left = SystemUtils.dp2px(OnlineActivity.this, 2.5F);
            }
        }
    }
}
