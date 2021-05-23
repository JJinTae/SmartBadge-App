package com.youth.smartbadge;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.AccessTokenInfo;
import com.youth.smartbadge.Login.LoginActivity;
import com.youth.smartbadge.Map.MapActivity;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;


public class MainActivity extends AppCompatActivity {

    private String userID;
    private View btnLogout, btnMap;

    private SharedPreferences appData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

//        GET 임시 저장
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://112.158.50.42:9080")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
//        retrofitAPI.getData("1513441517").enqueue(new Callback<SmartBadge>() {
//            @Override
//            public void onResponse(Call<SmartBadge> call, Response<SmartBadge> response) {
//                if(response.isSuccessful()){
//                    Log.d("TEST", Integer.toString(response.body().getUserID()));
//                }
//            }
//            @Override
//            public void onFailure(Call<SmartBadge> call, Throwable t) {
//                t.printStackTrace();
//            }
//        });

        UserApiClient.getInstance().accessTokenInfo(new Function2<AccessTokenInfo, Throwable, Unit>() {
            @Override
            public Unit invoke(AccessTokenInfo accessTokenInfo, Throwable throwable) {
                if (accessTokenInfo != null){
                    userID = Long.toString(accessTokenInfo.getId());
                    Log.d("MainTest", userID + " : login되어 있습니다.");
                    Log.d("MainTest", Integer.toString(appData.getInt("smartBadgeID", 0)));
                }
                else {
                    Log.d("MainTest", "사용자 정보가 없습니다.");
                    finish();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
                return null;
            }
        });

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MapActivity.class));
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
    public void init(){
        btnLogout = findViewById(R.id.btn_main_logout);
        btnMap = findViewById(R.id.btn_main_map);
        appData = getSharedPreferences("appData", MODE_PRIVATE);
    }
}
