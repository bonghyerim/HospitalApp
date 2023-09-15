package com.project.hospitalapp;

import static com.project.hospitalapp.LoginActivity.checkLogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ImageView;
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

public class AlarmViewActivity extends AppCompatActivity {

    EditText editContent;
    TextView txtDate;
    Button btnDelete;
    Button btnSave;

    AlarmAdapter adapter;


    Alarm alarm;

    ArrayList<Alarm> alarmArrayList = new ArrayList<Alarm>();



    int y=0, m=0, d=0, h=0, mi=0;

    String hour;

    String minutes;

    String strMonth;

    String day;

    String date;


    String content;

    Alarm dateAlarm;

    BottomNavigationView bottomNavigationView;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_view);

        adapter = new AlarmAdapter(this, alarmArrayList);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("알람"); // 원하는 타이틀로 변경
        }

        // 커스텀 뒤로가기 버튼 사용하기.
        Drawable customBackButton = getResources().getDrawable(R.drawable.baseline_arrow_back_24);
        getSupportActionBar().setHomeAsUpIndicator(customBackButton);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editContent = findViewById(R.id.editContent);
        txtDate = findViewById(R.id.txtDate);
        btnDelete = findViewById(R.id.btnDelete);
        btnSave =findViewById(R.id.btnSave);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);


        alarm = (Alarm) getIntent().getSerializableExtra("alarm");



        String alarmDate = alarm.getAlarm();
        alarmDate = alarmDate.replace("T", " ").substring(0, 15+1);


        editContent.setText(alarm.getContent());
        txtDate.setText(alarmDate);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                checkLogin(AlarmViewActivity.this, itemId);

                return false;
            }
        });


        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                showDateTime();




            }
        });




        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               showAlertDialog();
            }
        });


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String date = txtDate.getText().toString().trim();
                String content = editContent.getText().toString().trim();

                if(date.isEmpty() || content.isEmpty()){
                    Snackbar.make(btnSave,
                            "필수 항목은 모두 입력하세요",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }


                showProgress();

                Retrofit retrofit = NetworkClient.getRetrofitClient(AlarmViewActivity.this);
                AlarmApi api = retrofit.create(AlarmApi.class);
                SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                String token = sp.getString(Config.ACCESS_TOKEN, "");
                Alarm alarmUd = new Alarm(content, date);
                Call< ResultRes> call = api.updateAlarm(alarm.alarmId,"Bearer " + token, alarmUd);

                call.enqueue(new Callback<ResultRes>() {
                    @Override
                    public void onResponse(Call<ResultRes> call, Response<ResultRes> response) {
                        dismissProgress();

                        if(response.isSuccessful()){
                            finish();
                            Toast.makeText(getApplicationContext(), "저장 되었습니다.",Toast.LENGTH_SHORT).show();


                        }else{

                        }
                    }

                    @Override
                    public void onFailure(Call<ResultRes> call, Throwable t) {
                        dismissProgress();

                    }
                });


            }
        });

    }

    private void showAlertDialog() {
        Log.d("AlarmViewActivity", "showAlertDialog called");
        AlertDialog.Builder builder = new AlertDialog.Builder(AlarmViewActivity.this);
        builder.setTitle("삭제");
        builder.setMessage("정말 삭제하시겠습니까?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, Context.MODE_PRIVATE);
                String token = sp.getString(Config.ACCESS_TOKEN, "");

                showProgress();

                Retrofit retrofit = NetworkClient.getRetrofitClient(AlarmViewActivity.this);

                AlarmApi api = retrofit.create(AlarmApi.class);

                Call<ResultRes> call = api.deleteAlarm(alarm.alarmId, "Bearer " + token);

                call.enqueue(new Callback<ResultRes>() {
                    @Override
                    public void onResponse(Call<ResultRes> call, Response<ResultRes> response) {
                        dismissProgress();

                        if(response.isSuccessful()){

                            alarmArrayList.remove(alarm);
                            adapter.notifyDataSetChanged();


                            Toast.makeText(getApplicationContext(), "삭제 되었습니다.",Toast.LENGTH_SHORT).show();

                            finish();



                        }else{

                        }

                    }

                    @Override
                    public void onFailure(Call<ResultRes> call, Throwable t) {
                        dismissProgress();

                    }
                });

            }
        });
        builder.setNegativeButton("NO", null);
        builder.setCancelable(true);  // 다른 영역을 클릭하면 사라지게
        builder.show();
    }

    void showDateTime() {
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

                showTime();


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

                date = y + "-" + strMonth + "-" + day + " " + hour + ":" + minutes;
                txtDate.setText(date);




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