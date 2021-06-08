package com.youth.smartbadge.Map;

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

    private Button btnMakeZone;

    private MapPoint mapPoint;
    private MapView mapView;
    private MapPolyline safeZone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        init();

        if (makeState){
            PutSafeZoneOnMap();
        }
        else{
            btnMakeZone.setVisibility(View.VISIBLE);
        }

        btnMakeZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SmartBadge smartBadge = new SmartBadge(Integer.parseInt(smartBadgeID), true);
                retrofitAPI.putMakeState(smartBadgeID, smartBadge).enqueue(new Callback<SmartBadge>() {
                    @Override
                    public void onResponse(Call<SmartBadge> call, Response<SmartBadge> response) {
                        if(response.isSuccessful()){
                            Log.d("test", Boolean.toString(response.body().getMakeState()));
                            btnMakeZone.setVisibility(View.GONE);
                        }
                    }
                    @Override
                    public void onFailure(Call<SmartBadge> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            }
        });
    }

    public void PutSafeZoneOnMap(){
        retrofitAPI.getGpsRouteData(smartBadgeID).enqueue(new Callback<List<SmartBadge>>() {
            @Override
            public void onResponse(Call<List<SmartBadge>> call, Response<List<SmartBadge>> response) {
                if(response.isSuccessful()){
                    mapView.removeAllPOIItems();
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


    public void MapMarker(String MakerName, String detail, float startX, float startY) {
        mapPoint = MapPoint.mapPointWithGeoCoord( startY, startX );
        mapView.setMapCenterPointAndZoomLevel( mapPoint, 1, true);
        //true면 앱 실행 시 애니메이션 효과가 나오고 false면 애니메이션이 나오지않음.
        MapPOIItem marker = new MapPOIItem();
        marker.setItemName(MakerName+"("+detail+")");
        // 마커 클릭 시 컨테이너에 담길 내용
        marker.setMapPoint( mapPoint );
        // 마커 기본 이미지
        marker.setCustomImageResourceId(R.drawable.child_marker_map);
        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
        // 마커 클릭시 이미지
        marker.setCustomSelectedImageResourceId(R.drawable.child_marker_map);
        marker.setSelectedMarkerType( MapPOIItem.MarkerType.CustomImage );
        mapView.addPOIItem( marker );
    }


    public void init(){
        btnMakeZone = findViewById(R.id.btn_setting_make_zone);
        mapView = new MapView(this);
        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.view_setting_main);
        mapViewContainer.addView(mapView);

        safeZone = new MapPolyline();
        safeZone.setTag(1000);
        safeZone.setLineColor(Color.argb(128, 7, 248, 90));


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
}