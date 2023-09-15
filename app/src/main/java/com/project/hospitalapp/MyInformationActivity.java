package com.project.hospitalapp;

import static com.project.hospitalapp.LoginActivity.checkLogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.project.hospitalapp.config.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class MyInformationActivity extends AppCompatActivity {

    TextView txtEmail;
    EditText editBirth;
    EditText editName;
    EditText editHeight;
    EditText editWeight;
    TextView txtBmi;
    ImageView imgCheckEmail;
    ImageView imgCheckName;
    Button btnSave;
    TextView txtLogout;
    TextView txtUpdate;

    String email;
    String name;
    String accessToken;

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_information);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("내 정보"); // 원하는 타이틀로 변경
        }

        // 커스텀 뒤로가기 버튼 사용하기.
        Drawable customBackButton = getResources().getDrawable(R.drawable.baseline_arrow_back_24);
        getSupportActionBar().setHomeAsUpIndicator(customBackButton);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);

        txtEmail = findViewById(R.id.txtEmail);
        editBirth = findViewById(R.id.editBirth);
        editName = findViewById(R.id.editName);
        editHeight = findViewById(R.id.editHeight);
        editWeight = findViewById(R.id.editWeight);
        txtBmi = findViewById(R.id.txtBmi);
        imgCheckEmail = findViewById(R.id.imgCheckEmail);
        imgCheckName = findViewById(R.id.imgCheckName);
        btnSave = findViewById(R.id.btnSave);
        txtLogout = findViewById(R.id.txtLogout);
        txtUpdate = findViewById(R.id.txtUpdate);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                checkLogin(MyInformationActivity.this, itemId);

                return false;
            }
        });

        accessToken = sp.getString(Config.ACCESS_TOKEN, "");

        showProgress();
        RequestQueue queue = Volley.newRequestQueue(MyInformationActivity.this);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                Config.HOST + "/user/information",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            if (response.getString("result").equals("success")) {
                                JSONObject result =  response.getJSONObject("information");
                                txtEmail.setText(result.getString("email"));
                                editName.setText(result.getString("name"));
                                String birth = result.getString("birth");
                                birth = birth.replace("-", "");
                                editBirth.setText(birth);
                                String height = result.getString("height");
                                editHeight.setText(height);
                                String weight = result.getString("weight");
                                editWeight.setText(weight);
                                txtUpdate.setText("최근 업데이트: " + result.getString("updatedAt").replace("T", " / "));

                                dismissProgress();
                            }
                            else {
                                dismissProgress();
                                Log.e("TEST", "결과를 받아올 수 없습니다.");
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
                        dismissProgress();
                        Log.i("TEST", "에러가 발생했습니다.");
                        Log.i("TEST", error.toString());

                    }
                }

        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                Log.i("TEST", accessToken);
                return headers;
            }
        };
        queue.add(request);


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
                if(!name.isEmpty()){
                    imgCheckName.setVisibility(ImageView.VISIBLE);

                    if(name.length() > 2 && name.length() < 13) {
                        imgCheckName.setImageResource(R.drawable.baseline_check_24);
                    } else {
                        imgCheckName.setImageResource(R.drawable.baseline_close_24);
                    }

                } else {
                    imgCheckName.setVisibility(ImageView.GONE);
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {

            SharedPreferences.Editor editor = sp.edit();

            @Override
            public void onClick(View view) {

                if(name.length() < 3 || name.length() > 12){
                    Snackbar.make(btnSave,
                            "닉네임 길이를 확인해주세요.",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }

                String birth = editBirth.getText().toString().trim();

                if(birth.length() != 8 || !canParseNumber(birth)){
                    Snackbar.make(btnSave,
                            "생년월일을 확인해주세요.",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }
                birth = formatDate(birth);

                if(birth.equals("error")){
                    Snackbar.make(btnSave,
                            "생년월일을 확인해주세요.",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }

                String strHeight = editHeight.getText().toString().trim();

                if(strHeight == null || strHeight.isEmpty() || !canParseNumber(strHeight)){
                    Snackbar.make(btnSave,
                            "키를 확인해주세요.",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }

                double height = Double.parseDouble(strHeight);

                if(height > 250 || height < 30){
                    Snackbar.make(btnSave,
                            "키를 확인해주세요.",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }

                String strWeight = editWeight.getText().toString().trim();

                if(strWeight == null || strWeight.isEmpty() || !canParseNumber(strWeight)){
                    Snackbar.make(btnSave,
                            "몸무게를 확인해주세요.",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }

                double weight = Double.parseDouble(strWeight);

                if(weight > 250 || weight < 2){
                    Snackbar.make(btnSave,
                            "몸무게를 확인해주세요.",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }


                JSONObject requestData = new JSONObject();
                int sex = sp.getInt("sex", 0);
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
                RequestQueue queue = Volley.newRequestQueue(MyInformationActivity.this);

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

                                        Toast.makeText(MyInformationActivity.this,
                                                "회원정보가 수정되었습니다.",
                                                Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(MyInformationActivity.this, MainActivity.class);
                                        intent.putExtra("itemId", 0);
                                        startActivity(intent);

                                    } else {
                                        Snackbar.make(btnSave,
                                                "회원가입에 실패하였습니다.",
                                                Snackbar.LENGTH_SHORT).show();
                                        return;
                                    }
                                } catch (JSONException e) {

                                    Snackbar.make(btnSave,
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

        txtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                logoutDialLog(sp);
            }
        });

        editHeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String strHeight = editHeight.getText().toString().trim();
                String strWeight = editWeight.getText().toString().trim();

                if (!strHeight.isEmpty() && !strWeight.isEmpty()) {
                    double height = Double.parseDouble(strHeight) / 100;
                    double weight = Double.parseDouble(strWeight);
                    txtBmi.setText(weight / height / height + ""); // 몸무게 나누기 키 제곱
                }
            }
        });

        editWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String strHeight = editHeight.getText().toString().trim();
                String strWeight = editWeight.getText().toString().trim();

                if (!strHeight.isEmpty() && !strWeight.isEmpty()) {
                    double height = Double.parseDouble(strHeight) / 100;
                    double weight = Double.parseDouble(strWeight);
                    txtBmi.setText(weight / height / height + ""); // 몸무게 나누기 키 제곱
                }
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

    private void logoutDialLog(SharedPreferences sp){
        AlertDialog.Builder builder = new AlertDialog.Builder(MyInformationActivity.this);
        builder.setTitle("로그아웃");
        builder.setMessage("로그아웃 하시겠습니까?");
        // 이 다이얼로그의 외각부분을 눌렀을 때 사라지지 않도록 하는 코드
        builder.setCancelable(false);

        builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showProgress();
                RequestQueue queue = Volley.newRequestQueue(MyInformationActivity.this);

                JsonObjectRequest request = new JsonObjectRequest(
                        Request.Method.DELETE,
                        Config.HOST + "/user/logout",
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                try {
                                    if (response.getString("result").equals("success")) {

                                        SharedPreferences.Editor editor = sp.edit();

                                        editor.putString(Config.USER_EMAIL, "");
                                        editor.putString(Config.USER_UID, "");
                                        editor.putString(Config.ACCESS_TOKEN, "");
                                        editor.putBoolean(Config.USER_INFORMATION, false);
                                        editor.apply();

                                        Toast.makeText(MyInformationActivity.this,
                                                "로그아웃 되었습니다.",
                                                Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(MyInformationActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        dismissProgress();
                                        finish();
                                    }
                                    else {
                                        dismissProgress();
                                        Log.e("TEST", "결과를 받아올 수 없습니다.");
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
                                dismissProgress();
                                Log.i("TEST", "에러가 발생했습니다.");
                                Log.i("TEST", error.toString());

                            }
                        }

                ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Authorization", "Bearer " + accessToken);
                        Log.i("TEST", accessToken);
                        return headers;
                    }
                };
                queue.add(request);
            }
        });
        builder.setNegativeButton("아니요", null);

        builder.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(MyInformationActivity.this, MainActivity.class);
        intent.putExtra("itemId", 0);
        startActivity(intent);
        finish();
    }
}