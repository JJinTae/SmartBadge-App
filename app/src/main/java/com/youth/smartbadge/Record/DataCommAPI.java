package com.youth.smartbadge.Record;

import com.youth.smartbadge.Login.SmartBadge;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Multipart;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface DataCommAPI {

    //    @GET("/voice-uploads/")
//    @HTTP(method uploads/", hasBody = true)
////    Call<Record> getFileData(@Body Record record);= "GET", path="/voice-
    @Multipart
    @PUT("/voice-uploads/8/")
    Call<Record> uploadRoadWayFile(@Part("title") RequestBody title,
                                  @Part MultipartBody.Part voiceFile);


    @Multipart
    @PUT("/voice-uploads/9/")
    Call<Record> uploadCrossWalkFile(@Part("title") RequestBody title,
                                     @Part MultipartBody.Part voiceFile);

}