package com.project.hospitalapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class NotificationHelper extends ContextWrapper {

    public  static  final  String channelID = "channelID";
    public  static  final  String channelNm = "channelNm";
    private NotificationManager notiManager;


    public NotificationHelper(Context base) {
        super(base);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createChannels(){
        NotificationChannel channel1 = new NotificationChannel(channelID, channelNm, NotificationManager.IMPORTANCE_DEFAULT);
        channel1.enableLights(true);
        channel1.enableVibration(true);
        channel1.setLightColor(com.google.android.material.R.color.design_default_color_on_primary);
        channel1.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(channel1);

    }
    public NotificationManager getManager(){
        if(notiManager == null){
            notiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notiManager;

    }
    public NotificationCompat.Builder getChannelNotification(){
        Log.d("NotificationHelper", "Creating notification...");
        return new NotificationCompat.Builder(getApplicationContext(), channelID)
                .setContentTitle("Mydocter")
                .setContentText("알람매니저 실행중")
                .setSmallIcon(R.drawable.my_doctor);


    }

    public void showNotification(String content, int notificationId, PendingIntent pendingIntent) {
        NotificationCompat.Builder nb = getChannelNotification();
        nb.setContentText(content);
        nb.setContentIntent(pendingIntent); // PendingIntent 설정
        getManager().notify(notificationId, nb.build());
    }
}
