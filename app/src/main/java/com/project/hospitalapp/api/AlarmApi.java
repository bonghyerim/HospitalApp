package com.project.hospitalapp.api;

import com.project.hospitalapp.model.Alarm;
import com.project.hospitalapp.model.AlarmList;
import com.project.hospitalapp.model.ResultRes;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AlarmApi {

    // 리스트 보이게하기
    @GET("/alarm")
    Call<AlarmList> getAlarmList(@Header("Authorization") String token);

    // 알람생성
    @POST("/alarm")
    Call<ResultRes> addAlarm(@Header("Authorization") String token, @Body Alarm dateAlarm);

    // 알람 수정하는 API
    @PUT("/alarm/{alarmId}")
    Call<ResultRes> updateAlarm(@Path("alarmId") int alarmId , @Header("Authorization") String token, @Body Alarm alarmUd);

    // 알람 삭제하는 API
    @DELETE("/alarm/{alarmId}")
    Call<ResultRes> deleteAlarm(@Path("alarmId") int alarmId , @Header("Authorization") String token);




}
