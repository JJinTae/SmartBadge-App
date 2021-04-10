package com.youth.smartbadge.Login;

import android.app.Application;

import com.kakao.sdk.common.KakaoSdk;
import com.youth.smartbadge.BuildConfig;

public class KakaoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVEAPP_KEY);
    }
}
