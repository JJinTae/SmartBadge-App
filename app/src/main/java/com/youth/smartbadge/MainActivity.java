package com.youth.smartbadge;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.AccessTokenInfo;
import com.youth.smartbadge.Login.LoginActivity;
import com.youth.smartbadge.Map.MapActivity;
import com.youth.smartbadge.Record.RecordActivity;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;


public class MainActivity extends AppCompatActivity {

    private String userID;
    private View btnLogout, btnMap;
    private Button btnRecord;

    private SharedPreferences appData;

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
    public void init(){
        btnRecord = findViewById(R.id.btn_main_record);
        btnLogout = findViewById(R.id.btn_main_logout);
        btnMap = findViewById(R.id.btn_main_map);
        appData = getSharedPreferences("appData", MODE_PRIVATE);
    }
}