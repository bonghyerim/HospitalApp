package com.project.hospitalapp;

import static com.project.hospitalapp.FoodAllergyActivity.RESULT_FOOD_SAVE;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class CheckEmailActivity extends AppCompatActivity {

    EditText editEmail;

    Button btnNext;

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if(result.getResultCode() == 120){
                        finish();

                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_email);

        // 뒤로 가기 버튼 표시
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(""); // 원하는 타이틀로 변경
        }

        editEmail = findViewById(R.id.editEmail);
        btnNext = findViewById(R.id.btnNext);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = editEmail.getText().toString().trim();
                Pattern pattern = Patterns.EMAIL_ADDRESS;
                if(email == null || pattern.matcher(email).matches() == false){
                    Snackbar.make(btnNext,
                            "이메일 형식을 확인하세요.",
                            Snackbar.LENGTH_SHORT).show();

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    return;
                }

                Toast.makeText(CheckEmailActivity.this, "인증코드가 전송되었습니다.", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(CheckEmailActivity.this, ChangePasswordActivity.class);
                launcher.launch(intent);

            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() { // 액션바 백버튼
        finish();
        return true;
    }
}