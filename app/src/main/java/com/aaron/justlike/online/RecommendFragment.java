package com.aaron.justlike.online;

import com.aaron.justlike.common.http.unsplash.Order;
import com.aaron.justlike.common.http.unsplash.entity.photo.Photo;

public class RecommendFragment extends OnlineFragment {

    private IOnlinePresenter<Photo> mPresenter;

    public RecommendFragment() {
        // Required empty public constructor
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.detachView();
    }

    @Override
    public void requestPhotos(Order order, boolean isRefresh, boolean isFilter) {
        if (isRefresh || isFilter) mAdapter.clearAnimatedFlag();
        mPresenter.requestPhotos(order, isRefresh, isFilter, mPhotoList);
    }

    @Override
    public void requestLoadMore(Order order) {
        mPresenter.requestLoadMore(order);
    }

    @Override
    public void attachPresenter() {
        if (mPresenter == null) {
            mPresenter = new OnlinePresenter();
        }
        mPresenter.attachView(this);
    }
}
