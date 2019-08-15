package com.aaron.justlike.online.search;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import com.aaron.justlike.R;
import com.aaron.justlike.common.CommonActivity;
import com.aaron.justlike.common.JustLike;
import com.aaron.justlike.common.adapter.PhotoAdapter;
import com.aaron.justlike.common.http.unsplash.entity.photo.Photo;
import com.aaron.justlike.common.manager.ThemeManager;
import com.aaron.justlike.common.util.SystemUtil;
import com.aaron.justlike.common.widget.MyGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

public class ElementsActivity extends CommonActivity implements IElementContract.V<List<Photo>>,
        View.OnClickListener {

    private IElementContract.P mPresenter;

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private PhotoAdapter mAdapter;
    private View mErrorView;
    private ProgressBar mProgressBar;
    private View mFooterProgress;

    private ActionBar mActionBar;
    private Drawable mIconBack;

    private int mPhotosId;
    private String mCollectionTitle;
    private List<Photo> mPhotoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.getInstance().setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elements);
        mPhotosId = getIntent().getIntExtra("id", 0);
        mCollectionTitle = getIntent().getStringExtra("title");
        initView();
        mPresenter = new ElementPresenter(this);
        mPresenter.requestPhotos(mPhotosId, mPhotoList, false);
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
                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                } else {
                    window.setStatusBarColor(getResources().getColor(R.color.status_bar_background));
                }
                mToolbar.setTitleTextColor(getResources().getColor(R.color.colorGreyText));
                mActionBar.setHomeAsUpIndicator(mIconBack);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        overridePendingTransition(0, R.anim.activity_slide_out);
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, R.anim.activity_slide_out);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_click_refresh) {
            mPresenter.requestPhotos(mPhotosId, mPhotoList, false);
        } else if (v.getId() == R.id.toolbar) {
            backToTop();
        }
    }

    @Override
    public void onShowProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
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
        mFooterProgress.postDelayed(() -> {
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

    @Override
    public void onShowErrorView() {
        mErrorView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onHideErrorView() {
        mErrorView.setVisibility(View.GONE);
    }

    @Override
    public void onShowMessage(String msg) {
        Toast.makeText(JustLike.getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onShowPhotos(List<Photo> list) {
        mPhotoList.addAll(list);
        mAdapter.notifyItemRangeChanged(0, mPhotoList.size());
    }

    @Override
    public void onShowMore(List<Photo> list) {
        mPhotoList.addAll(list);
        mAdapter.notifyItemRangeInserted(mPhotoList.size() - list.size(), list.size());
    }

    public void backToTop() {
        int firstItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
        if (firstItem >= 24) {
            mRecyclerView.scrollToPosition(16);
        }
        mRecyclerView.smoothScrollToPosition(0);
    }

    private void initView() {
        mToolbar = findViewById(R.id.toolbar);
        mRecyclerView = findViewById(R.id.recycler_view);
        mErrorView = findViewById(R.id.error_view);
        Button clickRefresh = findViewById(R.id.btn_click_refresh);
        mProgressBar = findViewById(R.id.progress_bar);
        mFooterProgress = findViewById(R.id.footer_progress);

        clickRefresh.setOnClickListener(this);
        mToolbar.setOnClickListener(this);

        initIconColor();
        initToolbar();
        initRecyclerView();
    }

    private void initIconColor() {
        mIconBack = getResources().getDrawable(R.drawable.ic_back);
        if (ThemeManager.getInstance().getCurrentTheme() == null
                || ThemeManager.getInstance().getCurrentTheme() == ThemeManager.Theme.WHITE) {
            DrawableCompat.setTint(mIconBack, getResources().getColor(R.color.colorAccentWhite));
        } else {
            DrawableCompat.setTint(mIconBack, getResources().getColor(R.color.colorPrimaryWhite));
        }
    }

    private void initToolbar() {
        Window window = getWindow();
        View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        mToolbar.setTitle(mCollectionTitle);
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }
    }

    private void initRecyclerView() {
        MyGridLayoutManager layoutManager = new MyGridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator() {
            @Override
            public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromX, int fromY, int toX, int toY) {
                return true;
            }
        });
        mRecyclerView.addItemDecoration(new XItemDecoration());
        mAdapter = new PhotoAdapterImpl(mPhotoList);
        mRecyclerView.setAdapter(mAdapter);
        // 监听是否滑到底部
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mPhotoList.size() != 0) {
                    boolean canScrollVertical = mRecyclerView.canScrollVertically(1);
                    if (!canScrollVertical && mFooterProgress.getVisibility() == View.GONE) {
                        mPresenter.requestPhotos(mPhotosId, mPhotoList, true);
                    }
                }
            }
        });
    }

    private class XItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) % 2 == 0) {
                outRect.right = SystemUtil.dp2px(JustLike.getContext(), 2.5F);
            } else if (parent.getChildAdapterPosition(view) % 2 == 1) {
                outRect.left = SystemUtil.dp2px(JustLike.getContext(), 2.5F);
            }
        }
    }
}
