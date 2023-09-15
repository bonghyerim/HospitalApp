package com.project.hospitalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class RobotActivity extends AppCompatActivity {

    ImageView imgChat;
    ImageView imgMain;

    LinearLayout linearLayoutMain;
    LinearLayout linearLayoutChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot);

        getSupportActionBar().hide();

        linearLayoutMain = findViewById(R.id.linearLayoutMain);
        linearLayoutChat = findViewById(R.id.linearLayoutChat);

        linearLayoutChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RobotActivity.this, MainActivity.class);
                intent.putExtra("itemId", R.id.chatFragment);
                startActivity(intent);
                finish();
            }
        });

        linearLayoutMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RobotActivity.this, MainActivity.class);
                intent.putExtra("itemId", R.id.mainFragment);

                startActivity(intent);
                finish();
            }
        });
    }
}