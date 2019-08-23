package com.aaron.justlike.online.search;

import androidx.lifecycle.LifecycleOwner;

import com.aaron.justlike.common.http.unsplash.entity.photo.Photo;
import com.aaron.justlike.common.http.unsplash.entity.photo.SearchPhotoResult;
import com.aaron.justlike.common.impl.ObserverImpl;
import com.blankj.utilcode.util.StringUtils;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class PhotoPresenter implements ISerachContract.P<Photo> {

    private ISerachContract.V<Photo> mView;
    private ISerachContract.M mModel;

    private int mPhotosTotal = 9999;
    private int mPhotosTotalPages = 9999;
    private int mRequestCount = 0;
    private String mKeyWord = "这是一个初始化字符串";

    PhotoPresenter(ISerachContract.V<Photo> view) {
        mView = view;
        mModel = new SearchModel();
    }

    @Override
    public void detachView() {
        mView = null;
        mModel = null;
    }

    @Override
    public void requestPhotos(int requestMode, String keyWord, List<Photo> oldList) {
        if (requestMode == ISerachContract.P.FIRST_REQUEST && mKeyWord.equals(keyWord)) {
            if (oldList.size() != 0) return;
        } else if (mRequestCount >= mPhotosTotalPages && requestMode == ISerachContract.P.LOAD_MORE) {
            mView.onNoMoreData();
            return;
        } else if (StringUtils.isEmpty(keyWord)) {
            return;
        } else if (!mKeyWord.equals(keyWord)) {
            mRequestCount = 0;
        }
        mKeyWord = keyWord;
        mRequestCount++;
        switch (requestMode) {
            case ISerachContract.P.FIRST_REQUEST:
                if (oldList.size() == 0) {
                    mView.onHideSearchLogo();
                    mView.onShowProgress();
                }
                break;
            case ISerachContract.P.LOAD_MORE:
                mView.onShowLoading();
                break;
        }
        mModel.findPhotos((LifecycleOwner) mView, keyWord, mRequestCount, new ISerachContract.M.Callback<SearchPhotoResult>() {
            @Override
            public void onSuccess(SearchPhotoResult result) {
                Observable.create((ObservableOnSubscribe<List<Photo>>) emitter -> {
                    mPhotosTotal = result.getTotal();
                    mPhotosTotalPages = result.getTotalPages();
                    emitter.onNext(result.getResults());
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new ObserverImpl<List<Photo>>() {
                            @Override
                            public void onNext(List<Photo> list) {
                                if (requestMode == ISerachContract.P.FIRST_REQUEST) {
                                    mView.onHideProgress();
                                    if (list.size() == 0) {
                                        mView.onShowSearchLogo("无法获取资源");
                                        return;
                                    }
                                    mView.onShow(list);
                                } else {
                                    mView.onHideLoading();
                                    mView.onShowMore(list);
                                }
                            }
                        });
            }

            @Override
            public void onFailure() {
                mView.onHideProgress();
                mView.onHideLoading();
                if (oldList.isEmpty()) {
                    mView.onShowSearchLogo("搜索失败");
                }
                mView.onShowMessage("网络开小差了");
            }
        });
    }

    @Override
    public void requestCollections(int requestMode, String keyWord, List<Photo> oldList) {

    }
}
