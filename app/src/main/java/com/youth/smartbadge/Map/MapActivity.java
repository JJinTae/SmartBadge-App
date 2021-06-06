package com.youth.smartbadge.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup;

import com.youth.smartbadge.Login.RetrofitAPI;
import com.youth.smartbadge.Login.SmartBadge;
import com.youth.smartbadge.R;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapActivity extends AppCompatActivity {

    private String BASE_URL = "http://112.158.50.42:9080";
    private SharedPreferences appData;
    private RetrofitAPI retrofitAPI;

    private String smartBadgeID;
    private String updated_at;
    private float longitude;
    private float latitude;
    private boolean nowSafeState;
    private boolean preSafeState;
    private MapView mapView;
    private MapPoint mapPoint;

    private boolean shouldStopLoop;
    private Handler mHandler;
    private Runnable runnable;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;
    private NotificationChannel channel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        init();
        PutMarkerOnMap();

        shouldStopLoop = false;
        mHandler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                PutMarkerOnMap();
                if (!shouldStopLoop) {
                    mHandler.postDelayed(this, 5000);
                }
            }
        };
        mHandler.post(runnable);
    }

    public void PutMarkerOnMap(){
        retrofitAPI.getLocationData(smartBadgeID).enqueue(new Callback<SmartBadge>() {
            @Override
            public void onResponse(Call<SmartBadge> call, Response<SmartBadge> response) {
                if(response.isSuccessful()){
                    mapView.removeAllPOIItems();
                    Log.d("TEST", Integer.toString(response.body().getSmartBadgeID()));
                    Log.d("TEST", Float.toString(response.body().getLongitude()));
                    Log.d("TEST", Float.toString(response.body().getLatitude()));
                    Log.d("TEST", Boolean.toString(response.body().getSafeState()));
                    longitude = response.body().getLongitude();
                    latitude = response.body().getLatitude();
                    nowSafeState = response.body().getSafeState();
                    updated_at = response.body().getUpdate_at();
                    MapMarker("스마트 배지", updated_at, longitude, latitude);

                    if(!change_valid(nowSafeState)){
                        Notification notification = builder.build();
                        notificationManager.notify(1, notification);
                    }
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
        marker.setCustomImageResourceId(R.drawable.child_marker_map);
        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
        // 마커 클릭시 이미지
        marker.setCustomSelectedImageResourceId(R.drawable.child_marker_map);
        marker.setSelectedMarkerType( MapPOIItem.MarkerType.CustomImage );
        mapView.addPOIItem( marker );
    }

    public boolean change_valid(boolean nowState){
        if(nowState == false && preSafeState == true){
            preSafeState = false;
            return false;
        }
        else if(nowState == false && preSafeState == false){
            return true;
        }
        preSafeState = true;
        return true;
    }

    public void setNotificationManager(){
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        builder = null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            channel = new NotificationChannel("channel_01","MyChannel01", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            builder = new NotificationCompat.Builder(this, "channel_01");
        }
        else{
            builder = new NotificationCompat.Builder(this, null);
        }
        builder.setSmallIcon(R.drawable.child_marker_map);
        builder.setContentTitle("어린이 스마트 배지 알림");
        builder.setContentText("어린이가 안심지역을 이탈하였습니다. 알림을 눌러 확인하세요.");
        builder.setAutoCancel(true);
        Intent intent = new Intent(this, MapActivity.class);
        builder.setContentIntent(PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT));
    }

    public void init(){
        setNotificationManager();
        preSafeState = false;
        mapView = new MapView(this);
        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.view_main_map);
         mapViewContainer.addView(mapView);

        appData = getSharedPreferences("appData", MODE_PRIVATE);
        smartBadgeID = Integer.toString(appData.getInt("smartBadgeID", 0));
        Log.d("InitTest", smartBadgeID);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitAPI = retrofit.create(RetrofitAPI.class);
    }

//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        shouldStopLoop = false;
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        shouldStopLoop = true;
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        shouldStopLoop = false;
//        mHandler.post(runnable);
//    }
}