package com.aaron.justlike.mvp.presenter.online.search;

import com.aaron.justlike.common.ObserverImpl;
import com.aaron.justlike.http.unsplash.entity.photo.Photo;
import com.aaron.justlike.http.unsplash.entity.photo.SearchPhotoResult;
import com.aaron.justlike.mvp.model.online.search.ISearchModel;
import com.aaron.justlike.mvp.model.online.search.SearchModel;
import com.aaron.justlike.mvp.view.online.search.ISearchView;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class PhotoPresenter implements ISearchPresenter<Photo> {

    private static final String TAG = "PhotoPresenter";

    private ISearchView<Photo> mView;
    private ISearchModel mModel;

    private int mPhotosTotal = 9999;
    private int mPhotosTotalPages = 9999;
    private int mRequestCount = 0;
    private String mKeyWord = "这是一个初始化字符串";

    @Override
    public void attachView(ISearchView<Photo> view) {
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
        if (requestMode == ISearchPresenter.FIRST_REQUEST && mKeyWord.equals(keyWord)) {
            if (oldList.size() != 0) return;
        } else if (mRequestCount >= mPhotosTotalPages && requestMode == ISearchPresenter.LOAD_MORE) {
            return;
        } else if (keyWord.equals("")) {
            return;
        } else if (!mKeyWord.equals(keyWord)) {
            mRequestCount = 0;
        }
        mKeyWord = keyWord;
        mRequestCount++;
        switch (requestMode) {
            case ISearchPresenter.FIRST_REQUEST:
                if (oldList.size() == 0) {
                    mView.onHideSearchLogo();
                    mView.onShowProgress();
                } else {
                    mView.onShowRefresh();
                }
                break;
            case ISearchPresenter.LOAD_MORE:
                mView.onShowLoading();
                break;
        }
        mModel.findPhotos(keyWord, mRequestCount, new ISearchModel.Callback<SearchPhotoResult>() {
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
                                if (requestMode == ISearchPresenter.FIRST_REQUEST) {
                                    mView.onHideProgress();
                                    mView.onHideRefresh();
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
                mView.onHideRefresh();
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
