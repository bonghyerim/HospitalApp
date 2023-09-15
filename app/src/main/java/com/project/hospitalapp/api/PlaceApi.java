package com.project.hospitalapp.api;




import com.project.hospitalapp.model.PlaceDetailResult;
import com.project.hospitalapp.model.PlaceList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlaceApi {

    @GET("/maps/api/place/nearbysearch/json")
    Call<PlaceList> getPlaceList(@Query("language") String language,
                                 @Query("location") String location,
                                 @Query("radius") int radius,
                                 @Query("key") String key,
                                 @Query("keyword") String keyword);

    @GET("/maps/api/place/nearbysearch/json")
    Call<PlaceList> getPlaceListPage(@Query("language") String language,
                                     @Query("location") String location,
                                     @Query("radius") int radius,
                                     @Query("key") String key,
                                     @Query("keyword") String keyword,
                                     @Query("pagetoken") String pageToken);

    @GET("/maps/api/place/details/json")
    Call<PlaceDetailResult> getDetailList(@Query("language") String language,
                                          @Query("place_id") String placeId,
                                          @Query("key") String key);


}
