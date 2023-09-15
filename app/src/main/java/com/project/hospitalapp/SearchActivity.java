package com.project.hospitalapp;

import static com.project.hospitalapp.LoginActivity.checkLogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;
import com.project.hospitalapp.adapter.PlaceAdapter;
import com.project.hospitalapp.api.NetworkClient;
import com.project.hospitalapp.api.PlaceApi;
import com.project.hospitalapp.config.Config;
import com.project.hospitalapp.model.Place;
import com.project.hospitalapp.model.PlaceList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SearchActivity extends AppCompatActivity {

    EditText editKeyword;
    ImageView imgSearch;
    ProgressBar progressBar;

    RecyclerView recyclerView;

    boolean delay;

    PlaceAdapter adapter;
    ArrayList<Place> placeArrayList = new ArrayList<>();

    // 내 위치 가져오기 위한 멤버변수
    LocationManager locationManager;
    LocationListener locationListener;

    double lat;
    double lng;

    int radius = 2000;  // 미터 단위

    String keyword;

    boolean isLocationReady;

    String pageToken;

    BottomNavigationView bottomNavigationView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // 뒤로 가기 버튼 표시
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("병원/약국 검색"); // 원하는 타이틀로 변경
        }
        
        // 화면구성연결
        editKeyword = findViewById(R.id.editKeyword);
        imgSearch = findViewById(R.id.imgSearch);
        progressBar = findViewById(R.id.progressBar);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        keyword = getIntent().getStringExtra("keyword");
        editKeyword.setText(keyword);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                checkLogin(SearchActivity.this, itemId);

                return false;
            }
        });


        // 리사이클러뷰에 스크롤 단다.
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // 맨 마지막 데이터가 화면에 나타나게 되면,
                // 네트워크를 통해서, 추가로 데이터를 받아오게 한다.
                int lastPosition = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int totalCount = recyclerView.getAdapter().getItemCount();

                if(lastPosition + 1 == totalCount){

                    if(pageToken != null){
                        addNetworkData();

                    }


                }

            }
        });

        // 폰의 위치를 가져오기 위해서는, 시스템서비스로부터 로케이션 매니져들을
        // 받아온다.
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // 로케이션 리스트를 만든다.
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {


                lat = location.getLatitude();
                lng = location.getLongitude();

                // lat,lng위치값을 다 가져온 후에

                isLocationReady = true;

            }
        };

        if(ActivityCompat.checkSelfPermission(SearchActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(SearchActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    100);
            return;
        }

        // 위치기반 허용하였으므로,
        // 로케이션 매지너에, 리스너를 연결한다.
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                3000,
                -1,
                locationListener);

        // todo: 권한을 확인해야 할 때 고려하여 수정하기.
        //   권한이 수락되어 있을 때 실행 and 권한을 수락했을 때 실행

        // todo: 페이징 했을 때 스크롤이 움직이는 것 수정하기.

        startDelay();
        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 키보드를 숨기는 코드 추가
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                if(isLocationReady == false){
                    Snackbar.make(imgSearch,
                            "아직 위치를 잡지 못했습니다. 잠시후 다시 검색하세요",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }

                keyword = editKeyword.getText().toString().trim();


                if(keyword.isEmpty()){
                    return;
                }

                getNetworkData();


            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.icon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.menuAlarm){
            checkLogin(SearchActivity.this, AlarmActivity.class);

        } else if(itemId == R.id.menuInfo){
            checkLogin(SearchActivity.this, MyInformationActivity.class);

        }
        return super.onOptionsItemSelected(item);
    }

    private void addNetworkData() {

        progressBar.setVisibility(View.VISIBLE);

        Retrofit retrofit = NetworkClient.getRetrofitClient(SearchActivity.this, Config.GOOGLE_MAP_HOST);
        PlaceApi api = retrofit.create(PlaceApi.class);

        Call<PlaceList> call = api.getPlaceListPage("ko",
                lat+","+lng,
                radius,
                Config.GOOGLE_API_KEY,
                keyword,
                pageToken);

        call.enqueue(new Callback<PlaceList>() {
            @Override
            public void onResponse(Call<PlaceList> call, Response<PlaceList> response) {

                progressBar.setVisibility(View.GONE);

                if(response.isSuccessful()){

                    PlaceList placeList = response.body();

                    pageToken = placeList.next_page_token;

                    placeArrayList.addAll(placeList.results );
                    calculateDistances(); // 추가: 거리 계산 및 저장

                    adapter.notifyDataSetChanged();



                }else{

                }

            }

            @Override
            public void onFailure(Call<PlaceList> call, Throwable t) {

                progressBar.setVisibility(View.GONE);

            }
        });

    }

    private void getNetworkData() {
        Log.i("TEST", "getNetworkData");

        progressBar.setVisibility(View.VISIBLE);

        placeArrayList.clear();

        Retrofit retrofit = NetworkClient.getRetrofitClient(SearchActivity.this, Config.GOOGLE_MAP_HOST);
        PlaceApi api = retrofit.create(PlaceApi.class);

        Call<PlaceList> call = api.getPlaceList("ko",
                lat+","+lng,
                radius,
                Config.GOOGLE_API_KEY,
                keyword);


        call.enqueue(new Callback<PlaceList>() {
            @Override
            public void onResponse(Call<PlaceList> call, Response<PlaceList> response) {

                progressBar.setVisibility(View.GONE);

                if(response.isSuccessful()){

                    PlaceList placeList = response.body();

                    pageToken = placeList.next_page_token;

                    // 장소 검색 결과를 받아온 후, 거리 계산 코드를 실행
                    placeArrayList.addAll(placeList.results);
                    calculateDistances(); // 추가: 거리 계산 및 저장


                    adapter = new PlaceAdapter(SearchActivity.this, placeArrayList);
                    recyclerView.setAdapter(adapter);

                }else{
                    Log.i("TEST", "서버에러?");

                }


            }

            @Override
            public void onFailure(Call<PlaceList> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.i("TEST", "데이터 가공 에러");

            }
        });

    }

    @Override

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){

            if( ActivityCompat.checkSelfPermission(SearchActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED ){

                ActivityCompat.requestPermissions(SearchActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION} ,
                        100);
                return;

            }
            // 위치기반 허용하였으므로,
            // 로케이션 매니저에, 리스너를 연결한다. 그러면 동작한다.
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    3000,
                    -1,
                    locationListener);

        }

    }

    private void calculateDistances() {
        Location userLocation = new Location("");
        userLocation.setLatitude(lat);
        userLocation.setLongitude(lng);

        for (Place place : placeArrayList) {
            Place.Geometry geometry = place.geometry;
            if (geometry != null && geometry.location != null) {
                Location placeLocation = new Location("");
                placeLocation.setLatitude(geometry.location.lat);
                placeLocation.setLongitude(geometry.location.lng);

                // 두 지점 간의 거리 계산
                float distance = userLocation.distanceTo(placeLocation);
                place.setDistance(distance); // Place 객체에 거리 정보 저장
            }
        }

        // 거리를 기준으로 placeArrayList를 정렬
        Collections.sort(placeArrayList, new Comparator<Place>() {
            @Override
            public int compare(Place place1, Place place2) {
                return Float.compare(place1.getDistance(), place2.getDistance());
            }
        });

    }


    private void startDelay(){
        progressBar.setVisibility(View.VISIBLE);
        delay = true;
        CountDownTimer timer = new CountDownTimer(7000, 500) {
            // 0.5초씩 감소
            @Override
            public void onTick(long l) {

                if(delay == false) {
                    return;
                }

                if(isLocationReady == true){
                    getNetworkData();
                    delay = false;
                }
            }

            @Override
            public void onFinish() {

                if(delay == false) {
                    return;
                }

                progressBar.setVisibility(View.GONE);
                Toast.makeText(SearchActivity.this,
                        "위치를 찾을 수 없습니다. 잠시후 다시 검색하세요",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        };

        timer.start();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(SearchActivity.this, MainActivity.class);
        intent.putExtra("itemId", 0);
        startActivity(intent);
        finish();
    }
}