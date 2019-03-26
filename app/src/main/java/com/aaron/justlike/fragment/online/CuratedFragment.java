package com.aaron.justlike.fragment.online;

import com.aaron.justlike.http.unsplash.Order;
import com.aaron.justlike.http.unsplash.entity.Photo;
import com.aaron.justlike.mvp.presenter.online.IOnlinePresenter;
import com.aaron.justlike.mvp.presenter.online.OnlinePresenter;

public class CuratedFragment extends PhotoFragment {

    private IOnlinePresenter<Photo> mPresenter;

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
        mPresenter.requestCuratedPhotos(order, isRefresh, isFilter);
    }

    @Override
    public void requestLoadMore(Order order) {
        mPresenter.requestLoadMoreCurated(order);
    }

    @Override
    public void attachPresenter() {
        if (mPresenter == null) {
            mPresenter = new OnlinePresenter();
        }
        mPresenter.attachView(this);
    }
}
