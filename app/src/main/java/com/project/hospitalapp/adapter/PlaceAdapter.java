package com.project.hospitalapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.project.hospitalapp.R;
import com.project.hospitalapp.api.NetworkClient;
import com.project.hospitalapp.api.PlaceApi;
import com.project.hospitalapp.config.Config;
import com.project.hospitalapp.model.Place;
import com.project.hospitalapp.model.PlaceDetailResult;

import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {

    Context context;
    ArrayList<Place> placeArrayList;

    public PlaceAdapter(Context context, ArrayList<Place> placeArrayList) {
        this.context = context;
        this.placeArrayList = placeArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_row, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Place place = placeArrayList.get(position);

        if(place.name == null) {
            holder.txtName.setText("상점명 없음");
        }else{
            holder.txtName.setText(place.name);

        }

        if(place.vicinity == null){
            holder.txtVicinity.setText("주소 없음");
        }else {
            holder.txtVicinity.setText(place.vicinity);
        }

        if (place.opening_hours != null) {
            if (place.opening_hours.open_now) {
                holder.openingHours.setText("영업중");
            } else {
                holder.openingHours.setText("준비중");
            }
        } else {
            // opening_hours가 null인 경우 처리
            holder.openingHours.setText("운영 정보 없음");
        }

        String distanceText = formatDistance(place.getDistance()); // 거리 정보 포맷팅
        holder.placeDistance.setText(distanceText); // 포맷팅된 거리 정보 표시
    }

    private String formatDistance(float distance) {
        if (distance >= 1000) {
            // 거리가 1킬로미터 이상인 경우 "km" 단위로 표시
            return String.format(Locale.getDefault(), "거리 %.1f km", distance / 1000);
        } else {
            // 그 외의 경우 "m" 단위로 표시
            return String.format(Locale.getDefault(), "거리 %.0f m", distance);
        }

    }

    @Override
    public int getItemCount() {
        return placeArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView txtName;
        TextView txtVicinity;

        TextView openingHours;

        TextView placeDistance;

        CardView cardView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            txtName = itemView.findViewById(R.id.txtName);
            txtVicinity = itemView.findViewById(R.id.txtVicinity);
            openingHours = itemView.findViewById(R.id.openingHours);
            placeDistance = itemView.findViewById(R.id.placeDistance);
            cardView = itemView.findViewById(R.id.cardView);

            cardView.setOnClickListener(new View.OnClickListener() {




                @Override
                public void onClick(View view) {




                    int index = getAdapterPosition();

                    Place place = placeArrayList.get(index);



                    Retrofit retrofit = NetworkClient.getRetrofitClient(context, Config.GOOGLE_MAP_HOST);
                    PlaceApi api = retrofit.create(PlaceApi.class);



                    Call<PlaceDetailResult> call = api.getDetailList("ko",place.place_id,
                            Config.GOOGLE_API_KEY);



                    call.enqueue(new Callback<PlaceDetailResult>() {
                        @Override
                        public void onResponse(Call<PlaceDetailResult> call, Response<PlaceDetailResult> response) {

                            if(response.isSuccessful()){



                                PlaceDetailResult detailResult = response.body();

                                PlaceDetailResult.DetailPlace detailPlace = detailResult.result;


                                Log.i("URL", detailPlace.url);

                                openWebPage(detailPlace.url);


                            }else{
                                Log.i("TEST", "요청 실패");

                            }

                        }

                        @Override
                        public void onFailure(Call<PlaceDetailResult> call, Throwable t) {
                            Log.i("TEST", "데이터 가공 실패");
                            Log.i("TEST", t.toString());
                        }
                    });

                }
            });



        }
    }

    private void openWebPage (String url){
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

}
