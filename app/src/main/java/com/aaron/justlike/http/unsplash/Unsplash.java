package com.aaron.justlike.http.unsplash;

import com.aaron.justlike.common.ObserverImpl;
import com.aaron.justlike.http.unsplash.entity.collection.Collection;
import com.aaron.justlike.http.unsplash.entity.photo.Photo;
import com.aaron.justlike.http.unsplash.interfaces.PhotoCallback;
import com.aaron.justlike.http.unsplash.interfaces.PhotoService;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Unsplash 网络请求库
 */
public class Unsplash {

    private static final String BASE_URL = "https://api.unsplash.com/";
    private static final String CLIENT_ID = "18db24a3d59a1b2633897fa63f3f49455c2cbfa8a22e5b8520141cb2660fa816";
    private static final String NEW_CLIENT_ID = "41f1f23556b01d63b1ae823bdf008cc32ce446f77c843e2daa2a80c770015df3";

    private PhotoService mPhotoService;

    private Unsplash() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HeaderInterceptor(NEW_CLIENT_ID))
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mPhotoService = retrofit.create(PhotoService.class);
    }

    public void getPhotos(int page, int perPage, Order order, PhotoCallback<List<Photo>> callback) {
        mPhotoService.getPhotos(page, perPage, order.getOrder())
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

    public void getCuratedPhotos(int page, int perPage, Order order, PhotoCallback<List<Photo>> callback) {
        mPhotoService.getCuratedPhotos(page, perPage, order.getOrder())
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

    public void getRandomPhotos(int count, PhotoCallback<List<Photo>> callback) {
        mPhotoService.getRandomPhotos(count)
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

    public void getPhoto(String id, PhotoCallback<Photo> callback) {
        mPhotoService.getPhoto(id)
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

    public void searchPhotos(String keyWord, int page, int perPage, PhotoCallback<List<Photo>> callback) {
        mPhotoService.searchPhotos(keyWord, page, perPage)
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

    public void searchCollections(String keyWord, int page, int perPage, PhotoCallback<List<Collection>> callback) {
        mPhotoService.searchCollections(keyWord, page, perPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ObserverImpl<List<Collection>>() {
                    @Override
                    public void onNext(List<Collection> list) {
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
