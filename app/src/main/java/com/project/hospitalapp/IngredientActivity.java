package com.project.hospitalapp;

import static com.project.hospitalapp.LoginActivity.checkLogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageAnnotatorSettings;
import com.google.protobuf.ByteString;
import com.google.cloud.vision.v1.Image;
import com.project.hospitalapp.api.FoodApi;
import com.project.hospitalapp.api.NetworkClient;
import com.project.hospitalapp.config.Config;
import com.project.hospitalapp.model.FoodAllLIst;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class IngredientActivity extends AppCompatActivity {

    MaterialCardView search;

    TextView txtContent;

    TextView txtResult;

    File photoFile;
    private ImageAnnotatorClient visionClient;

    String token;


    ImageView imageView3;

    Bitmap selectedImage;


    String foodTagsText;
    BottomNavigationView bottomNavigationView;

    TextView textView27;


    Button btnSearc;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient);

        // 뒤로 가기 버튼 표시
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("성분 검색"); // 원하는 타이틀로 변경
        }

        search = findViewById(R.id.search);
        txtContent = findViewById(R.id.txtContent);
        txtResult = findViewById(R.id.txtResult);
        textView27 = findViewById(R.id.textView27);

        imageView3 = findViewById(R.id.imageView3);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                checkLogin(IngredientActivity.this, itemId);

                return false;
            }
        });

        btnSearc = findViewById(R.id.btnSearc);

        // Vision API Client 초기화
        try {
            GoogleCredentials credentials = GoogleCredentials.fromStream(getResources().openRawResource(R.raw.youtube_app_393207_f8be737c9d7e));
            ImageAnnotatorSettings settings = ImageAnnotatorSettings.newBuilder()
                    .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                    .build();
            visionClient = ImageAnnotatorClient.create(settings);
        } catch (IOException e) {
            e.printStackTrace();
        }


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDialog();
            }
        });


        btnSearc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                token = sp.getString(Config.ACCESS_TOKEN, "");

                String strResult = "" + txtResult;

                if (!strResult.isEmpty()) {


                    Retrofit retrofit = NetworkClient.getRetrofitClient(IngredientActivity.this);

                    FoodApi api = retrofit.create(FoodApi.class);

                    Call<FoodAllLIst> call = api.getFoodAll("Bearer " + token);

                    call.enqueue(new Callback<FoodAllLIst>() {
                        @Override
                        public void onResponse(Call<FoodAllLIst> call, Response<FoodAllLIst> response) {
                            if (response.isSuccessful()) {
                                FoodAllLIst foodAllList = response.body();
                                if (foodAllList != null && foodAllList.items != null) {
                                    foodTagsText = TextUtils.join(", ", foodAllList.items);


                                    // txtContent에서 텍스트 가져오기
                                    String contentText = txtContent.getText().toString();

                                    if (contentText.isEmpty()) {
                                        // txtContent가 비어있을 때 Snackbar를 표시합니다.
                                        Snackbar.make(v, "원재료명 영역을 올려주세요.", Snackbar.LENGTH_SHORT).show();
                                        return; // 더 이상 진행하지 않고 함수를 종료합니다.
                                    }

                                    // 개행 문자(\n)를 쉼표(,)로 대체하여 텍스트 항목을 나눕니다.
                                    String[] contentArray = contentText.replace("\n", ", ").split(", ");

                                    // contentArray를 ArrayList로 변환
                                    List<String> textListItems = Arrays.asList(contentArray);

                                    // 불필요한 공백을 제거합니다.
                                    textListItems = textListItems.stream().map(String::trim).collect(Collectors.toList());

                                    Log.d("TextListItems", "textListItems: " + textListItems.toString());

                                    // foodTagsText를 콤마(,)로 구분하여 배열로 만듭니다.
                                    String[] foodTagsArray = foodTagsText.split(", ");

                                    Log.d("foodTagsText", "foodTagsText: " + Arrays.toString(foodTagsArray));

                                    // textListItems와 foodTagsArray를 비교하여 일치하는 텍스트를 찾습니다.
                                    ArrayList<String> matchingTexts = new ArrayList<>();


                                    for (String foodTag : foodTagsArray) {
                                        for (String textItem : textListItems) {
                                            String[] foodWords = foodTag.trim().split(" ");
                                            for (String foodWord : foodWords) {
                                                if (foodWord.length() == 1 && textItem.equals(foodWord)) {
                                                    // 요소가 한 글자이고, 전부 일치할 경우에 추가
                                                    matchingTexts.add(textItem);
                                                } else if (foodWord.length() >= 2 && textItem.contains(foodWord)) {
                                                    // 요소가 두 글자 이상이고, 두 글자 이상이 일치하는 경우에 추가
                                                    matchingTexts.add(textItem);
                                                }
                                            }
                                        }
                                    }


                                    // matchingTexts 리스트의 요소를 콤마로 구분하여 하나의 문자열로 만듭니다.
                                    String resultText = TextUtils.join(", ", matchingTexts);


                                    // txtResult TextView에 결과를 설정합니다.

                                    // Set the text color of resultText to red and leave the rest in default color
                                    if (!resultText.isEmpty()) {
                                        String coloredText = "<font color='#FF0000'>" + resultText + "</font>";
                                        txtResult.setText(Html.fromHtml("먹으면 안되는 음식 " + coloredText + "가(이) 들어간 제품입니다"));
                                    } else {
                                        txtResult.setText("먹어도 되는 제품입니다.");
                                    }


                                    Log.d("Textlist", resultText);
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
                        public void onFailure(Call<FoodAllLIst> call, Throwable t) {
                            Log.e("NetworkError", "Request failed", t);
                        }
                    });


                }else  Toast.makeText(IngredientActivity.this, "사진을 올려주세요.", Toast.LENGTH_SHORT).show();
            }
        });


    }




    private byte[] bitmapToByteArray(Bitmap bitmap) {
        Log.d("Debug", "bitmapToByteArray: Converting bitmap to byte array");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    private void analyzeImageWithVision(Bitmap bitmap) {

        if (bitmap != null) {
            // 이미지 분석 코드
        } else {
            Log.e("Debug", "Bitmap is null in analyzeImageWithVision");
        }
        Log.d("Debug", "analyzeImageWithVision: Analyzing image using Vision API");
        // Bitmap 이미지를 ByteString으로 변환
        ByteString byteString = ByteString.copyFrom(bitmapToByteArray(bitmap));

        // Image 객체 생성
        Image image = Image.newBuilder().setContent(byteString).build();

        // Text Detection 요청 생성
        Feature feature = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                .addFeatures(feature)
                .setImage(image)
                .build();

        // Vision API 호출
        List<AnnotateImageResponse> responses = visionClient.batchAnnotateImages(Arrays.asList(request)).getResponsesList();

        // 결과 처리
        if (!responses.isEmpty()) {
            AnnotateImageResponse response = responses.get(0);

            if (response.hasError()) {
                // 처리 중 오류 발생 시
                Log.e("Vision API", "Error: " + response.getError().getMessage());
            } else {
                // 텍스트 결과 추출 및 TextView에 출력
                StringBuilder resultText = new StringBuilder();
                EntityAnnotation annotation = response.getTextAnnotations(0); // 첫 번째 텍스트 영역만 처리
                resultText.append(annotation.getDescription());
                txtContent.setText(resultText.toString());

            }
        }





    }






    private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(IngredientActivity.this);
        builder.setTitle(R.string.alert_title); // 선택하세요
        builder.setItems(R.array.alert_photo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(i == 0){
                    // 첫번째 항목을 눌렀을 때
                    // 카메라로 사진찍기
                    camera();

                } else if(i == 1){
                    // 두번째 항목 눌렀을 때
                    // 앨범에서 가져오기
                    album();

                }

            }
        });
        builder.show();


    }

    private void album(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkPermission(android.Manifest.permission.READ_MEDIA_IMAGES);
        } else {
            checkPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    private void checkPermission(String permission) {
        if (ContextCompat.checkSelfPermission(IngredientActivity.this, permission)
                == PackageManager.PERMISSION_GRANTED) {
            displayFileChoose();
        } else {
            requestPermission(permission);
        }
    }

    private void requestPermission(String permission) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(IngredientActivity.this, permission)) {
            Log.i("DEBUGGING5", "true");
            Toast.makeText(IngredientActivity.this, "권한 수락이 필요합니다.", Toast.LENGTH_SHORT).show();
        } else {
            Log.i("DEBUGGING6", "false");
            ActivityCompat.requestPermissions(IngredientActivity.this,
                    new String[]{permission}, 500);
        }
    }

    public String getFileName( Uri uri ) {
        Cursor cursor = getContentResolver( ).query( uri, null, null, null, null );
        try {
            if ( cursor == null ) return null;
            cursor.moveToFirst( );
            @SuppressLint("Range") String fileName = cursor.getString( cursor.getColumnIndex( OpenableColumns.DISPLAY_NAME ) );
            cursor.close( );
            return fileName;

        } catch ( Exception e ) {
            e.printStackTrace( );
            cursor.close( );
            return null;
        }
    }

    private void displayFileChoose() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "SELECT IMAGE"), 300);
    }

    private void camera(){
        int permissionCheck = ContextCompat.checkSelfPermission(
                IngredientActivity.this, android.Manifest.permission.CAMERA);

        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(IngredientActivity.this,
                    new String[]{android.Manifest.permission.CAMERA} ,
                    1000);


            Toast.makeText(IngredientActivity.this, "카메라 권한 필요합니다.",
                    Toast.LENGTH_SHORT).show();

            Log.d("Debug1", "Camera permission not granted");
            return;
        } else {

            Log.d("Debug2", "Camera permission granted"); // 추가
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(i.resolveActivity(IngredientActivity.this.getPackageManager())  != null  ){

                // 사진의 파일명을 만들기
                String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                photoFile = getPhotoFile(fileName);

                // todo: 파일경로 확인하기
                Uri fileProvider = FileProvider.getUriForFile(IngredientActivity.this,
                        "com.project.hospitalapp.fileprovider", photoFile);
                i.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
                startActivityForResult(i, 100);

            } else{
                Toast.makeText(IngredientActivity.this, "이폰에는 카메라 앱이 없습니다.",
                        Toast.LENGTH_SHORT).show();
                Log.e("Debug3", "No camera app available");
            }
        }


    }


    private File getPhotoFile(String fileName) {
        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try{
            return File.createTempFile(fileName, ".jpg", storageDirectory);
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(IngredientActivity.this, "권한 허가 되었음",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(IngredientActivity.this, "아직 승인하지 않았음",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case 500: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(IngredientActivity.this, "권한 허가 되었음",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(IngredientActivity.this, "아직 승인하지 않았음",
                            Toast.LENGTH_SHORT).show();
                }

            }

        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("Debug", "onActivityResult: requestCode = " + requestCode + ", resultCode = " + resultCode);
        if (requestCode == 100 && resultCode == RESULT_OK) {

            Bitmap photo = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

            analyzeImageWithVision(photo);


            ExifInterface exif = null;
            try {
                exif = new ExifInterface(photoFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            photo = rotateBitmap(photo, orientation);

            OutputStream os;
            try {
                os = new FileOutputStream(photoFile);
                photo.compress(Bitmap.CompressFormat.JPEG, 50, os);
                os.flush();
                os.close();
            } catch (Exception e) {
                Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
            }

            photo = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

            imageView3.setImageBitmap(photo);
            imageView3.setScaleType(ImageView.ScaleType.CENTER_CROP);

            // 이미지 분석 수행


        } else if (requestCode == 300 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri albumUri = data.getData();
            String fileName = getFileName(albumUri);


            photoFile = new File(this.getCacheDir(), fileName);




            // Bitmap 이미지 로드
            selectedImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

            textView27.setVisibility(View.GONE);

            // 이미지 분석 수행
            if (selectedImage != null) {
                txtContent.setText("");
                txtResult.setText("");
                analyzeImageWithVision(selectedImage);
            } else {
                Log.e("Debug", "Selected image is null");
            }



            try {

                ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(albumUri, "r");
                if (parcelFileDescriptor == null) return;
                FileInputStream inputStream = new FileInputStream(parcelFileDescriptor.getFileDescriptor());
                photoFile = new File(this.getCacheDir(), fileName);
                FileOutputStream outputStream = new FileOutputStream(photoFile);
                IOUtils.copy(inputStream, outputStream);

                // 압축시킨다. 해상도 낮춰서
                Bitmap photo = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                OutputStream os;
                try {
                    os = new FileOutputStream(photoFile);
                    photo.compress(Bitmap.CompressFormat.JPEG, 60, os);
                    os.flush();
                    os.close();
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
                }

                imageView3.setImageBitmap(photo);
                imageView3.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

                // 이미지 분석 수행

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Vision API 클라이언트 정리
        if (visionClient != null) {
            visionClient.close();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(IngredientActivity.this, MainActivity.class);
        intent.putExtra("itemId", 0);
        startActivity(intent);
        finish();
    }


}