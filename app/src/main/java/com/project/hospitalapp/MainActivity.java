package com.project.hospitalapp;

import static com.project.hospitalapp.LoginActivity.checkLogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

//    ImageView introImg;

    BottomNavigationView bottomNavigationView;
    Fragment mainFragment;
    Fragment mapFragment;
    Fragment chatFragment;
    Fragment drugFragment;
    Fragment myHealthFragment;

//    FragmentContainerView fragment;

    int itemId;

    private boolean doubleBackToExitPressedOnce = false;
    private static final int DOUBLE_CLICK_EXIT_DELAY = 2000; // 뒤로가기 두번 눌렀을 때 종료되는 시간간격(밀리초)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
//        introImg = (ImageView) findViewById(R.id.introImg);
//        fragment = findViewById(R.id.fragment);

//        startIntro();

        // 커스텀 뒤로가기 버튼 사용하기.
        Drawable customBackButton = getResources().getDrawable(R.drawable.baseline_arrow_back_24);
        getSupportActionBar().setHomeAsUpIndicator(customBackButton);

        mainFragment = new MainFragment();
        mapFragment = new MapFragment();
        chatFragment = new ChatFragment();
        drugFragment = new DrugFragment();
        myHealthFragment = new MyHealthFragment();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                itemId = item.getItemId();


                checkLogin(MainActivity.this, itemId);
                return true;
            }
        });


    }
//
//    private void startIntro(){
//
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); // 상태바
//        getSupportActionBar().hide();
//
//        fragment.setVisibility(View.GONE);
//        bottomNavigationView.setVisibility(View.GONE);
//        introImg.setVisibility(View.VISIBLE);
//        Glide.with(MainActivity.this).load(R.drawable.intro_img).into(introImg);
//
//        CountDownTimer timer = new CountDownTimer(5000, 1000) {
//            // 1초씩 감소
//            @Override
//            public void onTick(long l) {
//            }
//
//            @Override
//            public void onFinish() {
//
//                fragment.setVisibility(View.VISIBLE);
//                bottomNavigationView.setVisibility(View.VISIBLE);
//                introImg.setVisibility(View.GONE);
//                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//
//                getSupportActionBar().show();
//
//            }
//        };
//
//        timer.start();
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.icon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.menuAlarm){
            checkLogin(MainActivity.this, AlarmActivity.class);

        } else if(itemId == R.id.menuInfo){
            checkLogin(MainActivity.this, MyInformationActivity.class);

        }
        return super.onOptionsItemSelected(item);
    }

    boolean loadFragment(Fragment fragment){
        if(fragment != null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment, fragment)
                    .commit();
            return true;
        } else {
            return false;
        }
    }

    public Fragment runFragment(){
        Fragment fragment = null;

        if (itemId == R.id.mainFragment){
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if(itemId == R.id.mainFragment){
            fragment = mainFragment;
            getSupportActionBar().setTitle("My Doctor");

        } else if (itemId == R.id.mapFragment) {
            fragment = mapFragment;
            getSupportActionBar().setTitle("지도 검색");

        } else if (itemId == R.id.chatFragment) {
            fragment = chatFragment;
            getSupportActionBar().setTitle("AI 챗 상담");

        } else if (itemId == R.id.drugFragment) {
            fragment = drugFragment;
            getSupportActionBar().setTitle("약 정보 찾기");

        }else if (itemId == R.id.myHealthFragment) {
            fragment = myHealthFragment;
            getSupportActionBar().setTitle("내 관리");

        }
        return fragment;
    }


    @Override
    protected void onResume() {
        super.onResume();

        itemId = getIntent().getIntExtra("itemId", 0);
        if (itemId != 0) {
            loadFragment(runFragment());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {

        if (itemId != R.id.mainFragment) {
            itemId = R.id.mainFragment;
            loadFragment(runFragment());
        } else {

            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, DOUBLE_CLICK_EXIT_DELAY);
        }

    }
}