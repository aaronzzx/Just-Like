package com.aaron.justlike.fragment.online;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ProgressBar;

import com.aaron.justlike.R;
import com.aaron.justlike.activity.online.OnlineActivity;
import com.aaron.justlike.activity.online.PreviewActivity;
import com.aaron.justlike.activity.online.SearchActivity;
import com.aaron.justlike.adapter.online.OnlineAdapter;
import com.aaron.justlike.entity.PhotoEvent;
import com.aaron.justlike.http.unsplash.Order;
import com.aaron.justlike.http.unsplash.entity.photo.Photo;
import com.aaron.justlike.mvp.view.online.IOnlineView;
import com.aaron.justlike.ui.MyGridLayoutManager;
import com.aaron.justlike.util.SystemUtil;
import com.google.android.material.snackbar.Snackbar;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public abstract class OnlineFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        OnlineAdapter.Callback<Photo>, IOnlineView<Photo>, View.OnClickListener {

    private Context mContext;
    private Order mOrder;

    private View mParentLayout;
    private SwipeRefreshLayout mSwipeRefresh;
    private RecyclerView mRecyclerView;
    private View mErrorView;
    private Button mClickRefresh;
    private ProgressBar mProgressBar;
    private View mFooterProgress;
    private RecyclerView.Adapter mAdapter;

    private int mMenuItemId;
    private int mColorPrimary;
    protected List<Photo> mPhotoList = new ArrayList<>();

    public OnlineFragment() {
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
                requestPhotos(mOrder, false, false);
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mParentLayout = inflater.inflate(R.layout.fragment_online, container, false);
        return mParentLayout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        mOrder = Order.LATEST;
        mColorPrimary = mContext != null ? ((OnlineActivity) mContext).getColorPrimary() : 0;
        initView();
        attachPresenter();
        // 实现 RecommendFragment 的加载
        if (getUserVisibleHint()) {
            requestPhotos(mOrder, false, false);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.activity_online_menu, menu);
        SystemUtil.setIconEnable(menu, true);
        switch (mMenuItemId) {
            case R.id.filter_latest:
                menu.findItem(R.id.filter_latest).setChecked(true);
                break;
            case R.id.filter_oldest:
                menu.findItem(R.id.filter_oldest).setChecked(true);
                break;
            case R.id.filter_popular:
                menu.findItem(R.id.filter_popular).setChecked(true);
                break;
            default:
                menu.findItem(R.id.filter_latest).setChecked(true);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.setChecked(true);
        switch (item.getItemId()) {
            case R.id.action_search:
                startActivity(new Intent(getContext(), SearchActivity.class));
                ((Activity) mContext).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case R.id.filter_latest:
                mMenuItemId = R.id.filter_latest;
                mOrder = Order.LATEST;
                requestPhotos(mOrder, true, true);
                break;
            case R.id.filter_oldest:
                mMenuItemId = R.id.filter_oldest;
                mOrder = Order.OLDEST;
                requestPhotos(mOrder, true, true);
                break;
            case R.id.filter_popular:
                mMenuItemId = R.id.filter_popular;
                mOrder = Order.POPULAR;
                requestPhotos(mOrder, true, true);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 当 EmptyView 出现时，即网络连接不可用或者其他情况导致加载不出图片时，
     * 此按钮用于用户点击重试加载图片
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_click_refresh) {
            requestPhotos(mOrder, true, false);
        }
    }

    @Override
    public void onRefresh() {
        if (mProgressBar.getVisibility() == View.GONE) {
            requestPhotos(mOrder, true, false);
        }
    }

    @Override
    public void onPress(Photo photo) {
        EventBus.getDefault().postSticky(new PhotoEvent(photo));
        startActivity(new Intent(mContext, PreviewActivity.class));
        ((Activity) mContext).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public abstract void requestPhotos(Order order, boolean isRefresh, boolean isFilter);

    public abstract void requestLoadMore(Order order);

    @Override
    public abstract void attachPresenter();

    @Override
    public void onShowPhoto(List<Photo> list) {
        if (mPhotoList.size() > 30) {
            mPhotoList.clear();
            mAdapter.notifyDataSetChanged();
        }
        mErrorView.setVisibility(View.GONE);
        mPhotoList.clear();
        mPhotoList.addAll(list);
        mAdapter.notifyItemRangeChanged(0, mPhotoList.size());
        backToTop();
    }

    @Override
    public void onShowMore(List<Photo> list) {
        mPhotoList.addAll(list);
        mAdapter.notifyItemRangeInserted(mPhotoList.size() - list.size(), list.size());
    }

    @Override
    public void onShowMessage(int requestMode, String args) {
        Snackbar.make(mRecyclerView, "网络开小差了，请检查网络连接", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onShowRefresh() {
        if (!mSwipeRefresh.isRefreshing()) {
            mSwipeRefresh.setRefreshing(true);
        }
    }

    @Override
    public void onHideRefresh() {
        if (!mSwipeRefresh.isEnabled()) {
            mSwipeRefresh.setEnabled(true);
        }
        mSwipeRefresh.postDelayed(() -> mSwipeRefresh.setRefreshing(false), 500);
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
    public void onShowErrorView() {
        mErrorView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onHideErrorView() {
        mErrorView.setVisibility(View.GONE);
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

    /**
     * Called by activity or self.
     */
    public void backToTop() {
        int firstItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
        if (firstItem >= 30) {
            mRecyclerView.scrollToPosition(22);
        }
        mRecyclerView.smoothScrollToPosition(0);
    }

    private void initView() {
        mSwipeRefresh = mParentLayout.findViewById(R.id.swipe_refresh_home_activity_main);
        mRecyclerView = mParentLayout.findViewById(R.id.recycler_view);
        mErrorView = mParentLayout.findViewById(R.id.search_logo);
        mClickRefresh = mParentLayout.findViewById(R.id.btn_click_refresh);
        mProgressBar = mParentLayout.findViewById(R.id.progress_bar);
        mFooterProgress = mParentLayout.findViewById(R.id.footer_progress);

        mClickRefresh.setOnClickListener(this);

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
                        requestLoadMore(mOrder);
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
                outRect.right = SystemUtil.dp2px(mContext, 2.5F);
            } else if (parent.getChildAdapterPosition(view) % 2 == 1) {
                outRect.left = SystemUtil.dp2px(mContext, 2.5F);
            }
        }
    }
}
