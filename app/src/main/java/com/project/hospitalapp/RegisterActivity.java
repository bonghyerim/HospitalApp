package com.project.hospitalapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.hospitalapp.config.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    EditText editEmail;
    EditText editPassword;
    EditText editCheckPassword;
    Button btnRegister;

    ImageView imgPassword;
    ImageView imgCheckPassword;

    Boolean checkPassword1 = false;
    Boolean checkPassword2 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(""); // 원하는 타이틀로 변경
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 키
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);

        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        editCheckPassword = findViewById(R.id.editCheckPassword);
        btnRegister = findViewById(R.id.btnRegister);

        imgPassword = findViewById(R.id.imgPassword);
        imgCheckPassword = findViewById(R.id.imgCheckPassword);

        // todo: 회원가입 이동하기 전 알러트다이얼로그 띄우기.

        // todo: editText와 연관되는 ImageView 넣기.

        imgPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!checkPassword1) {
                    editPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    imgPassword.setImageResource(R.drawable.eye);
                    checkPassword1 = true;
                } else {
                    editPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    imgPassword.setImageResource(R.drawable.eyeon);
                    checkPassword1 = false;
                }
            }
        });

        imgCheckPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!checkPassword2) {
                    editCheckPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    imgCheckPassword.setImageResource(R.drawable.eye);
                    checkPassword2 = true;
                } else {
                    editCheckPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    imgCheckPassword.setImageResource(R.drawable.eyeon);
                    checkPassword2 = false;
                }
            }
        });



        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = editEmail.getText().toString().trim();
                Pattern pattern = Patterns.EMAIL_ADDRESS;
                if(email == null || pattern.matcher(email).matches() == false){
                    Snackbar.make(btnRegister,
                            "이메일 형식을 확인하세요.",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }

                String password = editPassword.getText().toString().trim();
                if(password.length() < 6 || password.length() > 12){
                    Snackbar.make(btnRegister,
                            "비밀번호는 6 ~ 12글자 내로 입력해주세요.",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }

                String checkPassword = editCheckPassword.getText().toString().trim();
                if(!password.equals(checkPassword)){
                    Snackbar.make(btnRegister,
                            "비밀번호확인이 다릅니다.", // todo: 수정하기.
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }

                showProgress();

                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

                // Firebase 회원가입 메서드 호출
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // 회원가입이 성공한 경우
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                String userId = user.getUid();

                                SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();

                                editor.putString(Config.USER_EMAIL, email);
                                editor.putString(Config.USER_UID, userId);

                                JSONObject requestData = new JSONObject();
                                try {
                                    requestData.put("email", email);
                                    requestData.put("password", userId);
                                } catch (JSONException e) {
                                    Log.i("TEST", e.toString());
                                }


                                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);

                                JsonObjectRequest request = new JsonObjectRequest(
                                        Request.Method.POST,
                                        Config.HOST + "/user/register",
                                        requestData,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {

                                                try {
                                                    String result = response.getString("result");

                                                    if (result.equals("success")) {
                                                        String accessToken = response.getString("access_token");
                                                        editor.putString(Config.ACCESS_TOKEN, accessToken);
                                                        editor.apply();
                                                        Log.i("TEST1", accessToken);

                                                        dismissProgress();

                                                        Intent intent = new Intent(RegisterActivity.this, UpdateProfileActivity.class);

                                                        startActivity(intent);
                                                        finish();
                                                    } else {// todo: fail일 때 처리하기.
//                                                        Log.i("TEST", "result: fail");
//                                                        String error = response.getString("error");
//                                                        Log.i("TEST", error);
                                                        // todo: 추후에 에러코드를 확실하게 만들어서 어떤 이유에서 회원가입이 실패한지 나오도록.
                                                        Snackbar.make(btnRegister, "이미 회원정보가 있는 이메일입니다.", Snackbar.LENGTH_SHORT).show();
                                                    }

                                                } catch (JSONException e) {
                                                    dismissProgress();
                                                    Snackbar.make(btnRegister,
                                                            "데이터 파싱 에러",
                                                            Snackbar.LENGTH_SHORT).show();
                                                    Log.i("TEST", "파싱 에러");
                                                    Log.i("TEST", e.toString());
                                                    return;
                                                }
                                                dismissProgress();

                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                dismissProgress();
                                                Log.i("TEST", "서버에러");
                                                Log.i("TEST", error.toString());

                                            }
                                        }
                                );

                                queue.add(request);

                            } else {
                                dismissProgress();

                                // 회원가입이 실패한 경우
                                Exception exception = task.getException();
                                // 실패 원인에 대한 예외 처리를 수행할 수 있습니다.
                                // 예: 이메일 형식 오류, 이미 가입된 이메일 등
                                // ...

                                Log.i("ERROR", exception.toString());

                            }
                        });
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
}