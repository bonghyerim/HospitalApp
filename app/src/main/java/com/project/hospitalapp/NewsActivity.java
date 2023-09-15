package com.project.hospitalapp;

import static android.content.ContentValues.TAG;

import static com.project.hospitalapp.LoginActivity.checkLogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.project.hospitalapp.api.NewsContentViewApi;
import com.project.hospitalapp.api.RetrofitClientInstance;
import com.project.hospitalapp.model.NewsContentResponse;
import com.project.hospitalapp.model.NewsItem;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsActivity extends AppCompatActivity {

    private ImageView imgNews;
    private TextView txtImgSummary, txtContent, txtTitle, txtDate;

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);


        // XML 레이아웃에서 뷰를 찾음
        imgNews = findViewById(R.id.imgNews);
        txtImgSummary = findViewById(R.id.txtImgSummary);
        txtContent = findViewById(R.id.txtContent);
        txtTitle = findViewById(R.id.txtTitle);
        txtDate = findViewById(R.id.txtDate);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // NewsContentViewApi를 사용하여 뉴스 컨텐츠 데이터를 가져옴
        String url = getIntent().getStringExtra("url");

        // NewsItem 객체를 가져옴
        NewsItem newsItem = (NewsItem) getIntent().getSerializableExtra("newsItem");

        // 이미지와 기타 정보를 표시
        if (newsItem != null) {
            getSupportActionBar().setTitle(newsItem.getTitle());
            loadNewsContent(newsItem, url);
        }

        // 뒤로 가기 버튼 표시
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                checkLogin(NewsActivity.this, itemId);

                return false;
            }
        });
    }

    private void loadNewsContent(NewsItem newsItem, String url) {
        // Retrofit을 사용하여 API 호출
        NewsContentViewApi api = RetrofitClientInstance.getRetrofitInstance().create(NewsContentViewApi.class);
        Call<NewsContentResponse> call = api.getNewsContent(url);

        call.enqueue(new Callback<NewsContentResponse>() {
            @Override
            public void onResponse(Call<NewsContentResponse> call, Response<NewsContentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    NewsContentResponse contentResponse = response.body();

                    // 뉴스 컨텐츠 데이터를 화면에 표시
                    txtImgSummary.setText(contentResponse.getImgSummary());
                    txtContent.setText(contentResponse.getContent().substring(0, contentResponse.getContent().length() - 1));
                    txtTitle.setText(newsItem.getTitle());
                    txtDate.setText(contentResponse.getDatetime());

                    // 이미지가 있는 경우에만 이미지 표시
                    if (!contentResponse.getImgSummary().isEmpty()) {
                        imgNews.setVisibility(View.VISIBLE);
                        Glide.with(NewsActivity.this)
                                .load(newsItem.getImg()) // 이미지 URL
                                .into(imgNews);

                        Log.d(TAG, "API 요청 결과: " + response.toString());
                    } else {
                        imgNews.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<NewsContentResponse> call, Throwable t) {
                // 네트워크 오류 또는 요청 실패 시 처리
                Log.e(TAG, "API 요청 실패: " + t.getMessage());
            }
        });
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(NewsActivity.this, MainActivity.class);
        intent.putExtra("itemId", 0);
        startActivity(intent);
        finish();
    }
}
