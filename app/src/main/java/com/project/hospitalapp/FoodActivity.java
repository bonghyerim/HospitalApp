package com.project.hospitalapp;

import static com.project.hospitalapp.LoginActivity.checkLogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;
import com.project.hospitalapp.api.MedicineApi;
import com.project.hospitalapp.config.Config;

import com.project.hospitalapp.model.Medicine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FoodActivity extends AppCompatActivity {

    ImageView grapefruit;
    ImageView coffee;
    ImageView alcohol;
    ImageView dairyProducts;
    ImageView honey;
    ImageView soda;
    ImageView meat;
    ImageView wheat;
    EditText editFood;
    EditText medicineName;
    TextView Period1;
    TextView Period2;
    Button btnSave;

    String strMonth;

    String day;

    String date2;

    String date;

    Button breakfast;

    Button lunch;

    Button dinner;

    BottomNavigationView bottomNavigationView;

    Medicine medicine;

    MedicineApi adapter;

    ArrayList<Medicine> medicineArrayList = new ArrayList<Medicine>();



    int y, m, d = 0;

    List<String> foodList2 = new ArrayList<>();

    List<String> alarm = new ArrayList<>();

    String selectedDate1 = "";
    String selectedDate2 = "";

    String alarmEnd;
    String alarmStart;

    Boolean updateMedicine;


    public static final int RESULT_MEDICINE_SAVE = 150;





    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);


        // 뒤로 가기 버튼 표시
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("정보 입력"); // 원하는 타이틀로 변경
        }

        updateMedicine = getIntent().getBooleanExtra("update", false);

        grapefruit = findViewById(R.id.grapefruit);
        coffee = findViewById(R.id.coffee);
        alcohol = findViewById(R.id.alcohol);
        dairyProducts = findViewById(R.id.dairyProducts);
        honey = findViewById(R.id.honey);
        soda = findViewById(R.id.soda);
        meat = findViewById(R.id.meat);
        wheat = findViewById(R.id.wheat);
        editFood = findViewById(R.id.editFood);
        medicineName = findViewById(R.id.medicineName);
        breakfast = findViewById(R.id.breakfast);
        lunch = findViewById(R.id.lunch);
        dinner = findViewById(R.id.dinner);
        Period1 = findViewById(R.id.Period1);
        Period2 = findViewById(R.id.Period2);
        btnSave = findViewById(R.id.btnSave);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);



        medicine = (Medicine) getIntent().getSerializableExtra("medicine");




        if(updateMedicine == true)
            if(medicine.getFoodTag() != null){
                String foodTags = TextUtils.join(", ", medicine.getFoodTag());
                editFood.setText(foodTags);
                foodList2.addAll(medicine.getFoodTag());
                Log.i("저장된 리스트", "foodList: " + foodList2);

            }

        if(updateMedicine == true) {
            if (medicine.getAlarm() != null) {

                alarm.addAll(medicine.getAlarm());

                if (alarm.contains("아침")) {
                    breakfast.setBackgroundColor(Color.parseColor("#3D5AFD"));
                }

                if (alarm.contains("점심")) {
                    lunch.setBackgroundColor(Color.parseColor("#3D5AFD"));
                }

                if (alarm.contains("저녁")) {
                    dinner.setBackgroundColor(Color.parseColor("#3D5AFD"));
                }

            }
        }


        if(updateMedicine == true){

            if(medicine.getMedicineName() != null){

                medicineName.setText(medicine.getMedicineName());
            }
        }


        if(updateMedicine == true){

            if(medicine.getStartMedicine() != null && medicine.getEndMedicine() != null){

                alarmStart = medicine.getStartMedicine().replace("T", " ").substring(0, 18+1);
                alarmEnd =  medicine.endMedicine.replace("T", " ").substring(0, 18+1);
                Period1.setText(alarmStart);
                Period2.setText(alarmEnd);
            }

        }




        if(foodList2 != null) {
            for (String food : foodList2) {
                switch (food) {
                    case "커피":
                        coffee.setImageResource(R.drawable.coffee_1);
                        break;
                    case "자몽":
                        grapefruit.setImageResource(R.drawable.grapefruit_1);
                        break;
                    case "술":
                        alcohol.setImageResource(R.drawable.alcohol_1);
                        break;
                    case "우유":
                        dairyProducts.setImageResource(R.drawable.dairyproducts_1);
                        break;
                    case "꿀":
                        honey.setImageResource(R.drawable.honey_1);
                        break;
                    case "탄산":
                        soda.setImageResource(R.drawable.soda_1);
                        break;
                    case "돼지고기":
                        meat.setImageResource(R.drawable.pork_1);
                        break;
                    case "밀":
                        wheat.setImageResource(R.drawable.wheat_1);
                        break;
                }
            }
        }
















        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                checkLogin(FoodActivity.this, itemId);

                return false;
            }
        });


        grapefruit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (foodList2.contains("자몽")) {
                    foodList2.remove("자몽");
                    grapefruit.setImageResource(R.drawable.grapefruit);
                } else {
                    foodList2.add("자몽");
                    grapefruit.setImageResource(R.drawable.grapefruit_1);
                }

                updateEditFood();

            }
        });

        coffee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (foodList2.contains("커피")) {
                    foodList2.remove("커피");
                    coffee.setImageResource(R.drawable.coffee);
                } else {
                    foodList2.add("커피");
                    coffee.setImageResource(R.drawable.coffee_1);
                }

                updateEditFood();
            }
        });
        alcohol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (foodList2.contains("술")) {
                    foodList2.remove("술");
                    alcohol.setImageResource(R.drawable.alcohol);
                } else {
                    foodList2.add("술");
                    alcohol.setImageResource(R.drawable.alcohol_1);
                }

                updateEditFood();
            }
        });



        dairyProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (foodList2.contains("우유")) {
                    foodList2.remove("우유");
                    foodList2.remove("분유");
                    foodList2.remove("원유");
                    dairyProducts.setImageResource(R.drawable.dairyproducts);
                } else {
                    foodList2.add("우유");
                    foodList2.add("분유");
                    foodList2.add("원유");
                    dairyProducts.setImageResource(R.drawable.dairyproducts_1);
                }

                updateEditFood();
            }
        });

        honey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (foodList2.contains("꿀")) {
                    foodList2.remove("꿀");
                    honey.setImageResource(R.drawable.honey);
                } else {
                    foodList2.add("꿀");
                    honey.setImageResource(R.drawable.honey_1);
                }

                updateEditFood();
            }
        });
        soda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (foodList2.contains("탄산")) {
                    foodList2.remove("탄산");
                    soda.setImageResource(R.drawable.soda);
                } else {
                    foodList2.add("탄산");
                    soda.setImageResource(R.drawable.soda_1);
                }

                updateEditFood();
            }
        });
        meat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (foodList2.contains("돼지고기")) {
                    foodList2.remove("돼지고기");
                    meat.setImageResource(R.drawable.pork);
                } else {
                    foodList2.add("돼지고기");
                    meat.setImageResource(R.drawable.pork_1);
                }

                updateEditFood();
            }
        });

        wheat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (foodList2.contains("밀")) {
                    foodList2.remove("밀");
                    foodList2.remove("밀가루");
                    wheat.setImageResource(R.drawable.wheat);
                } else {
                    foodList2.add("밀");
                    foodList2.add("밀가루");
                    wheat.setImageResource(R.drawable.wheat_1);
                }

                updateEditFood();
            }
        });












        Period1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDate(1);






            }
        });

        Period2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDate(2);

            }
        });



        editFood.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                //해당 텍스트가 없으면 리스트에서 제거하는 작업을 실행합니다

                Iterator<String> iterator = foodList2.iterator();
                while (iterator.hasNext()) {
                    String food = iterator.next();
                    if (!food.equals("자몽") && !food.equals("커피") && !food.equals("우유") && !food.equals("원유") && !food.equals("분유") && !food.equals("술")&& !food.equals("꿀")
                            && !food.equals("탄산") && !food.equals("돼지고기") && !food.equals("밀") && !food.equals("밀가루") ) {
                        iterator.remove();
                    }
                }

                String strFood = editFood.getText().toString().trim();

                if (!strFood.contains("자몽")){

                    grapefruit.setImageResource(R.drawable.grapefruit);
                    foodList2.remove("자몽");
                }else{
                    grapefruit.setImageResource(R.drawable.grapefruit_1);

                }

                if (!strFood.contains("커피")){

                    coffee.setImageResource(R.drawable.coffee);
                    foodList2.remove("커피");
                }else{
                    coffee.setImageResource(R.drawable.coffee_1);
                }

                if (!strFood.contains("술")){

                    alcohol.setImageResource(R.drawable.alcohol);
                    foodList2.remove("술");
                }else{
                    alcohol.setImageResource(R.drawable.alcohol_1);
                }

                if (!strFood.contains("우유") && !strFood.contains("원유") &&(!strFood.contains("분유"))){

                    dairyProducts.setImageResource(R.drawable.dairyproducts);
                    foodList2.remove("우유");
                    foodList2.remove("원유");
                    foodList2.remove("분유");
                }else{
                    dairyProducts.setImageResource(R.drawable.dairyproducts_1);
                }
                if (!strFood.contains("꿀")){

                    honey.setImageResource(R.drawable.honey);
                    foodList2.remove("꿀");
                }else{
                    honey.setImageResource(R.drawable.honey_1);
                }
                if (!strFood.contains("탄산")){

                    soda.setImageResource(R.drawable.soda);
                    foodList2.remove("탄산");
                }else{
                    soda.setImageResource(R.drawable.soda_1);
                }
                if (!strFood.contains("돼지고기")){

                    meat.setImageResource(R.drawable.pork);
                    foodList2.remove("돼지고기");
                }else{
                    meat.setImageResource(R.drawable.pork_1);
                }
                if (!strFood.contains("밀")){

                    wheat.setImageResource(R.drawable.wheat);
                    foodList2.remove("밀");
                    foodList2.remove("밀가루");
                }else{
                    wheat.setImageResource(R.drawable.wheat_1);
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });


        breakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String period1Text = Period1.getText().toString();
                String period2Text = Period2.getText().toString();

                if (!alarm.contains("아침")) {

                    Toast.makeText(FoodActivity.this, "아침 선택시 07시에 알람이 울립니다.", Toast.LENGTH_SHORT).show();}



                if (alarm.contains("아침") && alarm.contains("점심") && alarm.contains("저녁")) {
                    alarm.remove("아침");
                    breakfast.setBackgroundColor(Color.parseColor("#C3C3C3"));
                    if(!period1Text.isEmpty()  && !period2Text.isEmpty()){

                        Period1.setText(period1Text.substring(0,9+1) + " " + "12:00:00");
                        Period2.setText(period2Text.substring(0,9+1) + " " + "18:00:00");}


                }else if(alarm.contains("아침") && !alarm.contains("점심") && alarm.contains("저녁")){

                    alarm.remove("아침");
                    breakfast.setBackgroundColor(Color.parseColor("#C3C3C3"));
                    if(!period1Text.isEmpty()  && !period2Text.isEmpty()){

                        Period1.setText(period1Text.substring(0,9+1) + " " + "18:00:00");
                        Period2.setText(period2Text.substring(0,9+1) + " " + "18:00:00");}



                }else if(alarm.contains("아침")  && alarm.contains("점심") && !alarm.contains("저녁")){

                    alarm.remove("아침");
                    breakfast.setBackgroundColor(Color.parseColor("#C3C3C3"));
                    if(!period1Text.isEmpty()  && !period2Text.isEmpty()){
                        Period1.setText(period1Text.substring(0,9+1) + " " + "12:00:00");
                        Period2.setText(period2Text.substring(0,9+1) + " " + "12:00:00");}



                }else if(alarm.contains("아침")  && !alarm.contains("점심")  &&  !alarm.contains("저녁")){

                    alarm.remove("아침");
                    breakfast.setBackgroundColor(Color.parseColor("#C3C3C3"));
                    if(!period1Text.isEmpty()  && !period2Text.isEmpty()){
                        Period1.setText(period1Text.substring(0,9+1) + " " + "00:00:00");
                        Period2.setText(period2Text.substring(0,9+1) + " " + "24:00:00");}


                }else if(!alarm.contains("아침")  && alarm.contains("점심")  &&  alarm.contains("저녁")){
                    alarm.add("아침");
                    breakfast.setBackgroundColor(Color.parseColor("#3D5AFD"));
                    if(!period1Text.isEmpty()  && !period2Text.isEmpty()){
                        Period1.setText(period1Text.substring(0,9+1) + " " + "07:00:00");
                        Period2.setText(period2Text.substring(0,9+1) + " " + "18:00:00");}

                }else if(!alarm.contains("아침")  && alarm.contains("점심")  &&  !alarm.contains("저녁")){
                    alarm.add("아침");
                    breakfast.setBackgroundColor(Color.parseColor("#3D5AFD"));
                    if(!period1Text.isEmpty()  && !period2Text.isEmpty()){
                        Period1.setText(period1Text.substring(0,9+1) + " " + "07:00:00");
                        Period2.setText(period2Text.substring(0,9+1) + " " + "12:00:00");}

                }else if(!alarm.contains("아침")  && !alarm.contains("점심")  &&  alarm.contains("저녁")) {
                    alarm.add("아침");
                    breakfast.setBackgroundColor(Color.parseColor("#3D5AFD"));
                    if (!period1Text.isEmpty() && !period2Text.isEmpty()) {
                        Period1.setText(period1Text.substring(0,9+1) + " " + "07:00:00");
                        Period2.setText(period2Text.substring(0,9+1) + " " + "12:00:00");
                    }

                }else if(!alarm.contains("아침")  &&!alarm.contains("점심") && !alarm.contains("저녁")){
                    alarm.add("아침");
                    breakfast.setBackgroundColor(Color.parseColor("#3D5AFD"));
                    if(!period1Text.isEmpty() && !period2Text.isEmpty()){
                        Period1.setText(period1Text.substring(0,9+1) + " " + "07:00:00");
                        Period2.setText(period2Text.substring(0,9+1) + " " + "07:00:00");
                    }
                }

            }
        });


        lunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String period1Text = Period1.getText().toString();
                String period2Text = Period2.getText().toString();

                if (!alarm.contains("점심")) {

                    Toast.makeText(FoodActivity.this, "점심 선택시 12시에 알람이 울립니다.", Toast.LENGTH_SHORT).show();}

                if (alarm.contains("아침") && alarm.contains("점심") && alarm.contains("저녁")) {
                    alarm.remove("점심");
                    lunch.setBackgroundColor(Color.parseColor("#C3C3C3"));
                    if(!period1Text.isEmpty()  && !period2Text.isEmpty()){

                        Period1.setText(period1Text.substring(0,9+1) + " " + "07:00:00");
                        Period2.setText(period2Text.substring(0,9+1) + " " + "18:00:00");}

                }else if(!alarm.contains("아침") && alarm.contains("점심") && alarm.contains("저녁")){
                    alarm.remove("점심");
                    lunch.setBackgroundColor(Color.parseColor("#C3C3C3"));
                    if(!period1Text.isEmpty()  && !period2Text.isEmpty()){
                        Period1.setText(period1Text.substring(0,9+1) + " " + "18:00:00");
                        Period2.setText(period2Text.substring(0,9+1) + " " + "18:00:00");}


                }else if(alarm.contains("아침")  && alarm.contains("점심") && !alarm.contains("저녁")){
                    Log.d("AlarmList", "Alarm List: " + alarm.toString());
                    alarm.remove("점심");
                    lunch.setBackgroundColor(Color.parseColor("#C3C3C3"));
                    if(!period1Text.isEmpty()  && !period2Text.isEmpty()){
                        Period1.setText(period1Text.substring(0,9+1) + " " + "07:00:00");
                        Period2.setText(period2Text.substring(0,9+1) + " " + "07:00:00");}



                }else if(!alarm.contains("아침")  && alarm.contains("점심")  &&  !alarm.contains("저녁")){
                    Log.d("AlarmList", "Alarm List: " + alarm.toString());
                    alarm.remove("점심");
                    lunch.setBackgroundColor(Color.parseColor("#C3C3C3"));
                    if(!period1Text.isEmpty()  && !period2Text.isEmpty()){
                        Period1.setText(period1Text.substring(0,9+1) + " " + "00:00:00");
                        Period2.setText(period2Text.substring(0,9+1) + " " + "24:00:00");}

                }else if(alarm.contains("아침")  && !alarm.contains("점심")  &&  alarm.contains("저녁")){
                    alarm.add("점심");
                    lunch.setBackgroundColor(Color.parseColor("#3D5AFD"));
                    if(!period1Text.isEmpty()  && !period2Text.isEmpty()){
                        Period1.setText(period1Text.substring(0,9+1) + " " + "07:00:00");
                        Period2.setText(period2Text.substring(0,9+1) + " " + "18:00:00");}

                }else if(!alarm.contains("아침")  && !alarm.contains("점심")  &&  alarm.contains("저녁")){
                    alarm.add("점심");
                    lunch.setBackgroundColor(Color.parseColor("#3D5AFD"));
                    if(!period1Text.isEmpty()  && !period2Text.isEmpty()){
                        Period1.setText(period1Text.substring(0,9+1) + " " + "12:00:00");
                        Period2.setText(period2Text.substring(0,9+1) + " " + "18:00:00");}

                }else if(alarm.contains("아침")  && !alarm.contains("점심")  &&  !alarm.contains("저녁")){
                    alarm.add("점심");
                    lunch.setBackgroundColor(Color.parseColor("#3D5AFD"));
                    if(!period1Text.isEmpty()  && !period2Text.isEmpty()){
                        Period1.setText(period1Text.substring(0,9+1) + " " + "07:00:00");
                        Period2.setText(period2Text.substring(0,9+1) + " " + "12:00:00");}

                }else if(!alarm.contains("아침")  && !alarm.contains("점심") && !alarm.contains("저녁")){
                    alarm.add("점심");
                    lunch.setBackgroundColor(Color.parseColor("#3D5AFD"));
                    if(!period1Text.isEmpty() && !period2Text.isEmpty()){
                        Period1.setText(period1Text.substring(0,9+1) + " " + "12:00:00");
                        Period2.setText(period2Text.substring(0,9+1) + " " + "12:00:00");
                    }
                }

            }
        });

        dinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String period1Text = Period1.getText().toString();
                String period2Text = Period2.getText().toString();
                if (!alarm.contains("저녁")) {

                    Toast.makeText(FoodActivity.this, "저녁 선택시 18시에 알람이 울립니다.", Toast.LENGTH_SHORT).show();}

                if (alarm.contains("아침") && alarm.contains("점심") && alarm.contains("저녁")) {
                    alarm.remove("저녁");
                    dinner.setBackgroundColor(Color.parseColor("#C3C3C3"));
                    if(!period1Text.isEmpty()  && !period2Text.isEmpty()){
                        Period1.setText(period1Text.substring(0,9+1) + " " + "07:00:00");
                        Period2.setText(period2Text.substring(0,9+1) + " " + "12:00:00");}

                }else if(!alarm.contains("아침") && alarm.contains("점심") && alarm.contains("저녁")){
                    alarm.remove("저녁");
                    dinner.setBackgroundColor(Color.parseColor("#C3C3C3"));
                    if(!period1Text.isEmpty()  && !period2Text.isEmpty()){
                        Period1.setText(period1Text.substring(0,9+1) + " " + "12:00:00");
                        Period2.setText(period2Text.substring(0,9+1) + " " + "12:00:00");}


                }else if(alarm.contains("아침")  && !alarm.contains("점심") && alarm.contains("저녁")){

                    alarm.remove("저녁");
                    dinner.setBackgroundColor(Color.parseColor("#C3C3C3"));
                    if(!period1Text.isEmpty()  && !period2Text.isEmpty()){
                        Period1.setText(period1Text.substring(0,9+1) + " " + "07:00:00");
                        Period2.setText(period2Text.substring(0,9+1) + " " + "07:00:00");}



                }else if(!alarm.contains("아침")  && !alarm.contains("점심")  &&  alarm.contains("저녁")){

                    alarm.remove("저녁");
                    dinner.setBackgroundColor(Color.parseColor("#C3C3C3"));
                    if(!period1Text.isEmpty()  && !period2Text.isEmpty()){
                        Period1.setText(period1Text.substring(0,9+1) + " " + "00:00:00");
                        Period2.setText(period2Text.substring(0,9+1) + " " + "24:00:00");}

                }else if(alarm.contains("아침")  && alarm.contains("점심")  &&  !alarm.contains("저녁")){
                    alarm.add("저녁");
                    dinner.setBackgroundColor(Color.parseColor("#3D5AFD"));
                    if(!period1Text.isEmpty()  && !period2Text.isEmpty()){
                        Period1.setText(period1Text.substring(0,9+1) + " " + "07:00:00");
                        Period2.setText(period2Text.substring(0,9+1) + " " + "18:00:00");}

                }else if(!alarm.contains("아침")  && alarm.contains("점심")  &&  !alarm.contains("저녁")){
                    alarm.add("저녁");
                    dinner.setBackgroundColor(Color.parseColor("#3D5AFD"));
                    if(!period1Text.isEmpty()  && !period2Text.isEmpty()){
                        Period1.setText(period1Text.substring(0,9+1) + " " + "12:00:00");
                        Period2.setText(period2Text.substring(0,9+1) + " " + "18:00:00");}

                }else if(alarm.contains("아침")  && !alarm.contains("점심")  &&  !alarm.contains("저녁")){
                    alarm.add("저녁");
                    dinner.setBackgroundColor(Color.parseColor("#3D5AFD"));
                    if(!period1Text.isEmpty()  && !period2Text.isEmpty()){
                        Period1.setText(period1Text.substring(0,9+1) + " " + "07:00:00");
                        Period2.setText(period2Text.substring(0,9+1) + " " + "18:00:00");}
                }else if(!alarm.contains("아침")  && !alarm.contains("점심")  &&  !alarm.contains("저녁")){
                    alarm.add("저녁");
                    dinner.setBackgroundColor(Color.parseColor("#3D5AFD"));
                    if(!period1Text.isEmpty() && !period2Text.isEmpty()){
                        Period1.setText(period1Text.substring(0,9+1) + " " + "18:00:00");
                        Period2.setText(period2Text.substring(0,9+1) + " " + "18:00:00");
                    }
                }

            }
        });






        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                // editFood의 텍스트 가져오기
                String editFoodText = editFood.getText().toString();
                String period1Text = Period1.getText().toString();
                String period2Text = Period2.getText().toString();
                String medicineNameText = medicineName.getText().toString();

                if (medicineNameText != null && period1Text != null && period2Text != null && !medicineNameText.isEmpty() && !period1Text.isEmpty() && !period2Text.isEmpty()) {
                    // 복용 시간이 빈 칸인 경우 토스트 메시지 표시
                    if (alarm.isEmpty()) {
                        // 수정된 부분: 복용 시간이 빈 칸인 경우 토스트 메시지 표시
                        Snackbar.make(btnSave,"복용 시간을 선택해 주세요.",Snackbar.LENGTH_SHORT).show();
                    } else {
                        // 텍스트를 쉼표를 기준으로 분리하여 음식 리스트에 추가
                        String[] foodArray = editFoodText.split(",");
                        foodList2.clear();
                        for (String food : foodArray) {
                            foodList2.add(food.trim()); // 음식 이름 앞뒤 공백 제거 후 리스트에 추가
                        }

                        SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                        String accessToken = sp.getString(Config.ACCESS_TOKEN, "");

                        Log.i("TEST", foodList2.get(0));


                        JSONArray foodArrayJson = new JSONArray();
                        if (foodList2.size() != 0 && !foodList2.get(0).isEmpty()) {
                            for (String food : foodList2) {
                                foodArrayJson.put(food);
                            }
                        }


                        JSONArray alarmArrayJson = new JSONArray();
                        for (String alarmItem : alarm) {
                            alarmArrayJson.put(alarmItem);
                        }

                        JSONObject requestData = new JSONObject();
                        try {
                            requestData.put("medicineName", medicineNameText);
                            requestData.put("startMedicine", period1Text);
                            requestData.put("endMedicine", period2Text);
                            requestData.put("food", foodArrayJson);
                            requestData.put("alarm", alarmArrayJson);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        Log.d("JSON", "JSON request data: " + requestData.toString());


                        RequestQueue queue = Volley.newRequestQueue(FoodActivity.this);
                        showProgress();

                        if (updateMedicine == true) {

                            // 업데이트할때 코드
                            JsonObjectRequest request = new JsonObjectRequest(
                                    Request.Method.POST,
                                    Config.HOST + "/medicine" + "/" + medicine.getId(),
                                    requestData,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {

                                            try {
                                                String result = response.getString("result");

                                                Log.i("EMPLOYER_APP", "Response result: " + result);
                                                // 저장이 성공적으로 이루어졌을 때 Toast 메시지로 출력
                                                if ("success".equals(result)) {

                                                    Log.i("TEST", "로그 테스트");
                                                    int id = response.getInt("medicineId");
                                                    Medicine medicine = new Medicine(id, medicineNameText, period1Text, period2Text, (ArrayList<String>) foodList2, (ArrayList<String>) alarm);
                                                    Intent intent = new Intent();
                                                    intent.putExtra("medicine", medicine);
                                                    setResult(RESULT_MEDICINE_SAVE, intent);
                                                    Log.i("TEST1", medicine.medicineName);
                                                    Toast.makeText(FoodActivity.this, "약 정보가 성공적으로 저장되었습니다.", Toast.LENGTH_SHORT).show();

                                                    finish(); // todo: 런쳐 확인하기.

                                                } else if ("success".equals(result) && !editFoodText.isEmpty() && !medicineNameText.isEmpty() && !period1Text.isEmpty() && !period2Text.isEmpty()) {
                                                    Toast.makeText(FoodActivity.this, "약과 음식 정보가 성공적으로 저장되었습니다.", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                } else
                                                    Toast.makeText(FoodActivity.this, "정보가 저장되지 않았습니다.", Toast.LENGTH_SHORT).show();


                                            } catch (JSONException e) {
                                                dismissProgress();
                                                Log.e("EMPLOYER_APP", "JSON parsing error: " + e.toString());
                                                return;

                                            }

                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.e("EMPLOYER_APP", "Volley error: " + error.toString());
                                            dismissProgress();

                                        }
                                    }
                            ) {
                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    Map<String, String> headers = new HashMap<>();
                                    headers.put("Authorization", "Bearer " + accessToken);
                                    // "Authorization" 헤더에 "Bearer"와 토큰 값을 넣습니다.
                                    return headers;
                                }
                            };

                            queue.add(request);
                        } else {
                            // 등록할 때 코드
                            JsonObjectRequest request = new JsonObjectRequest(
                                    Request.Method.POST,
                                    Config.HOST + "/medicine",
                                    requestData,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {

                                            try {
                                                String result = response.getString("result");

                                                Log.i("EMPLOYER_APP", "Response result: " + result);
                                                // 저장이 성공적으로 이루어졌을 때 Toast 메시지로 출력
                                                if ("success".equals(result)) {

                                                    Log.i("TEST", "로그 테스트");
                                                    int id = response.getInt("medicineId");
                                                    Medicine medicine = new Medicine(id, medicineNameText, period1Text, period2Text, (ArrayList<String>) foodList2, (ArrayList<String>) alarm);
                                                    Intent intent = new Intent();
                                                    intent.putExtra("medicine", medicine);
                                                    setResult(RESULT_MEDICINE_SAVE, intent);
                                                    Log.i("TEST1", medicine.medicineName);
                                                    Toast.makeText(FoodActivity.this, "약 정보가 성공적으로 저장되었습니다.", Toast.LENGTH_SHORT).show();

                                                    finish(); // todo: 런쳐 확인하기.

                                                } else if ("success".equals(result) && !editFoodText.isEmpty() && !medicineNameText.isEmpty() && !period1Text.isEmpty() && !period2Text.isEmpty()) {
                                                    Toast.makeText(FoodActivity.this, "약과 음식 정보가 성공적으로 저장되었습니다.", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                } else
                                                    Toast.makeText(FoodActivity.this, "정보가 저장되지 않았습니다.", Toast.LENGTH_SHORT).show();


                                            } catch (JSONException e) {
                                                dismissProgress();
                                                Log.e("EMPLOYER_APP", "JSON parsing error: " + e.toString());
                                                return;

                                            }

                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.e("EMPLOYER_APP", "Volley error: " + error.toString());
                                            dismissProgress();

                                        }
                                    }
                            ) {
                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    Map<String, String> headers = new HashMap<>();
                                    headers.put("Authorization", "Bearer " + accessToken);
                                    // "Authorization" 헤더에 "Bearer"와 토큰 값을 넣습니다.
                                    return headers;
                                }
                            };
                            queue.add(request);

                        }
                    }


                } else {
                    // 수정된 부분: 약 정보와 기간이 필수로 입력되어야 하는 경우 토스트 메시지 표시
                    Snackbar.make(btnSave, "약 정보와 기간은 필수입니다.", Snackbar.LENGTH_SHORT).show();
                    return;

                }
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.icon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.menuAlarm){
            checkLogin(FoodActivity.this, AlarmActivity.class);

        } else if(itemId == R.id.menuInfo){
            checkLogin(FoodActivity.this, MyInformationActivity.class);

        }
        return super.onOptionsItemSelected(item);
    }


    void showDate(final int periodNumber) {
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

                boolean containsMorning = alarm.contains("아침");
                boolean containsLunch = alarm.contains("점심");
                boolean containsDinner = alarm.contains("저녁");




                if(containsMorning) {

                    date = y + "-" + strMonth + "-" + day + " " + "07:00:00";
                }else if(containsLunch ){
                    date = y + "-" + strMonth + "-" + day + " " + "12:00:00";

                }else if(containsDinner){
                    date = y + "-" + strMonth + "-" + day + " " + "18:00:00";
                }else{
                    date = y + "-" + strMonth + "-" + day + " " + "00:00:00";
                }

                if(containsDinner){
                    date2 = y + "-" + strMonth + "-" + day + " " + "18:00:00";

                }else if (containsLunch){
                    date2 = y + "-" + strMonth + "-" + day + " " + "12:00:00";
                }else if(containsMorning){
                    date2 = y + "-" + strMonth + "-" + day + " " + "07:00:00";
                }else{
                    date2 = y + "-" + strMonth + "-" + day + " " + "24:00:00";
                }




                // 선택된 날짜를 변수에 저장
                if (periodNumber == 1) {
                    selectedDate1 = date;
                    Period1.setText(date);
                } else if (periodNumber == 2) {
                    selectedDate2 = date2;
                    Period2.setText(date2);
                }


            }
        },calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));


        datePickerDialog.setMessage("메시지");
        datePickerDialog.show();


    }
    private void updateEditFood() {
        StringBuilder foodText = new StringBuilder();
        for (String food : foodList2) {
            foodText.append(food).append(", "); // 각 음식 이름을 추가하고 쉼표와 공백을 붙임
        }
        if (foodText.length() > 0) {
            // 마지막에 추가된 쉼표와 공백 제거
            foodText.setLength(foodText.length() - 2);
        }
        editFood.setText(foodText.toString()); // EditText에 표시
    }

    Dialog dialog;

    void showProgress(){
        dialog = new Dialog(this);
        dialog.setContentView(new ProgressBar(this));
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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