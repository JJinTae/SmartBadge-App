package com.youth.smartbadge;

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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.AccessTokenInfo;
import com.youth.smartbadge.Login.LoginActivity;
import com.youth.smartbadge.Login.RetrofitAPI;
import com.youth.smartbadge.Login.SmartBadge;
import com.youth.smartbadge.Map.MapActivity;
import com.youth.smartbadge.Map.SettingActivity;
import com.youth.smartbadge.Record.RecordActivity;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {

    private String BASE_URL = "http://112.158.50.42:9080";
    private SharedPreferences appData;
    private RetrofitAPI retrofitAPI;

    private String userID;
    private View btnLogout;
    private Button btnRecord, btnSetting;

    private String smartBadgeID;
    private String updated_at;
    private float longitude;
    private float latitude;
    private boolean nowSafeState;
    private boolean preSafeState;
    private boolean makeState;
    private MapView mapView;
    private MapPoint mapPoint;
    private ViewGroup mapViewContainer;

    private boolean shouldStopLoop;
    private Handler mHandler;
    private Runnable runnable;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;
    private NotificationChannel channel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        UserApiClient.getInstance().accessTokenInfo(new Function2<AccessTokenInfo, Throwable, Unit>() {
            @Override
            public Unit invoke(AccessTokenInfo accessTokenInfo, Throwable throwable) {
                if (accessTokenInfo != null){
                    userID = Long.toString(accessTokenInfo.getId());
                    Log.d("MainTest", userID + " : login되어 있습니다.");
                    Log.d("MainTest", Integer.toString(appData.getInt("smartBadgeID", 0)));

                    shouldStopLoop = false;
                    mHandler = new Handler();
                    runnable = new Runnable() {
                        @Override
                        public void run() {
                            PutMarkerOnMap();
                            if (!shouldStopLoop) {
                                mHandler.postDelayed(this, 3000);
                            }
                        }
                    };
                    mHandler.post(runnable);
                }
                else {
                    Log.d("MainTest", "사용자 정보가 없습니다.");
                    finish();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
                return null;
            }
        });

        // 안심지역 관리 Activity 이동 버튼
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                finish();
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                intent.putExtra("makeState", makeState);
                shouldStopLoop = true;
                startActivity(intent);
            }
        });
        // 음성녹음 Activity 이동 버튼
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RecordActivity.class));
            }
        });
        // 카카오톡 로그아웃 버튼
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserApiClient.getInstance().logout(new Function1<Throwable, Unit>() {
                    @Override
                    public Unit invoke(Throwable throwable) {
                        finish();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        return null;
                    }
                });
            }
        });
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
                    Log.d("TEST make", Boolean.toString(response.body().getMakeState()));
                    longitude = response.body().getLongitude();
                    latitude = response.body().getLatitude();
                    nowSafeState = response.body().getSafeState();
                    makeState = response.body().getMakeState();
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
        Intent intent = new Intent(this, MainActivity.class);
        builder.setContentIntent(PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT));
    }

    public void init(){
        setNotificationManager();
        preSafeState = false;
        // mapView = new MapView(this);
        mapViewContainer = (ViewGroup) findViewById(R.id.view_map_main);
        // mapViewContainer.addView(mapView);

        btnRecord = findViewById(R.id.btn_main_record);
        btnLogout = findViewById(R.id.btn_main_logout);
        btnSetting = findViewById(R.id.btn_main_setting);

        appData = getSharedPreferences("appData", MODE_PRIVATE);
        smartBadgeID = Integer.toString(appData.getInt("smartBadgeID", 0));
        Log.d("InitTest", smartBadgeID);

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
        if (shouldStopLoop){
            shouldStopLoop = false;
            mHandler.post(runnable);
        }
        mapViewContainer.addView(mapView);
        super.onResume();
    }
}