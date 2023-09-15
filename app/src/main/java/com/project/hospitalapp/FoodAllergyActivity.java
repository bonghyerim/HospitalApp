package com.project.hospitalapp;

import static com.project.hospitalapp.LoginActivity.checkLogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.project.hospitalapp.config.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FoodAllergyActivity extends AppCompatActivity {

    ImageView eggs;
    ImageView milk;
    ImageView peanut;
    ImageView bean;
    ImageView buckwheat;
    ImageView wheat;
    ImageView walnut;
    ImageView crab;
    ImageView shrimp;
    ImageView squid;
    ImageView mackerel;
    ImageView shellfish;
    ImageView peach;
    ImageView tomato;
    ImageView chicken;
    ImageView pork;
    EditText editFood;
    Button btnSave;
    BottomNavigationView bottomNavigationView;

    List<String> foodList;

    public static final int RESULT_FOOD_SAVE = 100;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_allergy);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 키
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);

        eggs = findViewById(R.id.eggs);
        milk = findViewById(R.id.milk);
        peanut = findViewById(R.id.peanut);
        bean =findViewById(R.id.bean);
        buckwheat = findViewById(R.id.buckwheat);
        wheat = findViewById(R.id.wheat);
        walnut = findViewById(R.id.walnut);
        crab = findViewById(R.id.crab);
        shrimp = findViewById(R.id.shrimp);
        squid = findViewById(R.id.squid);
        mackerel = findViewById(R.id.mackerel);
        shellfish = findViewById(R.id.shellfish);
        peach = findViewById(R.id.peach);
        tomato = findViewById(R.id.tomato);
        chicken = findViewById(R.id.chicken);
        pork = findViewById(R.id.pork);
        editFood = findViewById(R.id.editFood);
        btnSave = findViewById(R.id.btnSave);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);







        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                checkLogin(FoodAllergyActivity.this, itemId);

                return false;
            }
        });

        
        foodList = (List<String>) getIntent().getSerializableExtra("foodList");


        if(foodList != null) {
            for (String food : foodList) {
                switch (food) {
                    case "계란":
                        eggs.setImageResource(R.drawable.eggs_1);
                        break;
                    case "우유":
                        milk.setImageResource(R.drawable.milk_1);
                        break;
                    case "땅콩":
                        peanut.setImageResource(R.drawable.peanut_1);
                        break;
                    case "대두":
                        bean.setImageResource(R.drawable.bean_1);
                        break;
                    case "메밀":
                        buckwheat.setImageResource(R.drawable.buckwheat_1);
                        break;
                    case "밀":
                        wheat.setImageResource(R.drawable.wwheat_1);
                        break;
                    case "호두":
                        walnut.setImageResource(R.drawable.walnut_1);
                        break;
                    case "게":
                        crab.setImageResource(R.drawable.crab_1);
                        break;
                    case "새우":
                        shrimp.setImageResource(R.drawable.shrimp_1);
                        break;
                    case "오징어":
                        squid.setImageResource(R.drawable.squid_1);
                        break;
                    case "고등어":
                        mackerel.setImageResource(R.drawable.mackerel_1);
                        break;
                    case "조개":
                        shellfish.setImageResource(R.drawable.shellfish_1);
                        break;
                    case "복숭아":
                        peach.setImageResource(R.drawable.peach_1);
                        break;
                    case "토마토":
                        tomato.setImageResource(R.drawable.tomato_1);
                        break;
                    case "닭고기":
                        chicken.setImageResource(R.drawable.chicken_1);
                        break;
                    case "돼지고기":
                        pork.setImageResource(R.drawable.ppork_1);
                        break;
                }
            }

            updateEditFood();
        } else {
            foodList = new ArrayList<>();
        }

        editFood.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {




            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                //해당 텍스트가 없으면 리스트에서 제거하는 작업을 실행합니다

                Iterator<String> iterator = foodList.iterator();
                while (iterator.hasNext()) {
                    String food = iterator.next();
                    if (!food.equals("계란") && !food.equals("땅콩") && !food.equals("우유") && !food.equals("원유") && !food.equals("분유") && !food.equals("땅콩")&& !food.equals("대두")&& !food.equals("메밀")&& !food.equals("밀")
                            && !food.equals("호두") && !food.equals("게") && !food.equals("새우") && !food.equals("오징어")&& !food.equals("고등어")&& !food.equals("조개") && !food.equals("밀가루")
                            && !food.equals("복숭아")&& !food.equals("토마토")&& !food.equals("닭고기")&& !food.equals("돼지고기")) {
                        iterator.remove();
                    }
                }







                String strFood = editFood.getText().toString().trim();

                if (!strFood.contains("계란")){

                    eggs.setImageResource(R.drawable.eggs);

                    foodList.remove("계란");
                }else{

                    eggs.setImageResource(R.drawable.eggs_1);
                }

                if (!strFood.contains("땅콩")){
                    peanut.setImageResource(R.drawable.peanut);
                    foodList.remove("땅콩");
                }
                else{
                    peanut.setImageResource(R.drawable.peanut_1);
                }

                if (!strFood.contains("대두")){
                    bean.setImageResource(R.drawable.bean);
                    foodList.remove("대두");
                }else{
                    bean.setImageResource(R.drawable.bean_1);
                }

                if (!strFood.contains("메밀")){


                    buckwheat.setImageResource(R.drawable.buckwheat);

                    foodList.remove("메밀");
                }else{
                    bean.setImageResource(R.drawable.bean_1);
                }

                if (!strFood.contains("우유") && !strFood.contains("원유") &&(!strFood.contains("분유"))){

                    milk.setImageResource(R.drawable.milk);
                    foodList.remove("우유");
                    foodList.remove("원유");
                    foodList.remove("분유");
                }else{
                    milk.setImageResource(R.drawable.milk_1);
                }
                if (!strFood.contains("밀")){

                    wheat.setImageResource(R.drawable.wwheat);
                    foodList.remove("밀");
                    foodList.remove("밀가루");
                }else{
                    wheat.setImageResource(R.drawable.wwheat_1);
                }
                if (!strFood.contains("호두")){

                    walnut.setImageResource(R.drawable.walnut);
                    foodList.remove("호두");
                }else{
                    walnut.setImageResource(R.drawable.walnut_1);
                }
                if (!strFood.contains("게")){

                    crab.setImageResource(R.drawable.crab);
                    foodList.remove("게");
                }else{
                    crab.setImageResource(R.drawable.crab_1);
                }
                if (!strFood.contains("새우")){

                    shrimp.setImageResource(R.drawable.shrimp);
                    foodList.remove("새우");
                }else{
                    shrimp.setImageResource(R.drawable.shrimp_1);
                }
                if (!strFood.contains("오징어")){

                    squid.setImageResource(R.drawable.squid);
                    foodList.remove("오징어");
                }else{
                    squid.setImageResource(R.drawable.squid_1);
                }
                if (!strFood.contains("고등어")){

                    mackerel.setImageResource(R.drawable.mackerel);
                    foodList.remove("고등어");
                }else{
                    mackerel.setImageResource(R.drawable.mackerel_1);
                }
                if (!strFood.contains("조개")){

                    shellfish.setImageResource(R.drawable.shellfish);
                    foodList.remove("조개");
                }else{
                    shellfish.setImageResource(R.drawable.shellfish_1);
                }
                if (!strFood.contains("복숭아")){

                    peach.setImageResource(R.drawable.peach);
                    foodList.remove("복숭아");
                }else{
                    peach.setImageResource(R.drawable.peach_1);
                }
                if (!strFood.contains("토마토")){

                    tomato.setImageResource(R.drawable.tomato);
                    foodList.remove("토마토");
                }else{
                    tomato.setImageResource(R.drawable.tomato_1);
                }

                if (!strFood.contains("닭고기")){

                    chicken.setImageResource(R.drawable.chicken);
                    foodList.remove("닭고기");
                }else{
                    chicken.setImageResource(R.drawable.chicken_1);
                }
                if (!strFood.contains("돼지고기")){

                    pork.setImageResource(R.drawable.ppork);
                    foodList.remove("돼지고기");
                }else{
                    pork.setImageResource(R.drawable.ppork_1);
                }






            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });




        eggs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (foodList.contains("계란")) {
                    foodList.remove("계란");
                    eggs.setImageResource(R.drawable.eggs); // 이전 상태의 그림으로 변경
                } else {
                    foodList.add("계란");
                    eggs.setImageResource(R.drawable.eggs_1);
                }

                updateEditFood();
            }
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("알레르기 유발 음식"); // 원하는 타이틀로 변경
        }

        // 뒤로 가기 버튼 표시
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);


        milk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (foodList.contains("우유")) {
                    foodList.remove("우유");
                    foodList.remove("분유");
                    foodList.remove("원유");
                    milk.setImageResource(R.drawable.milk); // 이전 상태의 그림으로 변경
                } else {
                    foodList.add("우유");
                    foodList.add("분유");
                    foodList.add("원유");
                    milk.setImageResource(R.drawable.milk_1);
                }

                updateEditFood();
            }
        });

        peanut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (foodList.contains("땅콩")) {
                    foodList.remove("땅콩");
                    peanut.setImageResource(R.drawable.peanut); // 이전 상태의 그림으로 변경
                } else {
                    foodList.add("땅콩");
                    peanut.setImageResource(R.drawable.peanut_1);
                }

                updateEditFood();
            }
        });
        bean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (foodList.contains("대두")) {
                    foodList.remove("대두");
                    bean.setImageResource(R.drawable.bean); // 이전 상태의 그림으로 변경
                } else {
                    foodList.add("대두");
                    bean.setImageResource(R.drawable.bean_1);
                }

                updateEditFood();
            }
        });
        buckwheat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (foodList.contains("메밀")) {
                    foodList.remove("메밀");
                    buckwheat.setImageResource(R.drawable.buckwheat); // 이전 상태의 그림으로 변경
                } else {
                    foodList.add("메밀");
                    buckwheat.setImageResource(R.drawable.buckwheat_1);
                }

                updateEditFood();
            }
        });

        wheat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (foodList.contains("밀")) {
                    foodList.remove("밀");
                    foodList.remove("밀가루");
                    wheat.setImageResource(R.drawable.wwheat); // 이전 상태의 그림으로 변경
                } else {
                    foodList.add("밀");
                    foodList.add("밀가루");
                    wheat.setImageResource(R.drawable.wwheat_1);
                }

                updateEditFood();


            }
        });
        walnut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (foodList.contains("호두")) {
                    foodList.remove("호두");
                    walnut.setImageResource(R.drawable.walnut); // 이전 상태의 그림으로 변경
                } else {
                    foodList.add("호두");
                    walnut.setImageResource(R.drawable.walnut_1);
                }

                updateEditFood();
            }
        });
        crab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (foodList.contains("게")) {
                    foodList.remove("게");
                    crab.setImageResource(R.drawable.crab); // 이전 상태의 그림으로 변경
                } else {
                    foodList.add("게");
                    crab.setImageResource(R.drawable.crab_1);
                }

                updateEditFood();
            }
        });

        shrimp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (foodList.contains("새우")) {
                    foodList.remove("새우");
                    shrimp.setImageResource(R.drawable.shrimp); // 이전 상태의 그림으로 변경
                } else {
                    foodList.add("새우");
                    shrimp.setImageResource(R.drawable.shrimp_1);
                }

                updateEditFood();
            }
        });

        squid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (foodList.contains("오징어")) {
                    foodList.remove("오징어");
                    squid.setImageResource(R.drawable.squid); // 이전 상태의 그림으로 변경
                } else {
                    foodList.add("오징어");
                    squid.setImageResource(R.drawable.squid_1);
                }

                updateEditFood();
            }
        });
        mackerel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (foodList.contains("고등어")) {
                    foodList.remove("고등어");
                    mackerel.setImageResource(R.drawable.mackerel); // 이전 상태의 그림으로 변경
                } else {
                    foodList.add("고등어");
                    mackerel.setImageResource(R.drawable.mackerel_1);
                }

                updateEditFood();
            }
        });
        shellfish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (foodList.contains("조개")) {
                    foodList.remove("조개");
                    shellfish.setImageResource(R.drawable.shellfish); // 이전 상태의 그림으로 변경
                } else {
                    foodList.add("조개");
                    shellfish.setImageResource(R.drawable.shellfish_1);
                }

                updateEditFood();
            }
        });
        peach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (foodList.contains("복숭아")) {
                    foodList.remove("복숭아");
                    peach.setImageResource(R.drawable.peach); // 이전 상태의 그림으로 변경
                } else {
                    foodList.add("복숭아");
                    peach.setImageResource(R.drawable.peach_1);
                }

                updateEditFood();
            }
        });
        tomato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (foodList.contains("토마토")) {
                    foodList.remove("토마토");
                    tomato.setImageResource(R.drawable.tomato); // 이전 상태의 그림으로 변경
                } else {
                    foodList.add("토마토");
                    tomato.setImageResource(R.drawable.tomato_1);
                }

                updateEditFood();
            }
        });
        chicken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (foodList.contains("닭고기")) {
                    foodList.remove("닭고기");
                    chicken.setImageResource(R.drawable.chicken); // 이전 상태의 그림으로 변경
                } else {
                    foodList.add("닭고기");
                    chicken.setImageResource(R.drawable.chicken_1);
                }

                updateEditFood();
            }
        });
        pork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (foodList.contains("돼지고기")) {
                    foodList.remove("돼지고기");
                    pork.setImageResource(R.drawable.ppork); // 이전 상태의 그림으로 변경
                } else {
                    foodList.add("돼지고기");
                    pork.setImageResource(R.drawable.ppork_1);
                }

                updateEditFood();
            }
        });


        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // editFood의 텍스트 가져오기
                String editFoodText = editFood.getText().toString();

                // 텍스트를 쉼표를 기준으로 분리하여 음식 리스트에 추가
                String[] foodArray = editFoodText.split(",");
                foodList.clear();
                for (String food : foodArray) {
                    foodList.add(food.trim()); // 음식 이름 앞뒤 공백 제거 후 리스트에 추가
                }

                SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                String accessToken = sp.getString(Config.ACCESS_TOKEN, "");
                Boolean userInformation = sp.getBoolean(Config.USER_INFORMATION, false);

                if(userInformation) {
                    JSONArray foodArrayJson = new JSONArray();
                    for (String food : foodList) {
                        foodArrayJson.put(food);
                    }

                    JSONObject requestData = new JSONObject();
                    try {
                        requestData.put("food", foodArrayJson);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    Log.d("JSON", "JSON request data: " + requestData.toString());

                    RequestQueue queue = Volley.newRequestQueue(FoodAllergyActivity.this);
                    showProgress();
                    JsonObjectRequest request = new JsonObjectRequest(
                            Request.Method.POST,
                            Config.HOST + "/food/user",
                            requestData,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    try {
                                        String result = response.getString("result");

                                        Log.i("EMPLOYER_APP", "Response result: " + result);
                                        // 저장이 성공적으로 이루어졌을 때 Toast 메시지로 출력
                                        if ("success".equals(result)) {
                                            Toast.makeText(FoodAllergyActivity.this, "음식 정보가 성공적으로 저장되었습니다.", Toast.LENGTH_SHORT).show();
                                            finish(); // todo: 내관리도 런쳐로 실행하기.
                                        } else { // todo: 저장되지 않은 상황을 조금 더 디테일하게
                                            Toast.makeText(FoodAllergyActivity.this, "음식 정보가 저장되지 않았습니다.", Toast.LENGTH_SHORT).show();
                                        }

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
                // 세부정보 있는 사람이나 없는 사람이나 실행시킨 액티비티로 foodList 보내도록.
                // 받는 곳에서는 리스트의 길이 확인하기.

                Intent intent = new Intent();
                intent.putExtra("foodList", (Serializable) foodList); // ArrayList<foodList>클래스에 implements Serializable

                setResult(RESULT_FOOD_SAVE, intent); // Result코드 100
                // 실행한 곳으로 값을 돌려주는 것.

                finish();

            }
        });



    }

    private void updateEditFood() {
        StringBuilder foodText = new StringBuilder();
        for (String food : foodList) {
            foodText.append(food).append(", "); // 각 음식 이름을 추가하고 쉼표와 공백을 붙임
        }
        if (foodText.length() > 0) {
            // 마지막에 추가된 쉼표와 공백 제거
            foodText.setLength(foodText.length() - 2);
        }
        editFood.setText(foodText.toString()); // EditText에 표시
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // 뒤로가기 동작 수행
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed(); // 이전 화면으로 돌아가기
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