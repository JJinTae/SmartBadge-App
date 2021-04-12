package com.youth.smartbadge.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.camera2.params.SessionConfiguration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApi;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;
import com.youth.smartbadge.R;

import java.text.DateFormat;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;


public class LoginActivity extends AppCompatActivity{
    private static final String TAG = "LoginActivity";


    private View loginButton, logoutButton;
    private TextView nickName, uid;
    private ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
        updateKakaoLoginUi();

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
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(LoginActivity.this)) {
                    UserApiClient.getInstance().loginWithKakaoTalk(LoginActivity.this, callback);
                } else {
                    UserApiClient.getInstance().loginWithKakaoAccount(LoginActivity.this, callback);
                }
            }
        });

        // 카카오톡 로그아웃 버튼
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserApiClient.getInstance().logout(new Function1<Throwable, Unit>() {
                    @Override
                    public Unit invoke(Throwable throwable) {
                        updateKakaoLoginUi();
                        return null;
                    }
                });
            }
        });
    }



    private void updateKakaoLoginUi(){

        // 사용자 정보 가져오기 .me() 호출 시 Access Token refresh
        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
            @Override
            public Unit invoke(User user, Throwable throwable) {
                if (user != null){

                    Log.d(TAG, "invoke : id = " + user.getId());
                    Log.d(TAG, "invoke : nickname = " + user.getKakaoAccount().getProfile().getNickname());


                    nickName.setText(user.getKakaoAccount().getProfile().getNickname()); // get Nickname
                    uid.setText(Long.toString(user.getId())); // get UserID
                    Glide.with(profileImage).load(user.getKakaoAccount().getProfile().getThumbnailImageUrl()).circleCrop().into(profileImage);


                    loginButton.setVisibility(View.GONE);
                    logoutButton.setVisibility(View.VISIBLE);
                } else{
                    nickName.setText(null);
                    uid.setText(null);
                    profileImage.setImageBitmap(null);

                    loginButton.setVisibility(View.VISIBLE);
                    logoutButton.setVisibility(View.GONE);

                }
                return null;
            }
        });
    }

    private void init(){
        loginButton = findViewById(R.id.login);
        logoutButton = findViewById(R.id.logout);
        nickName = findViewById(R.id.nickname);
        uid = findViewById(R.id.uid);
        profileImage = findViewById(R.id.profile);
    }
}