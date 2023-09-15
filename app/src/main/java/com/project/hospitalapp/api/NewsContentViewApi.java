package com.project.hospitalapp.api;

import com.project.hospitalapp.model.NewsContentResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsContentViewApi {

    @GET("view/news")
    Call<NewsContentResponse> getNewsContent(@Query("url") String url);
}
