package com.project.hospitalapp;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.project.hospitalapp.config.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    EditText editEmail;
    EditText editPassword;

    ImageView imgCheckEmail;
    ImageView imgCheckPassword;
    TextView txtChangePassword;
    TextView txtRegister;

    Button btnLogin;
    Button btnEasyLogin;

    Boolean checkPassword = false;
    String email;

    FirebaseAuth mAuth;

    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 뒤로 가기 버튼 표시
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(""); // 원하는 타이틀로 변경
        }

        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnEasyLogin = findViewById(R.id.btnEasyLogin);
        imgCheckEmail = findViewById(R.id.imgCheckEmail);
        imgCheckPassword = findViewById(R.id.imgCheckPassword);
        txtChangePassword = findViewById(R.id.txtChangePassword);
        txtRegister = findViewById(R.id.txtRegister);

        editEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                email = editEmail.getText().toString().trim();
                Pattern pattern = Patterns.EMAIL_ADDRESS;

                if(!email.isEmpty()){
                    imgCheckEmail.setVisibility(ImageView.VISIBLE);

                    if(pattern.matcher(email).matches() == true) {
                        imgCheckEmail.setImageResource(R.drawable.baseline_check_24);
                    } else {
                        imgCheckEmail.setImageResource(R.drawable.baseline_close_24);
                    }

                } else {
                    imgCheckEmail.setVisibility(ImageView.GONE);
                }
            }
        });


        imgCheckPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!checkPassword) {
                    editPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    imgCheckPassword.setImageResource(R.drawable.eye);
                    checkPassword = true;
                } else {
                    editPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    imgCheckPassword.setImageResource(R.drawable.eyeon);
                    checkPassword = false;
                }
            }
        });


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Pattern pattern = Patterns.EMAIL_ADDRESS;
                if (email == null || pattern.matcher(email).matches() == false){
                    Snackbar.make(btnLogin,
                            "이메일 형식을 확인하세요.",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }

                String password = editPassword.getText().toString().trim();

                if(password.length() < 6 || password.length() > 12){
                    Snackbar.make(btnLogin,
                            "비밀번호는 6 ~ 12글자 내로 입력해주세요.",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }

                showProgress();

                // FirebaseAuth 인스턴스 가져오기
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

                // Firebase 로그인 메서드 호출
                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                dismissProgress();
                                // 로그인이 성공한 경우
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                String userId = user.getUid(); // 사용자의 userId 가져오기

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

                                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);

                                JsonObjectRequest request = new JsonObjectRequest(
                                        Request.Method.POST,
                                        Config.HOST + "/user/login",
                                        requestData,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {

                                                try {
                                                    String result = response.getString("result");

                                                    if (result.equals("success")) {
                                                        String accessToken = response.getString("access_token");
                                                        Boolean userInformation = response.getBoolean("user_information");
                                                        editor.putString(Config.ACCESS_TOKEN, accessToken);
                                                        editor.putBoolean(Config.USER_INFORMATION, userInformation);

                                                        editor.apply();

                                                        dismissProgress();

                                                        Class runClass;
                                                        String message;
                                                        if(userInformation) {
                                                            runClass = RobotActivity.class;
                                                            message = "로그인 성공!";
                                                        } else {
                                                            runClass = UpdateProfileActivity.class;
                                                            message = "세부정보를 입력해주세요.";
                                                        }

                                                        Intent intent = new Intent(LoginActivity.this, runClass);

                                                        Toast.makeText(LoginActivity.this,
                                                                message,
                                                                Toast.LENGTH_SHORT).show();

                                                        startActivity(intent);
                                                        finish();
                                                    } else if (result.equals("fail")) {

                                                        Toast.makeText(LoginActivity.this,
                                                                "로그인에 실패하였습니다.",
                                                                Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Log.e("TEST", "로그인 에러");
                                                    }


                                                } catch (JSONException e) {
                                                    dismissProgress();
                                                    Snackbar.make(btnLogin,
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

                                            }
                                        }
                                );
                                showProgress();
                                queue.add(request);

                            } else {
                                // 로그인이 실패한 경우
                                dismissProgress();
                                Exception exception = task.getException();
                                // 실패 원인에 대한 예외 처리를 수행할 수 있습니다.
                                // 예: 이메일 형식 오류, 비밀번호가 일치하지 않음 등
                                // ...

                                Snackbar.make(btnLogin,
                                        "회원가입 정보가 없거나 비밀번호가 다릅니다.",
                                        Snackbar.LENGTH_SHORT).show();

                                Log.i("ERROR", exception.toString());
                            }
                        });
            }
        });


        // Initialize Firebase Authentication in your onCreate method
        mAuth = FirebaseAuth.getInstance();

        btnEasyLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseApp.initializeApp(LoginActivity.this);

                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

                // Create a GoogleSignInClient
                GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(LoginActivity.this, gso);

                // Start Google Sign-In
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        txtChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LoginActivity.this, CheckEmailActivity.class);
                startActivity(intent);
            }
        });

    }

    // 회원정보가 필요한 액티비티로 이동할 때 로그인여부 확인
    public static void checkLogin(Context context, Class runClass) {
        SharedPreferences sp = context.getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String accessToken = sp.getString(Config.ACCESS_TOKEN, "");
        Boolean userInformation = sp.getBoolean(Config.USER_INFORMATION, false);
//        Activity activity = (Activity) context;
        Intent intent;
        String message;
        if(accessToken.isEmpty()){
            intent = new Intent(context, LoginActivity.class);
            message = "로그인이";

        } else if(!userInformation){
            intent = new Intent(context, UpdateProfileActivity.class);
            message = "세부정보 입력이";
    
        } else {
            intent = new Intent(context, runClass);
            context.startActivity(intent);
//            activity.finish();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(message + " 필요한 서비스입니다.");
        builder.setMessage(message.substring(0, message.length() - 1) + "창으로 이동하시겠습니까?");
        // 이 다이얼로그의 외각부분을 눌렀을 때 사라지지 않도록 하는 코드
        builder.setCancelable(false);

        builder.setPositiveButton("이동", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                context.startActivity(intent);
            }
        });
        builder.setNegativeButton("취소", null);

        builder.show();
    }

    // 회원정보가 필요한 fragment 메뉴를 클릭했을 때 로그인여부 확인
    public static void checkLogin(Context context, int itemId){
        SharedPreferences sp = context.getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String accessToken = sp.getString(Config.ACCESS_TOKEN, "");
        Boolean userInformation = sp.getBoolean(Config.USER_INFORMATION, false);
        Activity activity = (Activity) context;
        Intent intent;
        String message;
        if((itemId == R.id.chatFragment || itemId == R.id.myHealthFragment) && accessToken.isEmpty()){
            intent = new Intent(context, LoginActivity.class);
            message = "로그인이";

        } else if((itemId == R.id.chatFragment || itemId == R.id.myHealthFragment) && !userInformation){
            intent = new Intent(context, UpdateProfileActivity.class);
            message = "세부정보 입력이";

        } else if(context instanceof MainActivity) {
            // 메인액티비티의 프레그먼트 이동시키기
            ((MainActivity) context).loadFragment(((MainActivity) context).runFragment());
            return;

        }else {
            intent = new Intent(context, MainActivity.class);
            intent.putExtra("itemId", itemId);
            context.startActivity(intent);
            activity.finish();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(message + " 필요한 서비스입니다.");
        builder.setMessage(message.substring(0, message.length() - 1) + "창으로 이동하시겠습니까?");
        // 이 다이얼로그의 외각부분을 눌렀을 때 사라지지 않도록 하는 코드
        builder.setCancelable(false);

        builder.setPositiveButton("이동", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                context.startActivity(intent);
            }
        });
        builder.setNegativeButton("취소", null);

        builder.show();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                }
            } catch (ApiException e) {
                // Handle sign-in failure
                Log.w("TEST", "Google sign-in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign-in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // Sign-in failure
                        Log.w("TEST", "signInWithCredential:failure", task.getException());
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // 사용자가 로그인한 경우, 로그인한 사용자 정보를 표시하거나 다른 작업을 수행합니다.
            // 예를 들어, 환영 메시지를 표시하거나 로그인 후 작업을 수행할 수 있습니다.
//            String displayName = user.getDisplayName(); // 구글계정 이름.
            String email = user.getEmail();
            String userId = user.getUid();

            // 사용자 정보를 UI에 표시하거나 다른 작업 수행

            // 예제: 환영 메시지를 표시하는 TextView를 업데이트

            // todo: db 로그인
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

            RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    Config.HOST + "/user/login/easy",
                    requestData,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                String result = response.getString("result");

                                if (result.equals("success")) {
                                    String accessToken = response.getString("access_token");
                                    Boolean userInformation = response.getBoolean("user_information");
                                    editor.putString(Config.ACCESS_TOKEN, accessToken);
                                    editor.putBoolean(Config.USER_INFORMATION, userInformation);

                                    editor.apply();

                                    dismissProgress();

                                    Class runClass;
                                    String message;
                                    if(userInformation) {
                                        runClass = RobotActivity.class;
                                        message = "로그인 성공!";
                                    } else {
                                        runClass = UpdateProfileActivity.class;
                                        message = "세부정보를 입력해주세요.";
                                    }

                                    Intent intent = new Intent(LoginActivity.this, runClass);

                                    Toast.makeText(LoginActivity.this,
                                            message,
                                            Toast.LENGTH_SHORT).show();

                                    startActivity(intent);
                                    finish();
                                } else if (result.equals("fail")) {

                                    Toast.makeText(LoginActivity.this,
                                            "로그인에 실패하였습니다.",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.e("TEST", "로그인 에러");
                                }

                            } catch (JSONException e) {
                                dismissProgress();
                                Snackbar.make(btnLogin,
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

                        }
                    }
            );
            showProgress();
            queue.add(request);

        } else {
            // 사용자가 로그아웃한 경우 또는 로그인에 실패한 경우,
            // UI를 초기 상태로 돌리거나 오류 메시지를 표시할 수 있습니다.
            // 예를 들어, 로그인 버튼을 다시 활성화하거나 오류 메시지를 표시할 수 있습니다.

            // 예제: 오류 메시지를 표시하는 TextView를 업데이트
            Toast.makeText(LoginActivity.this, "로그인에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
        }
    }
}