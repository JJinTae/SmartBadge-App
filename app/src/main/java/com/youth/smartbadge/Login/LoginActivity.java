package com.youth.smartbadge.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;
import com.youth.smartbadge.MainActivity;
import com.youth.smartbadge.R;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LoginActivity extends AppCompatActivity{
    private static final String TAG = "LoginActivity";

    private View btnLogin;
    private Button btnStart;
    private ImageView imgProfile;
    private TextView tvNickname, tvUserId;
    private EditText edBadgeNum;

    private SharedPreferences appData;
    private int smartBadgeId, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
        updateKakaoLoginUi();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://112.158.50.42:9080")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);

        // Function2 callback
        Function2<OAuthToken, Throwable, Unit> callback = new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                if (oAuthToken != null){

                }
                if (throwable != null){

                }
                updateKakaoLoginUi();
                return null;
            }
        };

        // 카카오톡 로그인 버튼
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(LoginActivity.this)) {
                    UserApiClient.getInstance().loginWithKakaoTalk(LoginActivity.this, callback);
                } else {
                    UserApiClient.getInstance().loginWithKakaoAccount(LoginActivity.this, callback);
                }
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edBadgeNum.length() == 10){
                    smartBadgeId = Integer.parseInt(edBadgeNum.getText().toString());

                    SharedPreferences.Editor editor = appData.edit();
                    editor.putInt("smartBadgeID", smartBadgeId);
                    editor.apply();

                    // retrofit 들어갈 자리 POST로 서버에 smartBadgeID와 userID 등록
                    SmartBadge smartBadge = new SmartBadge(smartBadgeId, userId);
                    retrofitAPI.postSignUp(smartBadge).enqueue(new Callback<SmartBadge>() {
                        @Override
                        public void onResponse(Call<SmartBadge> call, Response<SmartBadge> response) {
                            if(response.isSuccessful()){
                                SmartBadge data = response.body();
                                Log.d("TEST", "POST 성공");
                                Log.d("TEST", Integer.toString(data.getUserID()));
                            }
                        }

                        @Override
                        public void onFailure(Call<SmartBadge> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });
                    finish();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
            }
        });
    }


    private void updateKakaoLoginUi(){
        // 사용자 정보 가져오기 .me() 호출 시 Access Token refresh
        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
            @Override
            public Unit invoke(User user, Throwable throwable) {
                if (user != null){
                    userId = Integer.parseInt(Long.toString(user.getId()));

                    Log.d(TAG, "invoke : id = " + user.getId());
                    Log.d(TAG, "invoke : nickname = " + user.getKakaoAccount().getProfile().getNickname());

                    Glide.with(imgProfile).load(user.getKakaoAccount().getProfile().getThumbnailImageUrl()).circleCrop().into(imgProfile);
                    tvNickname.setText("User Name : " + user.getKakaoAccount().getProfile().getNickname()); // get Nickname
                    tvUserId.setText("User ID : " + Long.toString(user.getId())); // get UserID

                    edBadgeNum.setVisibility(View.VISIBLE);
                    btnStart.setVisibility(View.VISIBLE);
                    btnLogin.setVisibility(View.GONE);
                } else{
                    imgProfile.setImageBitmap(null);
                    tvNickname.setText(null);
                    tvUserId.setText(null);

                    edBadgeNum.setVisibility(View.GONE);
                    btnStart.setVisibility(View.GONE);
                    btnLogin.setVisibility(View.VISIBLE);
                }
                return null;
            }
        });
    }

    private void init(){
        btnLogin = findViewById(R.id.imageview_login_loginButton);
        btnStart = findViewById(R.id.btn_login_start);
        imgProfile = findViewById(R.id.imageview_login_profile);
        tvNickname = findViewById(R.id.tv_login_nickname);
        tvUserId = findViewById(R.id.tv_login_userId);
        edBadgeNum = findViewById(R.id.ed_login_badgeNum);

        appData = getSharedPreferences("appData", MODE_PRIVATE);
    }
}