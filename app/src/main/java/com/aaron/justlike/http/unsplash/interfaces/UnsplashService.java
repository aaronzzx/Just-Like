package com.aaron.justlike.http.unsplash.interfaces;

import com.aaron.justlike.http.unsplash.entity.collection.SearchCollectionResult;
import com.aaron.justlike.http.unsplash.entity.photo.Photo;
import com.aaron.justlike.http.unsplash.entity.photo.SearchPhotoResult;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UnsplashService {

    @GET("photos/{id}")
    Observable<Photo> getPhoto(@Path("id") String id);

    @GET("photos")
    Observable<List<Photo>> getPhotos(@Query("page") int page, @Query("per_page") int perPage, @Query("order_by") String orderBy);

    @GET("photos/curated")
    Observable<List<Photo>> getCuratedPhotos(@Query("page") int page, @Query("per_page") int perPage, @Query("order_by") String orderBy);

    @GET("photos/random")
    Observable<List<Photo>> getRandomPhotos(@Query("count") int count);

    @GET("search/photos")
    Observable<SearchPhotoResult> searchPhotos(@Query("query") String keyWord, @Query("page") int page, @Query("per_page") int perPage);

    @GET("search/collections")
    Observable<SearchCollectionResult> searchCollections(@Query("query") String keyWord, @Query("page") int page, @Query("per_page") int perPage);

    @GET("collections/{id}/photos")
    Observable<List<Photo>> searchPhotosFromCollection(@Path("id") int id, @Query("page") int page, @Query("per_page") int perPage);
}
