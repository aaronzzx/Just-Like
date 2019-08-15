package com.aaron.justlike.online.home;

import com.aaron.justlike.common.http.unsplash.Order;
import com.aaron.justlike.common.http.unsplash.entity.photo.Photo;

public class CuratedFragment extends OnlineFragment {

    private IOnlineContract.P<Photo> mPresenter;

    public CuratedFragment() {
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
        mPresenter.requestCuratedPhotos(order, isRefresh, isFilter, mPhotoList);
    }

    @Override
    public void requestLoadMore(Order order) {
        mPresenter.requestLoadMoreCurated(order);
    }

    @Override
    public void attachPresenter() {
        if (mPresenter == null) {
            mPresenter = new OnlinePresenter(this);
        }
    }
}
