package com.project.hospitalapp.api;

import com.project.hospitalapp.model.FoodList;
import com.project.hospitalapp.model.MedicineList;
import com.project.hospitalapp.model.ResultRes;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MedicineApi {
    // 음식, 약 확인
    @GET("/medicine")
    Call<MedicineList> getMedicine(@Header("Authorization") String token);

    // 음식, 약 삭제
    @DELETE("/medicine/{medicineId}")
    Call<ResultRes> deleteMedicine(@Path("medicineId") int id , @Header("Authorization") String token);

    // 음식, 약 수정
    @POST("/medicine/{medicineId}")
    Call<ResultRes> updateMedicine(@Path("medicineId") int id , @Header("Authorization") String token);




}
