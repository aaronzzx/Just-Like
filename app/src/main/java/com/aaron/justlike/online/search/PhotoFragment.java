package com.aaron.justlike.online.search;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import com.aaron.justlike.R;
import com.aaron.justlike.common.JustLike;
import com.aaron.justlike.common.adapter.PhotoAdapter;
import com.aaron.justlike.common.http.unsplash.entity.photo.Photo;
import com.aaron.justlike.common.manager.UiManager;
import com.aaron.justlike.common.util.SystemUtil;
import com.aaron.justlike.common.widget.MyGridLayoutManager;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;

import java.util.ArrayList;
import java.util.List;

public class PhotoFragment extends Fragment implements ISerachContract.V<Photo>, IFragment {

    private static final String TAG = "PhotoFragment";

    private ISerachContract.P<Photo> mPresenter;
    private Context mContext;

    private View mParentLayout;
    private View mSearchLogo;
    private TextView mSearchLogoHint;
    private ProgressBar mProgressBar;
    private SmartRefreshLayout mRefreshLayout;
    private BallPulseFooter mLoadMoreFooter;
//    private View mRefresh;
//    private View mFooterProgress;
    private RecyclerView mRecyclerView;
    private PhotoAdapter mAdapter;

    private String mKeyWord;
    private List<Photo> mPhotoList = new ArrayList<>();

    public PhotoFragment() {
        Log.i("PhotoFragment", "PhotoFragment: " + this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mParentLayout = inflater.inflate(R.layout.fragment_search, container, false);
        mContext = getActivity();
        initView();
        attachPresenter();
        return mParentLayout;
    }

    @Override
    public void attachPresenter() {
        mPresenter = new PhotoPresenter(this);
    }

    @Override
    public void onShow(List<Photo> list) {
        mRefreshLayout.setEnableLoadMore(true);
        if (mPhotoList.size() > 30) {
            mPhotoList.clear();
            mAdapter.notifyDataSetChanged();
        }
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
    public void onShowMessage(String msg) {
        UiManager.showShort(msg);
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
    public void onShowSearchLogo(String msg) {
        if (msg != null) mSearchLogoHint.setText(msg);
        mSearchLogo.setVisibility(View.VISIBLE);
        mPhotoList.clear();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onHideSearchLogo() {
        mSearchLogo.setVisibility(View.GONE);
    }

    @Override
    public void onShowLoading() {
        mRefreshLayout.autoLoadMore();
    }

    @Override
    public void onHideLoading() {
        mRefreshLayout.finishLoadMore();
    }

    @Override
    public void onNoMoreData() {
        mRefreshLayout.finishLoadMore(0, true, true);
    }

    /**
     * Called by Activity
     */
    @Override
    public void search(String keyWord) {
        mAdapter.clearAnimatedFlag();
        mKeyWord = keyWord;
        mPresenter.requestPhotos(ISerachContract.P.FIRST_REQUEST, keyWord, mPhotoList);
    }

    public void backToTop() {
        int firstItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
        if (firstItem >= 24) {
            mRecyclerView.scrollToPosition(16);
        }
        mRecyclerView.smoothScrollToPosition(0);
    }

    private void initView() {
        mRecyclerView = mParentLayout.findViewById(R.id.recycler_view);
        mSearchLogo = mParentLayout.findViewById(R.id.search_logo);
        mSearchLogoHint = mParentLayout.findViewById(R.id.search_logo_hint);
        mProgressBar = mParentLayout.findViewById(R.id.progress_bar);
        mRefreshLayout = mParentLayout.findViewById(R.id.app_refresh_layout);
        mLoadMoreFooter = mParentLayout.findViewById(R.id.app_refresh_footer);

        initRecyclerView();
        mRefreshLayout.setEnableLoadMore(false);
        mLoadMoreFooter.setAnimatingColor(((SearchActivity) mContext).getColorAccent());
        mRefreshLayout.setEnableRefresh(false);
        mRefreshLayout.setOnLoadMoreListener(refreshLayout -> {
            mPresenter.requestPhotos(ISerachContract.P.LOAD_MORE, mKeyWord, mPhotoList);
        });
    }

    private void initRecyclerView() {
        MyGridLayoutManager layoutManager = new MyGridLayoutManager(mContext, 2);
        int padding = SystemUtil.dp2px(JustLike.getContext(), 5);
        mRecyclerView.setPadding(padding, padding, padding, padding);
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
//        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if (newState == RecyclerView.SCROLL_STATE_IDLE && mPhotoList.size() != 0) {
//                    boolean canScrollVertical = mRecyclerView.canScrollVertically(1);
//                    if (!canScrollVertical && mFooterProgress.getVisibility() == View.GONE) {
//                        mPresenter.requestPhotos(ISerachContract.P.LOAD_MORE, mKeyWord, mPhotoList);
//                    }
//                }
//            }
//        });
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
