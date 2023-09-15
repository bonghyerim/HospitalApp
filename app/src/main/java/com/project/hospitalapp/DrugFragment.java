package com.project.hospitalapp;

import static com.project.hospitalapp.LoginActivity.checkLogin;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.project.hospitalapp.adapter.DrugAdapter;
import com.project.hospitalapp.model.Drug;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DrugFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DrugFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private EditText editKeyword;
    private ImageButton btnSearch;

    private Button buttonFood;
    private RecyclerView recyclerView;
    private DrugAdapter adapter;
    private ArrayList<Drug> drugList = new ArrayList<>();

    public DrugFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DrugFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DrugFragment newInstance(String param1, String param2) {
        DrugFragment fragment = new DrugFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_drug, container, false);

        editKeyword = rootView.findViewById(R.id.editKeywo);
        btnSearch = rootView.findViewById(R.id.btnSear);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        buttonFood = rootView.findViewById(R.id.buttonFood);
        adapter = new DrugAdapter(requireContext(), drugList);
        adapter.setOnItemClickListener(new DrugAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Drug drug) {
                // 약 정보를 DrugDetailActivity에 전달하고 해당 액티비티를 호출
                Intent intent = new Intent(requireContext(), DrugDetailActivity.class);
                intent.putExtra("selectedDrug", drug);
                intent.putExtra("searchKeyword", drug.itemNameText);
                startActivity(intent); // 에러. 어댑터에서 클릭했을 때 이동할 수 있도록 수정하기.
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));



        buttonFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkLogin(getActivity(), FoodActivity.class);


            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = editKeyword.getText().toString();

                if (searchText.isEmpty()) {
                    // 키워드가 비어 있는 경우 스낵바 메시지 표시
                    Snackbar.make(btnSearch, "검색어를 입력하세요.", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                String apiUrl = "https://ajjjrg32tj.execute-api.ap-northeast-2.amazonaws.com/medicine/search?keyword=" + searchText ;

                RequestQueue queue = Volley.newRequestQueue(requireContext());
                JsonObjectRequest request = new JsonObjectRequest(
                        Request.Method.GET,
                        apiUrl,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONArray items = response.getJSONArray("items");

                                    Log.d("DrugActivity", "JSON 파싱 시작");

                                    // 기존 데이터 초기화
                                    drugList.clear();

                                    // 검색 결과 데이터 파싱 및 drugList에 추가
                                    for (int i = 0; i < items.length(); i++) {
                                        JSONObject item = items.getJSONObject(i);

                                        Log.d("DrugActivity", "파싱 중: " + item.toString());

                                        Drug drug = new Drug();
                                        drug.itemNameText = item.getString("itemName");
                                        drug.entpNameText = item.getString("entpName");
                                        drug.itemSeqText = Integer.parseInt(item.getString("itemSeq")); // 파싱한 문자열을 정수로 변환
                                        drug.efcyQesitmText = item.getString("efcyQesitm");
                                        drug.useMethodQesitmText = item.getString("useMethodQesitm");
                                        drug.atpnWarnQesitmText = item.optString("atpnWarnQesitm", "정보없음"); // 기본값을 설정하여 null 처리
                                        drug.atpnQesitmText = item.optString("atpnQesitm", "정보없음"); // 기본값을 설정하여 null 처리
                                        drug.depositMethodQesitmText = item.getString("depositMethodQesitm");
                                        drug.itemImageUrl = item.getString("itemImage");
                                        drug.intrcQesitmText = item.getString("intrcQesitm");
                                        drugList.add(drug);
                                    }

                                    Log.d("DrugActivity", "JSON 파싱 완료");

                                    // 어댑터에 데이터 변경 알림
                                    adapter.notifyDataSetChanged();
                                } catch (JSONException e) {
                                    Log.e("DrugActivity", "파싱 에러: " + e.getMessage());
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

                // API 요청을 큐에 추가
                queue.add(request);
            }
        });

        return rootView;
    }


    // ... (기타 코드 생략)
}