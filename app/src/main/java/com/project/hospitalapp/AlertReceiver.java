package com.project.hospitalapp;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class AlertReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {


        Log.d("AlertReceiver", "onReceive: Alarm is triggered!"); // 로그 메시지 추가
        Log.d("AlertReceiver", "onReceive: Alarm is triggered for content: " + intent.getStringExtra("content"));

        Bundle extras = intent.getExtras();
        if (extras != null) {
            String content = extras.getString("content"); // Alarm 객체의 내용을 가져옴
            String alarmType = extras.getString("alarmType");// 알람 종류 또는 구분자를 가져옴
            // 알람을 클릭했을 때 실행될 액티비티 설정
            Intent activityIntent;
            if ("morning".equals(alarmType)) {
                activityIntent = new Intent(context, MainActivity.class);
            } else if ("lunch".equals(alarmType)) {
                activityIntent = new Intent(context, MainActivity.class);
            } else if ("dinner".equals(alarmType)) {
                activityIntent = new Intent(context, MainActivity.class);
            } else {
                activityIntent = new Intent(context, AlarmActivity.class);
            }
            activityIntent.putExtra("content", content); // 알람 내용을 인텐트에 추가

            // PendingIntent를 사용하여 액티비티를 실행
            PendingIntent pendingActivityIntent = PendingIntent.getActivity(context, 0, activityIntent, PendingIntent.FLAG_IMMUTABLE);

            // 알람에 대한 알림 설정
            NotificationHelper notificationHelper = new NotificationHelper(context);
            notificationHelper.showNotification(content, generateUniqueNotificationId(), pendingActivityIntent);
        }


    }
    private int generateUniqueNotificationId() {
        // 현재 시간을 밀리초로 변환하여 고유한 ID로 사용
        return (int) System.currentTimeMillis();
    }
}
