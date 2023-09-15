package com.project.hospitalapp;

import static com.project.hospitalapp.FoodAllergyActivity.RESULT_FOOD_SAVE;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.project.hospitalapp.config.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UpdateProfileActivity extends AppCompatActivity {

    EditText editName;
    EditText editBirth;
    EditText editHeight;
    EditText editWeight;
    Button btnFinish;
    ImageView imgCheckName;
    RadioGroup radioGroup;

    Button btnAddFood;
    TextView txtAddFood;

    String name;

    ArrayList<String> foodList;

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    // AddActivity에서 데이터를 받는 경우

                    if(result.getResultCode() == RESULT_FOOD_SAVE){
                        // AddActivity.RESULT_ADDACTIVITY_SAVE를 한 뒤에 알트엔터로 자동완성

                        foodList = (ArrayList<String>) result.getData().getSerializableExtra("foodList");

                        if(foodList.size() == 0) {
                            return;
                        }
                        String foodText = "";
                        for (String food : foodList) {
                            foodText += food + ", ";
                        }
                        Log.i("TEST", foodText);
                        foodText = foodText.substring(0, foodText.length() - 2);
                        txtAddFood.setText(foodText);

                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        // 뒤로 가기 버튼 표시
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(""); // 원하는 타이틀로 변경
        }

        editName = findViewById(R.id.editName);
        editBirth = findViewById(R.id.editBirth);
        editHeight = findViewById(R.id.editHeight);
        editWeight = findViewById(R.id.editWeight);
        btnFinish = findViewById(R.id.btnFinish);
        imgCheckName = findViewById(R.id.imgCheckName);
        radioGroup = findViewById(R.id.radioGroup);
        btnAddFood = findViewById(R.id.btnAddFood);
        txtAddFood = findViewById(R.id.txtAddFood);

        editName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                name = editName.getText().toString().trim();

                if(!name.isEmpty() || name == null){
                    imgCheckName.setVisibility(ImageView.VISIBLE);

                    if(name.length() > 2) {
                        imgCheckName.setImageResource(R.drawable.baseline_check_24);
                    } else {
                        imgCheckName.setImageResource(R.drawable.baseline_close_24);
                    }

                } else {
                    imgCheckName.setVisibility(ImageView.GONE);
                }

            }
        });

        btnAddFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(UpdateProfileActivity.this, FoodAllergyActivity.class);

                if(foodList != null && foodList.size() != 0) {
                    intent.putExtra("foodList", foodList);
                }

                launcher.launch(intent);
            }
        });

        btnFinish.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                name = editName.getText().toString().trim();
                if(name == null || name.length() < 3 || name.length() > 12){
                    Snackbar.make(btnFinish,
                            "닉네임 길이를 확인해주세요.",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }

                String birth = editBirth.getText().toString().trim();

                if(birth == null || birth.length() != 8 || !canParseNumber(birth)){
                    Snackbar.make(btnFinish,
                            "생년월일을 확인해주세요.",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }
                birth = formatDate(birth);

                if(birth.equals("error")){
                    Snackbar.make(btnFinish,
                            "생년월일을 확인해주세요.",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }

                String strHeight = editHeight.getText().toString().trim();

                if(strHeight == null || strHeight.isEmpty() || !canParseNumber(strHeight)){
                    Snackbar.make(btnFinish,
                            "키를 확인해주세요.",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }

                double height = Double.parseDouble(strHeight);

                if(height > 250 || height < 30){
                    Snackbar.make(btnFinish,
                            "키를 확인해주세요.",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }

                String strWeight = editWeight.getText().toString().trim();

                if(strWeight == null || strWeight.isEmpty() || !canParseNumber(strWeight)){
                    Snackbar.make(btnFinish,
                            "몸무게를 확인해주세요.",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }

                double weight = Double.parseDouble(strWeight);

                if(weight > 250 || weight < 2){
                    Snackbar.make(btnFinish,
                            "몸무게를 확인해주세요.",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }

                int sex;
                int radioButtonId = radioGroup.getCheckedRadioButtonId();
                if(radioButtonId == R.id.rbtnFemale){
                    sex = 0;
                }else if(radioButtonId == R.id.rbtnMale){
                    sex = 1;
                }else{
                    Snackbar.make(btnFinish,
                            "성별을 입력해주세요.",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }

                SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();

                JSONObject requestData = new JSONObject();
                try {
                    requestData.put("name", name);
                    requestData.put("birth", birth);
                    requestData.put("sex", sex);
                    requestData.put("height", height);
                    requestData.put("weight", weight);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                showProgress();
                RequestQueue queue = Volley.newRequestQueue(UpdateProfileActivity.this);

                JsonObjectRequest request = new JsonObjectRequest(
                        Request.Method.POST,
                        Config.HOST + "/user/information",
                        requestData,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                dismissProgress();

                                try {
                                    String result = response.getString("result");
                                    if (result.equals("success")) {

                                        if(foodList == null || foodList.size() == 0) {

                                            Toast.makeText(UpdateProfileActivity.this,
                                                    "회원가입에 성공했습니다.",
                                                    Toast.LENGTH_SHORT).show();

                                            editor.putBoolean(Config.USER_INFORMATION, true);
                                            editor.apply();

                                            Intent intent = new Intent(UpdateProfileActivity.this, RobotActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            // 다른 액티비티 종료시키고 RobotActivity만 남도록 하는 코드.
                                            startActivity(intent);
                                            finish();
                                        } else {
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

                                            RequestQueue queue = Volley.newRequestQueue(UpdateProfileActivity.this);

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
                                                                    Toast.makeText(UpdateProfileActivity.this, "회원가입에 성공했습니다.", Toast.LENGTH_SHORT).show();
                                                                    editor.putBoolean(Config.USER_INFORMATION, true);
                                                                    editor.apply();

                                                                    Intent intent = new Intent(UpdateProfileActivity.this, RobotActivity.class);
                                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                    // 다른 액티비티 종료시키고 RobotActivity만 남도록 하는 코드.
                                                                    startActivity(intent);
                                                                    finish();
                                                                }

                                                            } catch (JSONException e) {
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
                                                    SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                                                    String accessToken = sp.getString(Config.ACCESS_TOKEN, "");
                                                    headers.put("Authorization", "Bearer " + accessToken);
                                                    // "Authorization" 헤더에 "Bearer"와 토큰 값을 넣습니다.
                                                    return headers;
                                                }
                                            };

                                            queue.add(request);

                                        }

                                    } else if (result.equals("fail")) {
                                        Snackbar.make(btnFinish,
                                                "회원가입에 실패하였습니다.",
                                                Snackbar.LENGTH_SHORT).show();
                                        Log.i("TEST", response.getString("error"));
                                        return;
                                    } else {
                                        Snackbar.make(btnFinish,
                                                "회원가입에 실패하였습니다.",
                                                Snackbar.LENGTH_SHORT).show();
                                        return;
                                    }
                                } catch (JSONException e) {

                                    Snackbar.make(btnFinish,
                                            "데이터 파싱 에러",
                                            Snackbar.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                dismissProgress();
                                Log.i("TEST", "에러가 발생했습니다.");
                                Log.i("TEST", error.toString());

                            }
                        }

                ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        String accessToken = sp.getString(Config.ACCESS_TOKEN, "");
                        headers.put("Authorization", "Bearer " + accessToken);
                        return headers;
                    }
                };
                queue.add(request);

            }
        });

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
    public boolean onSupportNavigateUp() { // 액션바 백버튼
        finish();
        return true;
    }

    public boolean canParseNumber(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private String formatDate (String dateString){
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            // 입력 형식으로 문자열을 Date 객체로 변환 시도
            java.util.Date date = inputFormat.parse(dateString);

            // 변환된 Date 객체를 원하는 출력 형식으로 포맷
            String formattedDate = outputFormat.format(date);
            return formattedDate;
        } catch (ParseException e) {
            return "error";
        }
    }

}