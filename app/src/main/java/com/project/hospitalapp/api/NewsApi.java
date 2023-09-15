package com.project.hospitalapp.api;

import com.project.hospitalapp.model.NewsItem;
import com.project.hospitalapp.model.NewsResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface NewsApi {
    @GET("get/news") // 여기에 실제 엔드포인트 URL을 넣어야 합니다.
    Call<NewsResponse> getNews(); // 반환 타입을 Call<NewsResponse>로 변경
}