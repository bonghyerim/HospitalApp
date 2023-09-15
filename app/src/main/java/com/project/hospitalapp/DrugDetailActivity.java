package com.project.hospitalapp;

import static com.project.hospitalapp.LoginActivity.checkLogin;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.project.hospitalapp.model.Drug;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DrugDetailActivity extends AppCompatActivity {

    TextView entpName, itemName, itemSeq, efcyQesitm, useMethodQesitm, atpnWarnQesitm, atpnQesitm, depositMethodQesitm, intrcQesitm;

    ImageView itemImage;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug_detail);


        entpName = findViewById(R.id.entpName);
        itemName = findViewById(R.id.itemName);
        itemSeq = findViewById(R.id.itemSeq);
        efcyQesitm = findViewById(R.id.efcyQesitm);
        useMethodQesitm = findViewById(R.id.useMethodQesitm);
        atpnWarnQesitm = findViewById(R.id.atpnWarnQesitm);
        atpnQesitm = findViewById(R.id.atpnQesitm);
        depositMethodQesitm = findViewById(R.id.depositMethodQesitm);
        itemImage = findViewById(R.id.itemImage);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        intrcQesitm = findViewById(R.id.intrcQesitm);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);


//        themes.xml 에서 아래 두개 지우면 위 소스 실행 됨.
//        <item name="windowActionBar">false</item>
//        <item name="windowNoTitle">true</item>

        getSupportActionBar().setTitle(Drug.itemNameText);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                checkLogin(DrugDetailActivity.this, itemId);

                return false;
            }
        });

        RequestQueue queue = Volley.newRequestQueue(DrugDetailActivity.this);
        String searchKeyword = getIntent().getStringExtra("searchKeyword");
        String apiUrl = "https://ajjjrg32tj.execute-api.ap-northeast-2.amazonaws.com/medicine/search?keyword=" + searchKeyword;
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                apiUrl,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        try {

                            JSONObject body = response.getJSONObject("body");
                            JSONArray items = body.getJSONArray("items");
                            JSONObject firstItem = items.getJSONObject(0);


                            Drug drug = new Drug();
                            drug.itemNameText = firstItem.getString("itemName");
                            drug.entpNameText = firstItem.getString("entpName");
                            drug.itemSeqText = Integer.parseInt(firstItem.getString("itemSeq")); // 파싱한 문자열을 정수로 변환
                            drug.efcyQesitmText = firstItem.getString("efcyQesitm");
                            drug.useMethodQesitmText = firstItem.getString("useMethodQesitm");
                            drug.atpnWarnQesitmText = firstItem.optString("atpnWarnQesitm", "정보없음"); // 기본값을 설정하여 null 처리
                            drug.atpnQesitmText = firstItem.optString("atpnQesitm", "정보없음"); // 기본값을 설정하여 null 처리
                            drug.depositMethodQesitmText = firstItem.getString("depositMethodQesitm");
                            drug.itemImageUrl = firstItem.getString("itemImage");
                            drug.intrcQesitmText = firstItem.getString("intrcQesitm");



                            itemName.setText(drug.itemNameText+"");
                            entpName.setText(drug.entpNameText+"");
                            itemSeq.setText(drug.itemSeqText+"");
                            efcyQesitm.setText(drug.efcyQesitmText+"");
                            useMethodQesitm.setText(drug.useMethodQesitmText+"");



                            if (drug.atpnWarnQesitmText != "null" && !drug.atpnWarnQesitmText.isEmpty()) {
                                atpnWarnQesitm.setText(drug.atpnWarnQesitmText);
                            } else {
                                atpnWarnQesitm.setText("정보없음");
                            }


                            if (drug.atpnQesitmText != "null" && !drug.atpnQesitmText.isEmpty()) {
                                atpnQesitm.setText(drug.atpnQesitmText);
                            } else {
                                atpnQesitm.setText("정보없음");
                            }


                            depositMethodQesitm.setText(drug.depositMethodQesitmText+"");



                            Glide.with(DrugDetailActivity.this)
                                    .load(drug.itemImageUrl)
                                    .placeholder(R.drawable.baseline_image_24)
                                    .into(itemImage);


                        } catch (JSONException e) {
                            Log.i("TEST", "파싱에러");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("TEST", error.toString());
                    }
                }
        );

        queue.add(request);


        Drug selectedDrug = getIntent().getParcelableExtra("selectedDrug");
        if (selectedDrug != null) {
            // 선택한 약 정보를 화면에 표시
            itemName.setText(selectedDrug.itemNameText);
            entpName.setText(selectedDrug.entpNameText);
            itemSeq.setText(String.valueOf(selectedDrug.itemSeqText));
            efcyQesitm.setText(selectedDrug.efcyQesitmText);
            useMethodQesitm.setText(selectedDrug.useMethodQesitmText);
            atpnWarnQesitm.setText(selectedDrug.atpnWarnQesitmText);
            atpnQesitm.setText(selectedDrug.atpnQesitmText);
            depositMethodQesitm.setText(selectedDrug.depositMethodQesitmText);
            intrcQesitm.setText(selectedDrug.intrcQesitmText);

            Glide.with(this)
                    .load(selectedDrug.itemImageUrl)
                    .placeholder(R.drawable.baseline_image_24)
                    .into(itemImage);
        }

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
    public void onBackPressed() {
        super.onBackPressed(); // 이전 화면으로 돌아가기
    }

}