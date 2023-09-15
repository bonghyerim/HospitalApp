package com.project.hospitalapp.adapter;

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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.project.hospitalapp.AlarmViewActivity;
import com.project.hospitalapp.AlertReceiver;
import com.project.hospitalapp.FoodActivity;
import com.project.hospitalapp.R;
import com.project.hospitalapp.api.AlarmApi;
import com.project.hospitalapp.api.MedicineApi;
import com.project.hospitalapp.api.NetworkClient;
import com.project.hospitalapp.config.Config;
import com.project.hospitalapp.model.Alarm;
import com.project.hospitalapp.model.Medicine;
import com.project.hospitalapp.model.ResultRes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MedicineAdapter extends  RecyclerView.Adapter<MedicineAdapter.ViewHolder> {

    Context context;

    ArrayList<Medicine> medicineArrayList;


    String medicineName;

    String alarmEndMorning;

    String alarmEndLunch;

    String alarmEndDinner;


    public MedicineAdapter(Context context, ArrayList<Medicine> medicineArrayList) {
        this.context = context;
        this.medicineArrayList = medicineArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.medicine_row, parent, false);
        return new MedicineAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Medicine medicine = medicineArrayList.get(position);

        String alarmStart = medicine.getStartMedicine().replace("T", " ").substring(0, 15+1);
        String alarmEnd =  medicine.endMedicine.replace("T", " ").substring(0, 15+1);

        // 약 이름 설정
        holder.txtMedicine.setText(medicine.getMedicineName());


        // 약 복용 기간 설정
        String period = alarmStart + " ~ " + alarmEnd;
        holder.txtPeriod.setText(period);

        // 복용 시간 설정
        if(medicine.getAlarm() != null) {
            String alarmTimes = TextUtils.join(", ", medicine.getAlarm());
            holder.txtTime.setText(alarmTimes);
        } else {
            holder.linearLayoutTakeTime.setVisibility(View.GONE);
        }

        // 피해야할 음식 설정
        if(medicine.getFoodTag() != null) {
            String foodTags = TextUtils.join(", ", medicine.getFoodTag());
            holder.txtFood.setText(foodTags);
        } else {
            holder.linearLayoutFood.setVisibility(View.GONE);
        }

        // 현재 시간 가져오기
        String currentDateTime = getCurrentTime();

        Log.d("MedicineAdapter", "Current DateTime: " + currentDateTime);

        // 현재 시간과 alarmEnd 비교하여 배경색 설정
        if (compareDateTime(alarmEnd, currentDateTime) < 0) {
            // 현재 시간이 endMedicine보다 이전인 경우 배경색 변경
            holder.cardView.setBackgroundResource(R.color.cardExpiredColor);
        } else {
            // 현재 시간이 endMedicine 이후인 경우 배경색 초기화
            holder.cardView.setBackgroundResource(android.R.color.transparent);
        }

        String alarmTimesText = holder.txtTime.getText().toString();


        alarmEndMorning = alarmEnd.substring(0, 11) + "07:01";
        Log.d("MedicineAdapter", "alarmEndMorning: " + alarmEndMorning);


        alarmEndLunch = alarmEnd.substring(0, 11) + "12:01";
        Log.d("MedicineAdapter", "alarmEndLunch: " + alarmEndLunch);


        alarmEndDinner = alarmEnd.substring(0, 11) + "18:05";
        Log.d("MedicineAdapter", "alarmEndDinner: " + alarmEndDinner);

        medicineName = medicine.getMedicineName();

        // 알람 설정을 중복 방지하기 위한 SharedPreferences 초기화
        SharedPreferences sharedPreferences = context.getSharedPreferences("AlarmStatus", Context.MODE_PRIVATE);
        boolean isMorningAlarmSet = sharedPreferences.getBoolean(medicineName + "_morning", false);
        boolean isNoonAlarmSet = sharedPreferences.getBoolean(medicineName + "_lunch", false);
        boolean isDinnerAlarmSet = sharedPreferences.getBoolean(medicineName + "_dinner", false);

        if (!isMorningAlarmSet && compareDateTime(alarmEndMorning, currentDateTime) > 0) {
            if (alarmTimesText.contains("아침")) {
                int requestCodeMorning = generateUniqueRequestCode(medicineName, "morning");
                Log.d("AlarmViewActivity", "Morning Alarm RequestCode: " + requestCodeMorning + " 현재시간 : " + currentDateTime);
                startAlarm("07:00", requestCodeMorning, alarmEndMorning, medicineName + "_morning" , "morning");
                // 알람 설정 상태를 저장
                sharedPreferences.edit().putBoolean(medicineName + "_morning", true).apply();
                // 알람 취소 스레드 시작
                startAlarmCancelThread(alarmEndMorning, requestCodeMorning, medicineName, "morning");
            }
        }
        if (!isNoonAlarmSet && compareDateTime(alarmEndLunch, currentDateTime) > 0) {
            if (alarmTimesText.contains("점심")) {
                int requestCodeNoon = generateUniqueRequestCode(medicineName, "lunch");
                Log.d("AlarmViewActivity", "Noon Alarm RequestCode: " + requestCodeNoon + " 현재시간 : " + currentDateTime);
                startAlarm("12:00", requestCodeNoon, alarmEndLunch, medicineName + "_lunch", "lunch");
                // 알람 설정 상태를 저장
                sharedPreferences.edit().putBoolean(medicineName + "_lunch", true).apply();
                // 알람 취소 스레드 시작
                startAlarmCancelThread(alarmEndLunch, requestCodeNoon, medicineName, "lunch");
            }
        }

        if (!isDinnerAlarmSet && compareDateTime(alarmEndDinner, currentDateTime) > 0) {
            if (alarmTimesText.contains("저녁")) {
                int requestCodeDinner = generateUniqueRequestCode(medicineName, "dinner");
                Log.d("AlarmViewActivity", "Dinner Alarm RequestCode: " + requestCodeDinner + " 현재시간 : " + currentDateTime);
                startAlarm("18:00", requestCodeDinner, alarmEndDinner, medicineName + "_dinner", "dinner");
                // 알람 설정 상태를 저장
                sharedPreferences.edit().putBoolean(medicineName + "_dinner", true).apply();
                // 알람 취소 스레드 시작
                startAlarmCancelThread(alarmEndDinner, requestCodeDinner, medicineName, "dinner");
            }
        }
    }













    @Override
    public int getItemCount() {
        return medicineArrayList.size();
    }




    public  class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgDelete;
        TextView txtMedicine;
        TextView txtPeriod;
        TextView txtTime;
        TextView txtFood;

        CardView cardView;

        LinearLayout linearLayoutFood;
        LinearLayout linearLayoutTakeTime;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtMedicine = itemView.findViewById(R.id.txtMedicine);
            txtPeriod = itemView.findViewById(R.id.txtPeriod);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtFood = itemView.findViewById(R.id.txtFood);
            imgDelete = itemView.findViewById(R.id.imgDelete);
            cardView = itemView.findViewById(R.id.cardView);
            linearLayoutFood = itemView.findViewById(R.id.linearLayoutFood);
            linearLayoutTakeTime = itemView.findViewById(R.id.linearLayoutTakeTime);

            imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showAlertDialog();



                }
            });



            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, FoodActivity.class);

                    int index = getAdapterPosition();

                    Medicine medicine =  medicineArrayList.get(index);

                    intent.putExtra("medicine", medicine);
                    intent.putExtra("update", true);


                    context.startActivity(intent);

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
                    Medicine medicine = medicineArrayList.get(index);


                    // 알람을 취소하는 메서드 호출
                    cancelAlarm(medicine);


                    SharedPreferences sp = context.getSharedPreferences(Config.PREFERENCE_NAME, Context.MODE_PRIVATE);
                    String token = sp.getString(Config.ACCESS_TOKEN, "");

                    showProgress();

                    Retrofit retrofit = NetworkClient.getRetrofitClient(context);

                    MedicineApi api = retrofit.create(MedicineApi.class);

                    Call<ResultRes> call = api.deleteMedicine(medicine.id, "Bearer " + token );

                    call.enqueue(new Callback<ResultRes>() {
                        @Override
                        public void onResponse(Call<ResultRes> call, Response<ResultRes> response) {
                            dismissProgress();

                            if(response.isSuccessful()){

                                medicineArrayList.remove(medicine);
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
    private int generateUniqueRequestCode(String medicineName, String alarmType) {
        // 약 이름과 알람 유형을 조합하여 고유한 requestCode 생성
        String requestCodeString = medicineName + "_" + alarmType;
        return requestCodeString.hashCode() & 0xfffffff; // 양수 값으로 변환
    }




    private void startAlarm(String alarmTime, int requestCode, String alarmEndDate, String medicineName, String alarmType) {
        Log.d("AlarmViewActivity", "Setting alarm for time: " + alarmTime + " on date: " + alarmEndDate + "name : " + medicineName);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, AlertReceiver.class);
        alarmIntent.putExtra("content", medicineName); // 알람 내용을 약 이름으로 설정
        alarmIntent.putExtra("alarmType", alarmType);



        // 이전에 설정된 알람이 있는지 확인
        PendingIntent existingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            existingIntent = PendingIntent.getBroadcast(context, requestCode, alarmIntent, PendingIntent.FLAG_MUTABLE);
        } else {
            existingIntent = PendingIntent.getBroadcast(context, requestCode, alarmIntent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);
        }

        // 이미 설정된 알람이 있다면 취소
        if (existingIntent != null) {
            alarmManager.cancel(existingIntent);
            existingIntent.cancel();
        }



        // Request Code를 requestCode로 설정
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getBroadcast(context, requestCode, alarmIntent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getBroadcast(context, requestCode, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        String alarmDate = alarmEndDate.substring(0, 10) + " 00:00";
        // 알람 시간 설정
        int hourOfDay = Integer.parseInt(alarmTime.split(":")[0]);
        int minute = Integer.parseInt(alarmTime.split(":")[1]);


        Calendar alarmCalendar = Calendar.getInstance();
        alarmCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        alarmCalendar.set(Calendar.MINUTE, minute);
        alarmCalendar.set(Calendar.SECOND, 0);

        // 현재 시간과 알람 시간을 비교하여 이미 지난 알람은 설정하지 않음
        if (alarmCalendar.before(Calendar.getInstance())) {
            return;
        }

        // 알람 설정
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), pendingIntent);
        }


        // 알람을 반복 설정
        long intervalMillis = 1 * 30 * 1000; // 분을 밀리초로 변환
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), intervalMillis, pendingIntent);
    }


    // 알람 취소 스레드 시작하는 메서드
    private void startAlarmCancelThread(String alarmEndTime, int requestCode, String medicineName, String alarmType) {
        Thread checkTimeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    // 현재 시간 가져오기
                    String currentDateTime = getCurrentTime();

                    if (compareDateTime(currentDateTime, alarmEndTime) > 0) {
                        // 현재 시간이 알람 종료 시간보다 커졌을 때 알람을 취소
                        int requestCodeMorning = generateUniqueRequestCode(medicineName, alarmType);
                        cancelSingleAlarm(requestCodeMorning);

                        // 알람 설정 상태를 초기화
                        SharedPreferences sharedPreferences = context.getSharedPreferences("AlarmStatus", Context.MODE_PRIVATE);
                        sharedPreferences.edit().putBoolean(medicineName + "_" + alarmType, false).apply();

                        // 루프 종료
                        break;
                    }

                    try {
                        // 30초마다 현재 시간을 확인
                        Thread.sleep(30000); // 1분 = 60,000 밀리초
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        checkTimeThread.start();
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        return sdf.format(calendar.getTime());
    }

    private int compareDateTime(String dateTime1, String dateTime2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        try {
            Date date1 = sdf.parse(dateTime1);
            Date date2 = sdf.parse(dateTime2);
            return date1.compareTo(date2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0; // 비교 중 오류 발생 시 0 반환
    }


    // 알람을 취소하는 메서드
    private void cancelAlarm(Medicine medicine) {
        // Medicine 객체에서 알람 정보를 가져와서 해당 알람을 취소하는 로직을 작성하세요.
        // 알람을 식별하기 위한 고유한 requestCode를 생성하여 사용합니다.
        String medicineName = medicine.getMedicineName();

        int requestCodeMorning = generateUniqueRequestCode(medicineName, "morning");
        int requestCodeNoon = generateUniqueRequestCode(medicineName, "lunch");
        int requestCodeDinner = generateUniqueRequestCode(medicineName, "dinner");

        // 해당 requestCode를 사용하여 알람을 취소합니다.
        cancelSingleAlarm(requestCodeMorning);
        cancelSingleAlarm(requestCodeNoon);
        cancelSingleAlarm(requestCodeDinner);
    }

    // requestCode를 사용하여 단일 알람을 취소하는 메서드
    private void cancelSingleAlarm(int requestCode) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // 알람을 취소합니다.
        alarmManager.cancel(pendingIntent);

        // PendingIntent를 취소합니다.
        pendingIntent.cancel();
    }








}
