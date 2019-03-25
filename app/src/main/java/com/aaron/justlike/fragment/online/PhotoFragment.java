package com.aaron.justlike.fragment.online;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ProgressBar;

import com.aaron.justlike.R;
import com.aaron.justlike.activity.online.OnlineActivity;
import com.aaron.justlike.activity.online.PreviewActivity;
import com.aaron.justlike.adapter.online.OnlineAdapter;
import com.aaron.justlike.entity.PhotoEvent;
import com.aaron.justlike.http.unsplash.entity.Photo;
import com.aaron.justlike.mvp.presenter.online.OnlinePresenter;
import com.aaron.justlike.mvp.view.online.IOnlineView;
import com.aaron.justlike.ui.MyGridLayoutManager;
import com.aaron.justlike.util.SystemUtils;
import com.google.android.material.snackbar.Snackbar;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public abstract class PhotoFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        OnlineAdapter.Callback<Photo>, IOnlineView<Photo> {

    private Context mContext;

    private SwipeRefreshLayout mSwipeRefresh;
    private RecyclerView mRecyclerView;
    private View mErrorView;
    private ProgressBar mProgressBar;
    private View mFooterProgress;
    private RecyclerView.Adapter mAdapter;

    private int mColorPrimary;
    private List<Photo> mPhotoList = new ArrayList<>();

    public PhotoFragment() {
        // Required empty public constructor
    }

    /**
     * 实现 CuratedFragment 懒加载
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isVisible()) {
            if (mPhotoList.size() == 0) {
                requestPhotos(false);
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View parentLayout = inflater.inflate(R.layout.fragment_photo, container, false);
        mContext = getActivity();
        initView(parentLayout);
        attachPresenter();
        // 实现 RecommendFragment 的加载
        if (getUserVisibleHint()) {
            requestPhotos(false);
        }
        return parentLayout;
    }

    @Override
    public void onRefresh() {
        if (mProgressBar.getVisibility() == View.GONE) {
            requestPhotos(true);
        }
    }

    @Override
    public void onPress(Photo photo) {
        EventBus.getDefault().postSticky(new PhotoEvent(photo));
        startActivity(new Intent(mContext, PreviewActivity.class));
        ((Activity) mContext).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /**
     * 子类实现
     */
    public abstract void requestPhotos(boolean isRefresh);

    /**
     * 子类实现
     */
    public abstract void requestLoadMore();

    @Override
    public abstract void attachPresenter();

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
        Snackbar snackbar = Snackbar.make(mRecyclerView, args, Snackbar.LENGTH_SHORT);
        snackbar.setActionTextColor(getResources().getColor(mColorPrimary));
        snackbar.setAction("刷新", v -> {
            switch (mode) {
                case OnlinePresenter.REQUEST_IMAGE:
                    requestPhotos(true);
                    break;
                case OnlinePresenter.LOAD_MORE:
                    requestLoadMore();
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

    /**
     * Called by activity
     */
    public void backToTop() {
        int firstItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
        if (firstItem >= 30) {
            mRecyclerView.scrollToPosition(22);
        }
        mRecyclerView.smoothScrollToPosition(0);
    }

    private void initView(View parentLayout) {
        mSwipeRefresh = parentLayout.findViewById(R.id.swipe_refresh_home_activity_main);
        mRecyclerView = parentLayout.findViewById(R.id.recycler_view);
        mErrorView = parentLayout.findViewById(R.id.error_view);
        mProgressBar = parentLayout.findViewById(R.id.progress_bar);
        mFooterProgress = parentLayout.findViewById(R.id.footer_progress);

        mColorPrimary = ((OnlineActivity) mContext).getColorPrimary();
        initRecyclerView();
        initSwipeRefresh();
    }

    private void initRecyclerView() {
        MyGridLayoutManager layoutManager = new MyGridLayoutManager(mContext, 2);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new XItemDecoration());
        mAdapter = new OnlineAdapter(mPhotoList, this);
        mRecyclerView.setAdapter(mAdapter);
        // 监听是否滑到底部
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mPhotoList.size() != 0) {
                    boolean canScrollVertical = mRecyclerView.canScrollVertically(1);
                    if (!canScrollVertical && mFooterProgress.getVisibility() == View.GONE) {
                        requestLoadMore();
                    }
                }
            }
        });
    }

    private void initSwipeRefresh() {
        mSwipeRefresh.setOnRefreshListener(this);
        mSwipeRefresh.setColorSchemeColors(mColorPrimary);
        mSwipeRefresh.setEnabled(false);
    }

    private class XItemDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) % 2 == 0) {
                outRect.right = SystemUtils.dp2px(mContext, 2.5F);
            } else if (parent.getChildAdapterPosition(view) % 2 == 1) {
                outRect.left = SystemUtils.dp2px(mContext, 2.5F);
            }
        }
    }
}
