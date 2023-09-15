package com.project.hospitalapp.api;


import com.project.hospitalapp.model.User;
import com.project.hospitalapp.model.UserRes;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserApi {

    //회원가입 API 함수 작성
    @POST("/user/register")
    Call<UserRes> register(@Body User user);

    //로그인 API

    @POST("/user/login")
    Call<UserRes> login(@Body User user);


}
