package com.youth.smartbadge.Map;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.youth.smartbadge.Login.RetrofitAPI;
import com.youth.smartbadge.Login.SmartBadge;
import com.youth.smartbadge.R;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SettingActivity extends AppCompatActivity {

    private String BASE_URL = "http://112.158.50.42:9080";
    private SharedPreferences appData;
    private RetrofitAPI retrofitAPI;
    private String smartBadgeID;
    private boolean makeState;

    private Button btnMakeZone, btnDeleteZone, btnMakeNewZone, btnDeleteNewZone, btnDrawJaywalking;

    private ViewGroup mapViewContainer;
    private MapPoint mapPoint;
    private MapView mapView;
    private MapPolyline safeZone;
    private MapPolyline newRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        init();

        if (makeState){
            PutSafeZoneOnMap();
            PutNewRouteOnMap();
        }
        else{
            btnMakeZone.setVisibility(View.VISIBLE);
        }

        btnMakeZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MakeZone();
                Toast.makeText (getApplicationContext(),"안심 지역을 생성하였습니다.", Toast.LENGTH_SHORT).show ();
            }
        });
        btnDeleteZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteGpsRoute();
                Toast.makeText (getApplicationContext(),"안심 지역을 삭제하였습니다.", Toast.LENGTH_SHORT).show ();
            }
        });
        btnMakeNewZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MakeNewZone();
                Toast.makeText (getApplicationContext(),"새로운 안심 지역을 추가하였습니다.", Toast.LENGTH_SHORT).show ();
            }
        });
        btnDeleteNewZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteNewRoute();
                Toast.makeText (getApplicationContext(),"이탈경로를 삭제하였습니다.", Toast.LENGTH_SHORT).show ();
            }
        });
        btnDrawJaywalking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PutJaywalkingOnMap();
                Toast.makeText (getApplicationContext(),"무단횡단 기록을 표시하였습니다.", Toast.LENGTH_SHORT).show ();
            }
        });

    }

    public void PutJaywalkingOnMap(){
        retrofitAPI.getJaywalkingData(smartBadgeID).enqueue(new Callback<List<SmartBadge>>() {
            @Override
            public void onResponse(Call<List<SmartBadge>> call, Response<List<SmartBadge>> response) {
                if (response.isSuccessful()){
                    List<SmartBadge> data = response.body();
                    Log.d("jaywalking on map", "success");

                    if (data.size() > 0){
                        for (int i=0; i<data.size(); i++){
                            String updated_at = data.get(i).getUpdate_at();
                            float latitude =  data.get(i).getLatitude();
                            float longitude = data.get(i).getLongitude();
                            MapMarker("무단 횡단 : ", updated_at, longitude, latitude);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<SmartBadge>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void PutSafeZoneOnMap(){
        retrofitAPI.getGpsRouteData(smartBadgeID).enqueue(new Callback<List<SmartBadge>>() {
            @Override
            public void onResponse(Call<List<SmartBadge>> call, Response<List<SmartBadge>> response) {
                if(response.isSuccessful()){
                    safeZone = new MapPolyline();
                    safeZone.setTag(1000);
                    safeZone.setLineColor(Color.argb(128, 7, 248, 90));

                    List<SmartBadge> data = response.body();
                    Log.d("settingTest", "success");

                    for (int i=0; i<data.size(); i++){
                        float latitude =  data.get(i).getLatitude();
                        float longitude = data.get(i).getLongitude();
                        safeZone.addPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude));
                    }
                    mapView.addPolyline(safeZone);

//                    Log.d("TEST", Integer.toString(response.body().getSmartBadgeID()));
//                    Log.d("TEST", Float.toString(response.body().getLongitude()));
//                    Log.d("TEST", Float.toString(response.body().getLatitude()));
//                    Log.d("TEST", Boolean.toString(response.body().getSafeState()));
//                    longitude = response.body().getLongitude();
//                    latitude = response.body().getLatitude();
//                    nowSafeState = response.body().getSafeState();
//                    updated_at = response.body().getUpdate_at();
//                    MapMarker("스마트 배지", updated_at, longitude, latitude);
                }
            }
            @Override
            public void onFailure(Call<List<SmartBadge>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


    public void PutNewRouteOnMap(){
        retrofitAPI.getNewRouteData(smartBadgeID).enqueue(new Callback<List<SmartBadge>>() {
            @Override
            public void onResponse(Call<List<SmartBadge>> call, Response<List<SmartBadge>> response) {
                if (response.isSuccessful()){
                    List<SmartBadge> data = response.body();
                    Log.d("settingTest", "success");
                    newRoute = new MapPolyline();
                    newRoute.setTag(1001);
                    newRoute.setLineColor(Color.argb(128, 237, 28, 36));

                    if (data.size() > 0){
                        for (int i=0; i<data.size(); i++){
                            float latitude =  data.get(i).getLatitude();
                            float longitude = data.get(i).getLongitude();
                            newRoute.addPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude));
                        }
                        btnMakeNewZone.setVisibility(View.VISIBLE);
                        btnDeleteNewZone.setVisibility(View.VISIBLE);

                        mapView.addPolyline(newRoute);
                    }
                    else {
                        btnMakeNewZone.setVisibility(View.GONE);
                        btnDeleteNewZone.setVisibility(View.GONE);
                        btnDeleteZone.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<SmartBadge>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void DeleteNewRoute(){
        retrofitAPI.deleteNewRouteData(smartBadgeID).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()){
                    Log.d("settingTest", "Delete New Route");
                    btnMakeNewZone.setVisibility(View.GONE);
                    btnDeleteNewZone.setVisibility(View.GONE);
                    mapView.removePolyline(newRoute);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }
    public void MakeNewZone(){
        SmartBadge smartBadge = new SmartBadge(Integer.parseInt(smartBadgeID), true, true);
        retrofitAPI.putMakeState(smartBadgeID, smartBadge).enqueue(new Callback<SmartBadge>() {
            @Override
            public void onResponse(Call<SmartBadge> call, Response<SmartBadge> response) {
                if(response.isSuccessful()){
                    Log.d("test", "make New zone success");
                    btnMakeNewZone.setVisibility(View.GONE);
                    btnDeleteNewZone.setVisibility(View.GONE);
                    btnDeleteZone.setVisibility(View.VISIBLE);
                    DeleteNewRoute();
                    mapView.removePolyline(newRoute);
                    mapView.removePolyline(safeZone);
                    PutSafeZoneOnMap();
                }
            }
            @Override
            public void onFailure(Call<SmartBadge> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void DeleteGpsRoute(){
        retrofitAPI.deleteGpsRouteData(smartBadgeID).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()){
                    Log.d("settingTest", "Delete Gps Route");
                    mapView.removePolyline(safeZone);
                    PutSafeZoneOnMap();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    public void MakeZone(){
        SmartBadge smartBadge = new SmartBadge(Integer.parseInt(smartBadgeID), true, false);
        retrofitAPI.putMakeState(smartBadgeID, smartBadge).enqueue(new Callback<SmartBadge>() {
            @Override
            public void onResponse(Call<SmartBadge> call, Response<SmartBadge> response) {
                if(response.isSuccessful()){
                    Log.d("test", Boolean.toString(response.body().getMakeState()));
                    PutSafeZoneOnMap();
                    btnMakeZone.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(Call<SmartBadge> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


    public void MapMarker(String MakerName, String detail, float startX, float startY) {
        mapPoint = MapPoint.mapPointWithGeoCoord( startY, startX );
        mapView.setMapCenterPointAndZoomLevel( mapPoint, 1, true);
        //true면 앱 실행 시 애니메이션 효과가 나오고 false면 애니메이션이 나오지않음.
        MapPOIItem marker = new MapPOIItem();
        marker.setItemName(MakerName+"("+detail+")");
        // 마커 클릭 시 컨테이너에 담길 내용
        marker.setMapPoint( mapPoint );
        // 마커 기본 이미지
        marker.setMarkerType(MapPOIItem.MarkerType.RedPin);
        // 마커 클릭시 이미지
        marker.setSelectedMarkerType( MapPOIItem.MarkerType.BluePin);
        mapView.addPOIItem( marker );
    }


    public void init(){
        btnMakeZone = findViewById(R.id.btn_setting_make_zone);
        btnDeleteZone = findViewById(R.id.btn_setting_delete_zone);
        btnMakeNewZone = findViewById(R.id.btn_setting_make_new_zone);
        btnDeleteNewZone = findViewById(R.id.btn_setting_delete_new_zone);
        btnDrawJaywalking = findViewById(R.id.btn_setting_draw_jaywalking);
        // mapView = new MapView(this);
        mapViewContainer = (ViewGroup) findViewById(R.id.view_setting_main);
        // mapViewContainer.addView(mapView);





        appData = getSharedPreferences("appData", MODE_PRIVATE);
        smartBadgeID = Integer.toString(appData.getInt("smartBadgeID", 0));
        Log.d("InitTest", smartBadgeID);


        Intent settingIntent = getIntent();
        makeState = settingIntent.getBooleanExtra("makeState", false);
        Log.d("InitTest", Boolean.toString(makeState));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitAPI = retrofit.create(RetrofitAPI.class);
    }

    @Override
    public void finish(){
        mapViewContainer.removeView(mapView);
        super.finish();
    }

    @Override
    protected void onPause() {
        mapViewContainer.removeView(mapView);
        super.onPause();
    }

    @Override
    protected void onResume() {
        mapView = new MapView(this);
        mapViewContainer.addView(mapView);
        super.onResume();
    }
}