package com.aaron.justlike.online.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import com.aaron.base.impl.OnClickListenerImpl;
import com.aaron.justlike.R;
import com.aaron.justlike.common.adapter.PhotoAdapter;
import com.aaron.justlike.common.http.unsplash.Order;
import com.aaron.justlike.common.http.unsplash.entity.photo.Photo;
import com.aaron.justlike.common.manager.UiManager;
import com.aaron.justlike.common.util.PopupWindowUtils;
import com.aaron.justlike.common.util.SystemUtil;
import com.aaron.justlike.common.widget.MyGridLayoutManager;
import com.aaron.justlike.online.search.SearchActivity;
import com.aaron.ui.widget.TopBar;
import com.google.android.material.snackbar.Snackbar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.header.BezierRadarHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

public abstract class OnlineFragment extends Fragment implements IOnlineContract.V<Photo> {

    private Context mContext;
    private Order mOrder;

    private View mParentLayout;
//    private SwipeRefreshLayout mSwipeRefresh;
    private SmartRefreshLayout mRefreshLayout;
    private BezierRadarHeader mRefreshHeader;
    private BallPulseFooter mLoadMoreFooter;
    private RecyclerView mRecyclerView;
    private View mErrorView;
    private Button mClickRefresh;
    private ProgressBar mProgressBar;
//    private View mFooterProgress;
    protected PhotoAdapter mAdapter;

    private int mMenuItemId;
    private int mColorAccent;
    protected List<Photo> mPhotoList = new ArrayList<>();

    private PopupWindow mPwMenu;
    private TextView mTvLatest;
    private TextView mTvOldest;
    private TextView mTvPopular;

