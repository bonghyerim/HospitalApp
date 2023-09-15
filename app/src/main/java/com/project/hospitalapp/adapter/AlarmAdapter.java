package com.project.hospitalapp.adapter;

import static android.content.ContentValues.TAG;
import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.project.hospitalapp.AlarmSettingActivity;
import com.project.hospitalapp.AlarmViewActivity;
import com.project.hospitalapp.AlertReceiver;
import com.project.hospitalapp.NotificationHelper;
import com.project.hospitalapp.R;
import com.project.hospitalapp.api.AlarmApi;
import com.project.hospitalapp.api.NetworkClient;
import com.project.hospitalapp.config.Config;
import com.project.hospitalapp.model.Alarm;
import com.project.hospitalapp.model.ResultRes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {


    Context context;

    ArrayList<Alarm> alarmArrayList;




    public AlarmAdapter(Context context, ArrayList<Alarm> alarmArrayList) {
        this.context = context;
        this.alarmArrayList = alarmArrayList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.alarm_row, parent, false);
        return new AlarmAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Alarm alarm = alarmArrayList.get(position);

        Log.d("AlarmAdapter", "Binding data at position: " + position);



        String alarmDate = alarm.alarm;
        alarmDate = alarmDate.replace("T", " ").substring(0, 15+1);
        holder.txtContent.setText(alarm.getContent()); // getContent() 메서드를 사용하여 내용을 설정
        holder.txtDate.setText(alarmDate);
//        holder.imgPhoto.setImageResource(R.drawable.baseline_find_in_page_24);







    }

    @Override
    public int getItemCount() {
        return alarmArrayList.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtContent;

        TextView txtDate;

        ImageView imgDelete;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtContent = itemView.findViewById(R.id.txtContent);
            txtDate = itemView.findViewById(R.id.txtDate);

            imgDelete = itemView.findViewById(R.id.imgDelete);
            cardView = itemView.findViewById(R.id.cardView);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, AlarmViewActivity.class);

                    int index = getAdapterPosition();

                    Alarm alarm =  alarmArrayList.get(index);

                    intent.putExtra("alarm", alarm);


                    context.startActivity(intent);

                }

            });

            imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showAlertDialog();



                }
            });








        }



        private void showAlertDialog() {
            Log.d("AlarmViewActivity", "showAlertDialog called");
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("삭제");
            builder.setMessage("정말 삭제하시겠습니까?");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    int index = getAdapterPosition();
                    Alarm alarm = alarmArrayList.get(index);

                    SharedPreferences sp = context.getSharedPreferences(Config.PREFERENCE_NAME, Context.MODE_PRIVATE);
                    String token = sp.getString(Config.ACCESS_TOKEN, "");

                    showProgress();

                    Retrofit retrofit = NetworkClient.getRetrofitClient(context);

                    AlarmApi api = retrofit.create(AlarmApi.class);

                    Call<ResultRes> call = api.deleteAlarm(alarm.alarmId, "Bearer " + token );

                    call.enqueue(new Callback<ResultRes>() {
                        @Override
                        public void onResponse(Call<ResultRes> call, Response<ResultRes> response) {
                            dismissProgress();

                            if(response.isSuccessful()){

                                alarmArrayList.remove(alarm);
                                notifyDataSetChanged();


                                Toast.makeText(context.getApplicationContext(), "삭제 되었습니다.",Toast.LENGTH_SHORT).show();




                            }else{

                            }

                        }

                        @Override
                        public void onFailure(Call<ResultRes> call, Throwable t) {
                            dismissProgress();

                        }
                    });

                }
            });
            builder.setNegativeButton("NO", null);
            builder.setCancelable(true);  // 다른 영역을 클릭하면 사라지게
            builder.show();
        }
        Dialog dialog;

        void showProgress(){
            dialog = new Dialog(context);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(new ProgressBar(context));
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

        }

        void dismissProgress(){
            dialog.dismiss();

        }
    }








}
