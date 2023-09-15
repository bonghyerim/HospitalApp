package com.project.hospitalapp.api;

import com.project.hospitalapp.model.Alarm;
import com.project.hospitalapp.model.FoodAllLIst;
import com.project.hospitalapp.model.FoodList;
import com.project.hospitalapp.model.ResultRes;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface FoodApi {


    @POST("/food/user")
    Call<ResultRes> addFood(@Header("Authorization") String token, @Body Alarm dateAlarm);


    @GET("/food/user")
    Call<FoodList> getFood(@Header("Authorization") String token);



    @GET("/food/user/all")
    Call<FoodAllLIst> getFoodAll(@Header("Authorization") String token);




}
