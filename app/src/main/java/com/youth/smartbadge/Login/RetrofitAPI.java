package com.youth.smartbadge.Login;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RetrofitAPI {
    @GET("/users/{smartBadgeID}")
    Call<SmartBadge> getUserData(@Path("smartBadgeID") String id);

    @GET("/location/{smartBadgeID}")
    Call<SmartBadge> getLocationData(@Path("smartBadgeID") String id);

    @POST("/users/")
    Call<SmartBadge> postSignUp(@Body SmartBadge smartBadge);
}
