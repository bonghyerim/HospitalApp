package com.project.hospitalapp;

import static android.content.ContentValues.TAG;

import static com.project.hospitalapp.LoginActivity.checkLogin;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.material.snackbar.Snackbar;
import com.project.hospitalapp.adapter.NewsAdapter;
import com.project.hospitalapp.api.NewsApi;
import com.project.hospitalapp.api.RetrofitClientInstance;
import com.project.hospitalapp.model.NewsItem;
import com.project.hospitalapp.model.NewsResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {




    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private void fetchNewsData() {
        // Retrofit 인스턴스 가져오기
        NewsApi newsApi = RetrofitClientInstance.getRetrofitInstance().create(NewsApi.class);

        // API 요청 보내기
        Call<NewsResponse> call = newsApi.getNews();
        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    NewsResponse newsResponse = response.body();
                    List<NewsItem> newsItemList = newsResponse.getItems();

                    for (NewsItem newsItem : newsItemList) {
                        Log.d(TAG, "Title: " + newsItem.getTitle() + ", Summary: " + newsItem.getSummary());
                    }

                    // 뉴스 데이터를 리사이클러뷰에 바인딩
                    NewsAdapter newsAdapter = new NewsAdapter(getContext(), newsItemList);
                    recyclerView.setAdapter(newsAdapter);
                } else {
                    // 요청 실패 또는 데이터가 비어 있는 경우 처리
                    Log.e(TAG, "API 요청 실패 또는 데이터가 비어 있음");
                }
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                // 네트워크 오류 또는 요청 실패 시 처리
                Log.e(TAG, "API 요청 실패: " + t.getMessage());
            }
        });
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    EditText searchText;
    ImageView imgSearch;

    CardView ingredient, drug;

    RecyclerView recyclerView;
    NewsAdapter newsAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main, container, false);

        searchText = rootView.findViewById(R.id.editKeyword);
        imgSearch = rootView.findViewById(R.id.imgSearch);
        ingredient = rootView.findViewById(R.id.ingredient);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        drug = rootView.findViewById(R.id.drug);

        // 액션바 설정
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("My Doctor"); // 원하는 이름으로 변경
        }

        fetchNewsData();


        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String keyword = searchText.getText().toString().trim();

                if(keyword.isEmpty()){
                    Snackbar.make(imgSearch,
                            "검색어를 입력해주세요.",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(getActivity(), SearchActivity.class);
                intent.putExtra("keyword", keyword);
                startActivity(intent);
            }
        });

        ingredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkLogin(getActivity(), IngredientActivity.class);


            }
        });

        drug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkLogin(getActivity(), FoodActivity.class);


            }
        });


        return rootView;
    }



}