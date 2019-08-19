package com.aaron.justlike.common.http.unsplash;

import com.aaron.base.http.HttpUtil;
import com.aaron.justlike.common.http.unsplash.entity.collection.SearchCollectionResult;
import com.aaron.justlike.common.http.unsplash.entity.photo.Photo;
import com.aaron.justlike.common.http.unsplash.entity.photo.SearchPhotoResult;
import com.aaron.justlike.common.http.unsplash.interfaces.UnsplashCallback;
import com.aaron.justlike.common.http.unsplash.interfaces.UnsplashService;
import com.aaron.justlike.common.impl.ObserverImpl;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;

/**
 * Unsplash 网络请求库
 */
public class Unsplash {

    private static final String BASE_URL = "https://api.unsplash.com/";
    private static final String CLIENT_ID = "18db24a3d59a1b2633897fa63f3f49455c2cbfa8a22e5b8520141cb2660fa816";
    private static final String NEW_CLIENT_ID = "41f1f23556b01d63b1ae823bdf008cc32ce446f77c843e2daa2a80c770015df3";

    private UnsplashService mUnsplashService;

    private Unsplash() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HeaderInterceptor(NEW_CLIENT_ID))
                .build();
        mUnsplashService = HttpUtil.createService(BASE_URL, client, UnsplashService.class);
    }

    public void getPhotos(int page, int perPage, Order order, UnsplashCallback<List<Photo>> callback) {
        mUnsplashService.getPhotos(page, perPage, order.getOrder())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ObserverImpl<List<Photo>>() {
                    @Override
                    public void onNext(List<Photo> photos) {
                        callback.onSuccess(photos);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onFailure(e.getMessage());
                    }
                });
    }

    public void getCuratedPhotos(int page, int perPage, Order order, UnsplashCallback<List<Photo>> callback) {
        mUnsplashService.getCuratedPhotos(page, perPage, order.getOrder())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ObserverImpl<List<Photo>>() {
                    @Override
                    public void onNext(List<Photo> photos) {
                        callback.onSuccess(photos);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onFailure(e.getMessage());
                    }
                });
    }

    public void getRandomPhotos(int count, UnsplashCallback<List<Photo>> callback) {
        mUnsplashService.getRandomPhotos(count)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ObserverImpl<List<Photo>>() {
                    @Override
                    public void onNext(List<Photo> photos) {
                        callback.onSuccess(photos);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onFailure(e.getMessage());
                    }
                });
    }

    public void getPhoto(String id, UnsplashCallback<Photo> callback) {
        mUnsplashService.getPhoto(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ObserverImpl<Photo>() {
                    @Override
                    public void onNext(Photo photo) {
                        callback.onSuccess(photo);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onFailure(e.getMessage());
                    }
                });
    }

    public void searchPhotos(String keyWord, int page, int perPage, UnsplashCallback<SearchPhotoResult> callback) {
        mUnsplashService.searchPhotos(keyWord, page, perPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
//                .map(searchPhotoResult -> searchPhotoResult.getResults())
                .subscribe(new ObserverImpl<SearchPhotoResult>() {
                    @Override
                    public void onNext(SearchPhotoResult result) {
                        callback.onSuccess(result);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onFailure(e.getMessage());
                    }
                });
    }

    public void searchCollections(String keyWord, int page, int perPage, UnsplashCallback<SearchCollectionResult> callback) {
        mUnsplashService.searchCollections(keyWord, page, perPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
//                .map(searchCollectionResult -> searchCollectionResult.getResults())
                .subscribe(new ObserverImpl<SearchCollectionResult>() {
                    @Override
                    public void onNext(SearchCollectionResult result) {
                        callback.onSuccess(result);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onFailure(e.getMessage());
                    }
                });
    }

    public void searchPhotosFromCollection(int id, int page, int perPage, UnsplashCallback<List<Photo>> callback) {
        mUnsplashService.searchPhotosFromCollection(id, page, perPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ObserverImpl<List<Photo>>() {
                    @Override
                    public void onNext(List<Photo> list) {
                        callback.onSuccess(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onFailure(e.getMessage());
                    }
                });
    }

    public static Unsplash getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final Unsplash INSTANCE = new Unsplash();
    }
}
