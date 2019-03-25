package com.aaron.justlike.fragment;

import com.aaron.justlike.http.unsplash.entity.Photo;
import com.aaron.justlike.mvp.presenter.online.IOnlinePresenter;
import com.aaron.justlike.mvp.presenter.online.OnlinePresenter;

public class RecommendFragment extends PhotoFragment {

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
    public void requestPhotos(boolean isRefresh) {
        mPresenter.requestPhotos(isRefresh);
    }

    @Override
    public void requestLoadMore() {
        mPresenter.requestLoadMore();
    }

    @Override
    public void attachPresenter() {
        if (mPresenter == null) {
            mPresenter = new OnlinePresenter();
        }
        mPresenter.attachView(this);
    }
}
