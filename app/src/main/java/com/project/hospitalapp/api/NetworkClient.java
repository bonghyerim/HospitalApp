package com.project.hospitalapp.api;

import android.content.Context;

import com.project.hospitalapp.config.Config;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkClient {

    // (10)
    // 네트워크 통신한, 로그를 확인할때 필요한 코드. (데이터가 제대로 왔는지 안왔는지...)
    public static Retrofit retrofit;

    public static Retrofit getRetrofitClient(Context context, String host){
        if(retrofit == null){
            HttpLoggingInterceptor loggingInterceptor =
                    new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);


    // (11)
    // 네트워크 연결시키는 코드

            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .connectTimeout(1, TimeUnit.MINUTES)
                    .readTimeout(1,TimeUnit.MINUTES)
                    .writeTimeout(1,TimeUnit.MINUTES)
                    .addInterceptor(loggingInterceptor)
                    .build();

            // 네트워크로 데이터를 보내고 받는
            // 레트로핏 라이브러리 관련 코드
    // (12)

            retrofit = new Retrofit.Builder()
                    .baseUrl(host)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getRetrofitClient(Context context){
        if(retrofit == null){
            HttpLoggingInterceptor loggingInterceptor =
                    new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);


            // (11)
            // 네트워크 연결시키는 코드

            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .connectTimeout(1, TimeUnit.MINUTES)
                    .readTimeout(1,TimeUnit.MINUTES)
                    .writeTimeout(1,TimeUnit.MINUTES)
                    .addInterceptor(loggingInterceptor)
                    .build();

            // 네트워크로 데이터를 보내고 받는
            // 레트로핏 라이브러리 관련 코드
            // (12)

            retrofit = new Retrofit.Builder()
                    .baseUrl(Config.HOST)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }


}
