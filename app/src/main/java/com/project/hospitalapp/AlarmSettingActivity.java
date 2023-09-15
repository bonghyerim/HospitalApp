package com.project.hospitalapp;

import static com.project.hospitalapp.LoginActivity.checkLogin;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;
import com.project.hospitalapp.adapter.AlarmAdapter;
import com.project.hospitalapp.api.AlarmApi;
import com.project.hospitalapp.api.NetworkClient;
import com.project.hospitalapp.config.Config;
import com.project.hospitalapp.model.Alarm;
import com.project.hospitalapp.model.ResultRes;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AlarmSettingActivity extends AppCompatActivity{

    Button btnDate;
    Button btnTime;


    Button btnSave;

    EditText editContent;

    ArrayList<Alarm> alarmArrayList = new ArrayList<Alarm>();

    AlarmAdapter adapter;

    TextView txtDate;
    TextView txtTime;



    int y=0, m=0, d=0;

    Integer h, mi;

    String hour;

    String minutes;

    String strMonth;

    String day;

    String alarm;

    String content;

    Alarm dateAlarm;

    BottomNavigationView bottomNavigationView;




    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_setting);

        // 커스텀 뒤로가기 버튼 사용하기.
        Drawable customBackButton = getResources().getDrawable(R.drawable.baseline_arrow_back_24);
        getSupportActionBar().setHomeAsUpIndicator(customBackButton);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("알람"); // 원하는 타이틀로 변경
        }

        btnDate = findViewById(R.id.btnDate);
        btnTime = findViewById(R.id.btnTime);
        btnSave = findViewById(R.id.btnSave);
        editContent = findViewById(R.id.editContent);
        txtTime = findViewById(R.id.txtTime);
        txtDate = findViewById(R.id.txtDate);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        adapter = new AlarmAdapter(this, alarmArrayList);

        Log.d("AlarmSettingActivity", "Adapter created");


        txtTime.setText("");
        txtDate.setText("");


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                checkLogin(AlarmSettingActivity.this, itemId);

                return false;
            }
        });


        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDate();




            }
        });

        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                showTime();


            }
        });



        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                content = editContent.getText().toString().trim();



                if (y != 0 && m != 0 && d != 0 && h != null && mi != null && !content.isEmpty()) {

                    dateAlarm = new Alarm(content, alarm);

                    startAlarm(dateAlarm);


                    editContent.setText("");







                }else {
                    if (h == null || mi == null) {
                        Toast.makeText(getApplicationContext(), "시간과 분을 선택하세요", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "필수항목을 전부 입력하세요", Toast.LENGTH_SHORT).show();
                    }
                    return;

                }

                Toast.makeText(getApplicationContext(), "저장 되었습니다.",Toast.LENGTH_SHORT).show();








            }
        });
    }

    void startAlarm(Alarm alarm) {
        showProgress();

        // 네트워크로 데이터를 보낸다.

        Retrofit retrofit = NetworkClient.getRetrofitClient(AlarmSettingActivity.this);

        AlarmApi api = retrofit.create(AlarmApi.class);

        SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String accessToken = sp.getString(Config.ACCESS_TOKEN, "");



        Call<ResultRes> call = api.addAlarm("Bearer "+ accessToken, dateAlarm);

        Log.d("AlarmSettingActivity", "dateAlarm content: " + dateAlarm.getContent());
        Log.d("AlarmSettingActivity", "dateAlarm alarm: " + dateAlarm.getAlarm());

        Log.d("RetrofitRequest", "Request URL: " + call.request().url());

        call.enqueue(new Callback<ResultRes>() {
            @Override
            public void onResponse(Call<ResultRes> call, Response<ResultRes> response) {
                dismissProgress();

                if(response.isSuccessful()){

                    finish();


                }else {

                    // 유저한테, 서버에 문제가 있다고 메시지를 띄운다.

                    Snackbar.make(btnSave,
                            "서버에 문제가 있습니다",
                            Snackbar.LENGTH_SHORT).show();

                }


            }

            @Override
            public void onFailure(Call<ResultRes> call, Throwable t) {

                dismissProgress();

                Snackbar.make(btnSave,
                        "서버와 통신이 되지 있습니다",
                        Snackbar.LENGTH_SHORT).show();





            }
        });
    }
    void showDate() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                y = year;
                m = month+1;
                d = dayOfMonth;

                // 업데이트된 값을 사용하여 날짜 문자열 생성
                if(month < 10){
                    strMonth = "0" + m;
                }else{
                    strMonth = "" + m;
                }


                if(d < 10){

                    day = "0"+ d;
                }else{
                    day = "" + d;
                }
                String Date =  y + "-" + strMonth + "-" + day;
                txtDate.setText(Date);


            }
        },calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.setMessage("메시지");
        datePickerDialog.show();

    }

    void showTime() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                h = hourOfDay;
                mi = minute;

                // 업데이트된 값을 사용하여 시간 문자열 생성
                if(h < 10){
                    hour = "0" + h;
                }else{
                    hour = "" + h;
                }
                if(mi< 10){

                    minutes = "0"+ mi;
                }else{
                    minutes = "" + mi;
                }

                // 날짜와 시간 값을 합쳐서 date 문자열 생성
                alarm = y + "-" + strMonth + "-" + day + " " + hour + ":" + minutes + ":00";

                String Time = hour + ":" + minutes;



                Log.d("AlarmSettingActivity", "알림 설정 시간: " + alarm);

                txtTime.setText(Time);

            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

        timePickerDialog.setMessage("메시지");
        timePickerDialog.show();
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
        finish();
        return true;
    }
}