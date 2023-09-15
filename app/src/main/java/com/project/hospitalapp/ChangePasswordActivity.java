package com.project.hospitalapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ChangePasswordActivity extends AppCompatActivity {

    EditText editCheckNum1;
    EditText editCheckNum2;
    EditText editCheckNum3;
    EditText editCheckNum4;
    Button btnCheckEmail;
    EditText editPassword;
    EditText editCheckPassword;
    ImageView imgPassword;
    ImageView imgCheckPassword;
    Button btnFinish;
    TextView txtResend;

    Boolean checkPassword1 = false;
    Boolean checkPassword2 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // 뒤로 가기 버튼 표시
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(""); // 원하는 타이틀로 변경
        }

        editCheckNum1 = findViewById(R.id.editCheckNum1);
        editCheckNum2 = findViewById(R.id.editCheckNum2);
        editCheckNum3 = findViewById(R.id.editCheckNum3);
        editCheckNum4 = findViewById(R.id.editCheckNum4);
        btnCheckEmail = findViewById(R.id.btnCheckEmail);
        editPassword = findViewById(R.id.editPassword);
        editCheckPassword = findViewById(R.id.editCheckPassword);
        imgPassword = findViewById(R.id.imgPassword);
        imgCheckPassword = findViewById(R.id.imgCheckPassword);
        btnFinish = findViewById(R.id.btnFinish);
        txtResend = findViewById(R.id.txtResend);

        editCheckNum1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                editCheckNum2.requestFocus();
                editCheckNum2.setSelection(editCheckNum2.getText().length());
            }
        });

        editCheckNum2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                editCheckNum3.requestFocus();
                editCheckNum3.setSelection(editCheckNum3.getText().length());
            }
        });

        editCheckNum3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                editCheckNum4.requestFocus();
                editCheckNum4.setSelection(editCheckNum4.getText().length());
            }
        });

        editCheckNum4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // 키보드를 숨기는 코드 추가
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editCheckNum4.getWindowToken(), 0);
            }
        });

        txtResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                Toast.makeText(ChangePasswordActivity.this, "인증코드를 다시 전송했습니다.", Toast.LENGTH_SHORT).show();
            }
        });


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

        btnCheckEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 키보드를 숨기는 코드 추가
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                Toast.makeText(ChangePasswordActivity.this, "이메일 인증이 완료되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ChangePasswordActivity.this, "비밀번호 변경이 완료되었습니다.", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent();
                setResult(120, intent);
                finish();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() { // 액션바 백버튼
        finish();
        return true;
    }
}