package com.project.hospitalapp;

import static android.content.Context.MODE_PRIVATE;

import static com.project.hospitalapp.FoodActivity.RESULT_MEDICINE_SAVE;
import static com.project.hospitalapp.FoodAllergyActivity.RESULT_FOOD_SAVE;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.project.hospitalapp.adapter.MedicineAdapter;
import com.project.hospitalapp.api.FoodApi;
import com.project.hospitalapp.api.MedicineApi;
import com.project.hospitalapp.api.NetworkClient;
import com.project.hospitalapp.config.Config;
import com.project.hospitalapp.model.Alarm;
import com.project.hospitalapp.model.Food;
import com.project.hospitalapp.model.FoodList;
import com.project.hospitalapp.model.Medicine;
import com.project.hospitalapp.model.MedicineList;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyHealthFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyHealthFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public MyHealthFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyHealthFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyHealthFragment newInstance(String param1, String param2) {
        MyHealthFragment fragment = new MyHealthFragment();
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

    ImageView imgDelete;

    TextView txtFood;

    Button btnFood;

    RecyclerView recyclerView;

    Button btnMedicine;
    String token;

    MedicineAdapter adapter;
    ArrayList<String> foodTags;


    ArrayList<Medicine> medicineArrayList = new ArrayList<Medicine>();

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if(result.getResultCode() == RESULT_FOOD_SAVE){
                        // AddActivity.RESULT_ADDACTIVITY_SAVE를 한 뒤에 알트엔터로 자동완성

                        foodTags = (ArrayList<String>) result.getData().getSerializableExtra("foodList");

                        if(foodTags.size() == 0) {
                            return;
                        }
                        String foodText = "";
                        for (String food : foodTags) {
                            foodText += food + ", ";
                        }
                        Log.i("TEST", foodText);
                        foodText = foodText.substring(0, foodText.length() - 2);
                        txtFood.setText(foodText);

                    } else if (result.getResultCode() == RESULT_MEDICINE_SAVE){
                        Medicine medicine = (Medicine) result.getData().getSerializableExtra("medicine");

                        Log.i("TEST", medicine.medicineName);
                        medicineArrayList.add(medicine);
                        adapter.notifyDataSetChanged();
                    }
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView  = (ViewGroup) inflater.inflate(R.layout.fragment_my_health, container, false);

        imgDelete = rootView.findViewById(R.id.imgDelete);

        txtFood = rootView.findViewById(R.id.txtFood);

        btnFood = rootView.findViewById(R.id.btnFood);

        btnMedicine = rootView.findViewById(R.id.btnMedicine);

        recyclerView = rootView.findViewById(R.id.recyclerView);

        txtFood.setText("입력하세요");

        medicineArrayList.clear();


        SharedPreferences sp = getActivity().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        token=sp.getString(Config.ACCESS_TOKEN, "");

        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());

        FoodApi api = retrofit.create(FoodApi.class);

        Call<FoodList> call =api.getFood( "Bearer "+ token);

        call.enqueue(new Callback<FoodList>() {
            @Override
            public void onResponse(Call<FoodList> call, Response<FoodList> response) {
                if (response.isSuccessful()) {
                    FoodList foodList = response.body();
                    if (foodList != null && foodList.items != null) {
                        foodTags = new ArrayList<>();
                        for (Food food : foodList.items) {
                            foodTags.add(food.foodTag);
                        }
                        String foodTagsText = TextUtils.join(", ", foodTags);



                        Log.d("FoodTags", "foodTagsText: " + foodTagsText);

                        txtFood.setText(foodTagsText);
                        // foodTags 리스트에 foodTag 값을 담았으므로 이후에 원하는 처리를 수행할 수 있습니다.
                        // 예: TextView에 리스트 내용을 설정하거나 다른 작업 수행
                    }
                } else {

                    Log.e("NetworkError", "Error: " + response.code());
                    // 요청 실패 처리

                    // 오류 정보를 response.errorBody()를 통해 얻어와서 로그로 출력하거나 처리할 수 있습니다.
                    try {
                        String errorResponse = response.errorBody().string();
                        Log.e("ErrorResponse", errorResponse);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<FoodList> call, Throwable t) {

                Log.e("NetworkError", "Request failed", t);
            }
        });



        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        MedicineApi apiMedicine = retrofit.create(MedicineApi.class);

        Call<MedicineList> callMedicine = apiMedicine.getMedicine("Bearer " + token);

        callMedicine.enqueue(new Callback<MedicineList>() {
            @Override
            public void onResponse(Call<MedicineList> call, Response<MedicineList> response) {
                if (response.isSuccessful()) {
                    MedicineList medicineList = response.body();
                    Log.d("MedicineAdapter", "MedicineList size: " + medicineList.getItems().size());

                    medicineArrayList.addAll(medicineList.getItems());
                    adapter = new MedicineAdapter(getActivity(), medicineArrayList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {

                    Log.e("MyHealthFragment", "MedicineList is null");
                    // API 요청이 실패한 경우 처리
                    // 예: 오류 메시지 표시
                }
            }

            @Override
            public void onFailure(Call<MedicineList> call, Throwable t) {

                // API 요청 실패 처리
                // 예: 네트워크 연결 오류 등
            }
        });





        btnFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), FoodAllergyActivity.class);

                if(foodTags != null && foodTags.size() != 0) {
                    intent.putExtra("foodList", foodTags);
                }

                launcher.launch(intent);

            }
        });

        btnMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!checkNotificationPermission()) {
                    showNotificationPermissionDialog(); // 권한이 없는 경우 다이얼로그 표시
                } else {
                    Intent intent = new Intent(getActivity(), FoodActivity.class);
                    launcher.launch(intent);

                }

            }
        });

        return rootView;







    }




    private boolean checkNotificationPermission() {
        NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
        return notificationManager.areNotificationsEnabled();
    }

    private void showNotificationPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("알림 권한 설정");
        builder.setMessage("알람을 받으려면 알림 권한을 설정해야 합니다.\n설정 화면으로 이동하시겠습니까?");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 알림 권한 설정 화면으로 이동
                Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra(Settings.EXTRA_APP_PACKAGE, getActivity().getPackageName());
                startActivity(intent);
            }
        });
        builder.setNegativeButton("취소", null);
        builder.show();






    }







    }










