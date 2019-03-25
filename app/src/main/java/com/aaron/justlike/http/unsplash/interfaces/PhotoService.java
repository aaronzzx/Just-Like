package com.aaron.justlike.http.unsplash.interfaces;

import com.aaron.justlike.http.unsplash.entity.Photo;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PhotoService {

    @GET("photos/{id}")
    Observable<Photo> getPhoto(@Path("id") String id);

    @GET("photos")
    Observable<List<Photo>> getPhotos(@Query("page") int page, @Query("per_page") int perPage, @Query("order_by") String orderBy);

    @GET("photos/curated")
    Observable<List<Photo>> getCuratedPhotos(@Query("page") int page, @Query("per_page") int perPage, @Query("order_by") String orderBy);
}