    public OnlineFragment() {
        // Required empty public constructor
    }

//    /**
//     * 实现 CuratedFragment 懒加载
//     */
//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if (isVisibleToUser && isVisible()) {
//            if (mPhotoList.size() == 0) {
//                requestPhotos(mOrder, false, false);
//            }
//        }
//    }

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
        mColorAccent = mContext != null ? ((OnlineActivity) mContext).getColorAccent() : 0;
        initView();
        attachPresenter();
//        // 实现 RecommendFragment 的加载
//        if (getUserVisibleHint()) {
//        }
        requestPhotos(mOrder, false, false);
    }

    @Override
    public void onStop() {
        super.onStop();
        mRefreshLayout.finishRefresh(false);
        mRefreshLayout.finishLoadMore(false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.activity_online_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                startActivity(new Intent(getContext(), SearchActivity.class));
                break;
            case R.id.action_filter:
                TopBar topBar = ((OnlineActivity) mContext).getTopBar();
                UiManager.showPopupWindow(mPwMenu, topBar);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public abstract void requestPhotos(Order order, boolean isRefresh, boolean isFilter);

    public abstract void requestLoadMore(Order order);

    @Override
    public abstract void attachPresenter();

    @Override
    public void onShowPhoto(List<Photo> list, boolean isDifference) {
        mRefreshLayout.setEnableLoadMore(true);
        if (isDifference) {
            mPhotoList.clear();
            mPhotoList.addAll(list);
            mAdapter.notifyDataSetChanged();
            mRecyclerView.scrollToPosition(0);
        } else {
            if (mPhotoList.size() > 30) {
                mPhotoList.clear();
                mAdapter.notifyDataSetChanged();
            }
            mErrorView.setVisibility(View.GONE);
            mPhotoList.clear();
            mPhotoList.addAll(list);
            mAdapter.notifyItemRangeChanged(0, mPhotoList.size());
        }
    }

    @Override
    public void onShowMore(List<Photo> list) {
        mPhotoList.addAll(list);
        mAdapter.notifyItemRangeInserted(mPhotoList.size() - list.size(), list.size());
    }

    @Override
    public void onShowMessage(int requestMode, String args) {
        if (getUserVisibleHint()) {
            Snackbar.make(mRecyclerView, "网络开小差了，请检查网络连接", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onShowRefresh() {
        mRefreshLayout.autoRefresh();
    }

    @Override
    public void onShowRefreshOnlyAnim() {
        mRefreshLayout.autoRefreshAnimationOnly();
    }

    @Override
    public void onHideRefresh() {
        mRefreshLayout.finishRefresh(500);
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
        mRefreshLayout.autoLoadMore();
    }

    @Override
    public void onHideLoading() {
        mRefreshLayout.finishLoadMore();
    }

    /**
     * Called by activity or self.
     */
    void backToTop() {
        int firstItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
        if (firstItem >= 24) {
            mRecyclerView.scrollToPosition(16);
        }
        mRecyclerView.smoothScrollToPosition(0);
    }

    private void initView() {
        mRefreshLayout = mParentLayout.findViewById(R.id.swipe_refresh_home_activity_main);
        mRefreshHeader = mParentLayout.findViewById(R.id.app_refresh_header);
        mLoadMoreFooter = mParentLayout.findViewById(R.id.app_refresh_footer);
        mRecyclerView = mParentLayout.findViewById(R.id.recycler_view);
        mErrorView = mParentLayout.findViewById(R.id.search_logo);
        mClickRefresh = mParentLayout.findViewById(R.id.btn_click_refresh);
        mProgressBar = mParentLayout.findViewById(R.id.progress_bar);

        mClickRefresh.setOnClickListener(new OnClickListenerImpl() {
            @Override
            public void onViewClick(View v, long interval) {
                if (v.getId() == R.id.btn_click_refresh) {
                    requestPhotos(mOrder, true, false);
                }
            }
        });
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (mRefreshLayout.getState() != RefreshState.Refreshing) {
                    requestLoadMore(mOrder);
                }
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (mProgressBar.getVisibility() == View.GONE) {
                    requestPhotos(mOrder, true, false);
                }
            }
        });

        initPwMenu();
        initRecyclerView();
        initSwipeRefresh();
    }

    private void initPwMenu() {
        View content = LayoutInflater.from(mContext).inflate(R.layout.app_pw_online_menu, null);
        mTvLatest = content.findViewById(R.id.app_tv_latest);
        mTvLatest.setSelected(true);
        mTvOldest = content.findViewById(R.id.app_tv_oldest);
        mTvPopular = content.findViewById(R.id.app_tv_popular);
        mPwMenu = PopupWindowUtils.create(content);
        mTvLatest.setOnClickListener(v -> {
            mTvOldest.setSelected(false);
            mTvPopular.setSelected(false);
            mTvLatest.setSelected(true);
            mOrder = Order.LATEST;
            requestPhotos(mOrder, false, true);
            mPwMenu.dismiss();
        });
        mTvOldest.setOnClickListener(v -> {
            mTvLatest.setSelected(false);
            mTvPopular.setSelected(false);
            mTvOldest.setSelected(true);
            mOrder = Order.OLDEST;
            requestPhotos(mOrder, false, true);
            mPwMenu.dismiss();
        });
        mTvPopular.setOnClickListener(v -> {
            mTvLatest.setSelected(false);
            mTvOldest.setSelected(false);
            mTvPopular.setSelected(true);
            mOrder = Order.POPULAR;
            requestPhotos(mOrder, false, true);
            mPwMenu.dismiss();
        });
        mPwMenu.setAnimationStyle(R.style.AppPopupWindow);
        mPwMenu.setFocusable(true);
        mPwMenu.setOutsideTouchable(true);
        mPwMenu.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPwMenu.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void initRecyclerView() {
//        ((DefaultItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        MyGridLayoutManager layoutManager = new MyGridLayoutManager(mContext, 2);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator() {
            @Override
            public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromX, int fromY, int toX, int toY) {
                return true;
            }
        });
        mRecyclerView.addItemDecoration(new XItemDecoration());
        mAdapter = new OnlineAdapter(mPhotoList);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initSwipeRefresh() {
        mRefreshHeader.setPrimaryColor(Color.WHITE);
        mRefreshHeader.setAccentColor(mColorAccent);
        mLoadMoreFooter.setAnimatingColor(mColorAccent);
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
