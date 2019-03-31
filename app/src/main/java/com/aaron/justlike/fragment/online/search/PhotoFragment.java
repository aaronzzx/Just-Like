package com.aaron.justlike.fragment.online.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aaron.justlike.JustLike;
import com.aaron.justlike.R;
import com.aaron.justlike.activity.online.PreviewActivity;
import com.aaron.justlike.adapter.online.OnlineAdapter;
import com.aaron.justlike.entity.PhotoEvent;
import com.aaron.justlike.http.unsplash.entity.photo.Photo;
import com.aaron.justlike.mvp.presenter.online.search.ISearchPresenter;
import com.aaron.justlike.mvp.presenter.online.search.PhotoPresenter;
import com.aaron.justlike.mvp.view.online.search.ISearchView;
import com.aaron.justlike.ui.MyGridLayoutManager;
import com.aaron.justlike.util.SystemUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

public class PhotoFragment extends Fragment implements ISearchView<Photo>,
        OnlineAdapter.Callback<Photo>, IFragment {

    private static final String TAG = "PhotoFragment";

    private ISearchPresenter<Photo> mPresenter;
    private Context mContext;

    private View mParentLayout;
    private View mSearchLogo;
    private TextView mSearchLogoHint;
    private ProgressBar mProgressBar;
    private View mRefresh;
    private View mFooterProgress;
    private RecyclerView mRecyclerView;
    private OnlineAdapter mAdapter;

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
        Log.i(TAG, "onCreateView: -------------------------------- ");
        return mParentLayout;
    }

    @Override
    public void attachPresenter() {
        mPresenter = new PhotoPresenter();
        mPresenter.attachView(this);
    }

    @Override
    public void onShow(List<Photo> list) {
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
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
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
        if (mFooterProgress.getVisibility() == View.GONE) {
            mFooterProgress.setVisibility(View.VISIBLE);
            ScaleAnimation animation = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F);
            animation.setFillAfter(true);
            animation.setDuration(250);
            mFooterProgress.startAnimation(animation);
        }
    }

    @Override
    public void onHideLoading() {
        if (mFooterProgress.getVisibility() == View.VISIBLE) {
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
    }

    @Override
    public void onShowRefresh() {
        if (mRefresh.getVisibility() == View.GONE) {
            mRefresh.setVisibility(View.VISIBLE);
            ScaleAnimation animation = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F);
            animation.setFillAfter(true);
            animation.setDuration(250);
            mRefresh.startAnimation(animation);
        }
    }

    @Override
    public void onHideRefresh() {
        if (mRefresh.getVisibility() == View.VISIBLE) {
            mRefresh.postDelayed(() -> {
                ScaleAnimation animation = new ScaleAnimation(1, 0, 1, 0, Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F);
                animation.setFillAfter(true);
                animation.setDuration(250);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mRefresh.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                mRefresh.startAnimation(animation);
            }, 500);
        }
    }

    @Override
    public void onPress(Photo photo) {
        EventBus.getDefault().postSticky(new PhotoEvent(photo));
        startActivity(new Intent(mContext, PreviewActivity.class));
        ((Activity) mContext).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /**
     * Called by ElementActivity
     */
    public void setElementList() {

    }

    /**
     * Called by Activity
     */
    @Override
    public void search(String keyWord) {
        mAdapter.clearAnimatedFlag();
        mKeyWord = keyWord;
        mPresenter.requestPhotos(ISearchPresenter.FIRST_REQUEST, keyWord, mPhotoList);
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
        mRefresh = mParentLayout.findViewById(R.id.refresh);
        mFooterProgress = mParentLayout.findViewById(R.id.footer_progress);

        initRecyclerView();
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
                        mPresenter.requestPhotos(ISearchPresenter.LOAD_MORE, mKeyWord, mPhotoList);
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
