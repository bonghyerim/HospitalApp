package com.project.hospitalapp;

import static com.project.hospitalapp.LoginActivity.checkLogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.project.hospitalapp.adapter.AlarmAdapter;
import com.project.hospitalapp.api.AlarmApi;
import com.project.hospitalapp.api.NetworkClient;
import com.project.hospitalapp.config.Config;
import com.project.hospitalapp.model.Alarm;
import com.project.hospitalapp.model.AlarmList;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AlarmActivity extends AppCompatActivity {

    CardView cardViewAlarm;
    RecyclerView recyclerView;

    AlarmAdapter adapter;

    ArrayList<Alarm> alarmArrayList = new ArrayList<Alarm>();

    String accessToken;

    BottomNavigationView bottomNavigationView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);


        cardViewAlarm = findViewById(R.id.cardViewAlarm);
        recyclerView = findViewById(R.id.recyclerView);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // 커스텀 뒤로가기 버튼 사용하기.
        Drawable customBackButton = getResources().getDrawable(R.drawable.baseline_arrow_back_24);
        getSupportActionBar().setHomeAsUpIndicator(customBackButton);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("알람"); // 원하는 타이틀로 변경
        }


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(AlarmActivity.this));

//        // 어댑터를 생성하고 알람 목록 데이터를 전달
//        adapter = new AlarmAdapter(this, alarmArrayList);
//        recyclerView.setAdapter(adapter); // RecyclerView에 어댑터 설정
//
//        if (getIntent().hasExtra("newAlarm")) {
//            Alarm newAlarm = (Alarm) getIntent().getSerializableExtra("newAlarm");
//            if (newAlarm != null) {
//                alarmArrayList.add(newAlarm);
//                adapter.notifyDataSetChanged();
//            }
//        }


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                checkLogin(AlarmActivity.this, itemId);

                return false;
            }
        });


        cardViewAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkNotificationPermission()) {
                    showNotificationPermissionDialog(); // 권한이 없는 경우 다이얼로그 표시
                } else {
                    Intent intent = new Intent(AlarmActivity.this, AlarmSettingActivity.class);
                    startActivity(intent);
                }
            }
        });




    }

    private boolean checkNotificationPermission() {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        return notificationManager.areNotificationsEnabled();
    }

    private void showNotificationPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("알림 권한 설정");
        builder.setMessage("알람을 받으려면 알림 권한을 설정해야 합니다.\n설정 화면으로 이동하시겠습니까?");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 알림 권한 설정 화면으로 이동
                Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                startActivity(intent);
            }
        });
        builder.setNegativeButton("취소", null);
        builder.show();
    }



    private void getNetworkData() {

        alarmArrayList.clear();

//        // 프로그레스바를 보이게 한다.
//        progressBar2.setVisibility(View.VISIBLE);

        SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String accessToken = sp.getString(Config.ACCESS_TOKEN, "");

        Retrofit retrofit = NetworkClient.getRetrofitClient(AlarmActivity.this);
        AlarmApi api = retrofit.create(AlarmApi.class);
        Call<AlarmList> call = api.getAlarmList("Bearer " + accessToken);

        Log.d("RetrofitRequest", "Request URL: " + call.request().url());
        showProgress();
        call.enqueue(new Callback<AlarmList>() {
            @Override
            public void onResponse(Call<AlarmList> call, Response<AlarmList> response) {

                dismissProgress();

                if(response.isSuccessful()){

                    AlarmList alarmList = response.body();

                    alarmArrayList.addAll(alarmList.getItems());
                    adapter = new AlarmAdapter(AlarmActivity.this, alarmArrayList);

                    recyclerView.setAdapter(adapter);

                    Log.d("API Response", "Success");

                    for (int i = 0; i < alarmArrayList.size(); i++) {
                        Alarm alarm = alarmArrayList.get(i);
                        startAlarm(alarm, i); // i 값을 고유한 requestCode로 사용
                    }




                }else {

                    // 유저한테, 서버에 문제가 있다고 메시지를 띄운다.

                    Log.e("API Response", "Error: " + response.code());

                }

            }

            @Override
            public void onFailure(Call<AlarmList> call, Throwable t) {

                dismissProgress();

                Log.e("API Response", "Failure: " + t.getMessage());

            }
        });


    }

    void startAlarm(Alarm alarm, int uniqueRequestCode) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, AlertReceiver.class);
        alarmIntent.putExtra("content", alarm.getContent());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, uniqueRequestCode, alarmIntent, PendingIntent.FLAG_IMMUTABLE);

        String alarmDate = alarm.getAlarm();
        int y = Integer.parseInt(alarmDate.substring(0, 4));
        int m = Integer.parseInt(alarmDate.substring(5, 7));
        int d = Integer.parseInt(alarmDate.substring(8, 10));
        int h = Integer.parseInt(alarmDate.substring(11, 13));
        int mi = Integer.parseInt(alarmDate.substring(14, 16));

        Calendar alarmCalendar = Calendar.getInstance();
        alarmCalendar.set(y, m - 1, d, h, mi, 0);

        // 현재 시간과 알람 시간을 비교하여 이미 지난 알람은 설정하지 않음
        if (alarmCalendar.before(Calendar.getInstance())) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), pendingIntent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        getNetworkData();

    }

    Dialog dialog;


    void showProgress(){
        dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(new ProgressBar(this));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    void dismissProgress(){
        dialog.dismiss();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(AlarmActivity.this, MainActivity.class);
        intent.putExtra("itemId", 0);
        startActivity(intent);
        finish();
    }

}

