package com.youth.smartbadge.Login;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RetrofitAPI {
    @GET("/users/{smartBadgeID}")
    Call<SmartBadge> getUserData(@Path("smartBadgeID") String id);

    @GET("/location/{smartBadgeID}")
    Call<SmartBadge> getLocationData(@Path("smartBadgeID") String id);

    @GET("/gps-route/{smartBadgeID}")
    Call<List<SmartBadge>> getGpsRouteData(@Path("smartBadgeID") String id);

    @GET("/new-route/{smartBadgeID}")
    Call<List<SmartBadge>> getNewRouteData(@Path("smartBadgeID") String id);

    @POST("/users/")
    Call<SmartBadge> postSignUp(@Body SmartBadge smartBadge);

    @PUT("/change-make/{smartBadgeID}/")
    Call<SmartBadge> putMakeState(@Path("smartBadgeID") String id, @Body SmartBadge smartBadge);

    @DELETE("/gps-route/{smartBadgeID}/")
    Call<Void> deleteGpsRouteData(@Path("smartBadgeID") String id);

    @DELETE("/new-route/{smartBadgeID}")
    Call<Void> deleteNewRouteData(@Path("smartBadgeID") String id);
}
