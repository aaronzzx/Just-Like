package com.aaron.justlike.fragment.online;

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
    public void requestPhotos(boolean isRefresh) {
        mPresenter.requestCuratedPhotos(isRefresh);
    }

    @Override
    public void requestLoadMore() {
        mPresenter.requestLoadMoreCurated();
    }

    @Override
    public void attachPresenter() {
        if (mPresenter == null) {
            mPresenter = new OnlinePresenter();
        }
        mPresenter.attachView(this);
    }
}
