package com.project.hospitalapp;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.project.hospitalapp.api.NetworkClient;
import com.project.hospitalapp.api.PlaceApi;
import com.project.hospitalapp.config.Config;
import com.project.hospitalapp.model.Place;
import com.project.hospitalapp.model.PlaceList;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class MapFragment extends Fragment {

    EditText editKeyword;
    ImageView btnSearch;
    ArrayList<Place> placeArrayList = new ArrayList<>();

    // 내 위치 가져오기 위한 멤버변수
    LocationManager locationManager;
    LocationListener locationListener;

    double lat;
    double lng;
    int radius = 500;  // 미터 단위
    String keyword;
    boolean isLocationReady;
    String pagetoken;
    SupportMapFragment mapFragment;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    public MapFragment() {

    }

    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
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

    private void getNetworkData() {

        placeArrayList.clear();

        Retrofit retrofit = NetworkClient.getRetrofitClient(requireContext(), Config.GOOGLE_MAP_HOST);
        PlaceApi api = retrofit.create(PlaceApi.class);

        Call<PlaceList> call = api.getPlaceList("ko",
                lat+","+lng,
                radius,
                Config.GOOGLE_API_KEY,
                keyword);

        call.enqueue(new Callback<PlaceList>() {
            @Override
            public void onResponse(Call<PlaceList> call, Response<PlaceList> response) {

                if(response.isSuccessful()){

                    PlaceList placeList = response.body();
                    pagetoken = placeList.next_page_token;
                    placeArrayList.addAll(placeList.results);

                    if (pagetoken != null && !pagetoken.isEmpty()) {

                        Call<PlaceList> call2 = api.getPlaceListPage("ko",
                                lat+","+lng,
                                radius,
                                Config.GOOGLE_API_KEY,
                                keyword,
                                pagetoken);

                        call2.enqueue(new Callback<PlaceList>() {
                            @Override
                            public void onResponse(Call<PlaceList> call2, Response<PlaceList> response2) {

                                if(response2.isSuccessful()) {
                                    PlaceList placeList2 = response2.body();
                                    pagetoken = placeList2.next_page_token;
                                    placeArrayList.addAll(placeList2.results);

                                    setMapMarkers();
                                }
                            }

                            @Override
                            public void onFailure(Call<PlaceList> call2, Throwable t) {

                            }
                        });
                    } else {
                        setMapMarkers();
                    }

                } else {

                }
            }

            @Override
            public void onFailure(Call<PlaceList> call, Throwable t) {

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 100){

            if( ActivityCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED ){

                ActivityCompat.requestPermissions(requireActivity(),
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




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        editKeyword = rootView.findViewById(R.id.editKeyword);
        btnSearch = rootView.findViewById(R.id.btnSearch);





        // 폰의 위치를 가져오기 위해서는, 시스템서비스로부터 로케이션 매니져를
        // 받아온다.
        locationManager = (LocationManager) requireContext().getSystemService(LOCATION_SERVICE);


        // 로케이션 리스터를 만든다.
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                lat = location.getLatitude();
                lng = location.getLongitude();

                if (!isLocationReady) { // 위치가 처음 설정될 때만 초기화
                    isLocationReady = true;

                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {
                            // 초기 위치로 이동
                            LatLng initialLatLng = new LatLng(lat, lng);
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLatLng, 17));

                            // 내 위치에 마커 추가
                            googleMap.addMarker(new MarkerOptions().position(initialLatLng).title("내 위치"));

                            // 처음 앱 실행시에는 빈 ArrayList이므로 마커는 추가하지 않습니다.
                            // 검색 결과 마커는 검색 버튼 클릭시에 추가됩니다.
                        }
                    });
                }
            }
        };

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                // 위치 권한이 부여되지 않았다면 초기 위치를 지정할 수 없음
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    LatLng defaultLatLng = new LatLng(0, 0);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, 1)); // 맵을 움직이지 않도록 초기 위치 설정
                    return;
                }

                // 초기 위치로 이동
                LatLng initialLatLng = new LatLng(lat, lng);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLatLng, 17));

                // 내 위치에 마커 추가
                googleMap.addMarker(new MarkerOptions().position(initialLatLng).title("내 위치"));

                // 처음 앱 실행시에는 빈 ArrayList이므로 마커는 추가하지 않습니다.
                // 검색 결과 마커는 검색 버튼 클릭시에 추가됩니다.
            }
        });

        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    100);

        }

        // 위치기반 허용하였으므로,
        // 로케이션 매니저에, 리스너를 연결한다. 그러면 동작한다.
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                3000,
                -1,
                locationListener);



        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 키보드를 숨기는 코드 추가
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                if(isLocationReady == false){
                    Snackbar.make(btnSearch,
                            "아직 위치를 잡지 못했습니다. 잠시후 다시 검색하세요.",
                            Snackbar.LENGTH_LONG).show();
                    return;
                }

                keyword = editKeyword.getText().toString().trim();

                if(keyword.isEmpty()){
                    Snackbar.make(btnSearch, "검색어를 입력하세요.", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                getNetworkData();

            }
        });
        return rootView;
    }




    private void setMapMarkers() {
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                BitmapDescriptor customMarker = BitmapDescriptorFactory.fromResource(R.drawable.hospital);

                googleMap.clear(); // 기존 마커들 제거
                LatLng initialLatLng = new LatLng(lat, lng);
                googleMap.addMarker(new MarkerOptions().position(initialLatLng).title("내 위치"));

                int markerCount = Math.min(placeArrayList.size(), 40); // 최대 40개의 마커만 추가

                for (int i = 0; i < markerCount; i++) {
                    Place place = placeArrayList.get(i);
                    LatLng placeLatLng = new LatLng(
                            place.geometry.location.lat,
                            place.geometry.location.lng);
                    Log.i("TEST", "장소이름" + i + ": " + place.name);

                    String title = place.name;
                    if (place.opening_hours != null) {
                        if (place.opening_hours.open_now) {
                            title += " - 영업 중";
                        } else {
                            title += " - 영업 종료";
                        }
                    } else {
                        title += " - 영업 정보 없음";
                    }

                    googleMap.addMarker(new MarkerOptions()
                            .position(placeLatLng)
                            .title(title)
                            .icon(customMarker));
                }

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLatLng, 15));
            }
        });
    }


}