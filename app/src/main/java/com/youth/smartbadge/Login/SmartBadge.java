package com.youth.smartbadge.Login;

import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;

public class SmartBadge {
    @SerializedName("smartBadgeID")
    private int smartBadgeID;
    @SerializedName("userID")
    private int userID;
    @SerializedName("longitude")
    private float longitude;
    @SerializedName("latitude")
    private float latitude;
    @SerializedName("updated_at")
    private String updated_at;

    public SmartBadge(){}

    // GET userData
    public SmartBadge(int smartBadgeID, int userID){
        this.smartBadgeID = smartBadgeID;
        this.userID = userID;
    }

    // GET locationData
    public SmartBadge(int smartBadgeID, float longitude, float latitude, String updated_at){
        this.smartBadgeID = smartBadgeID;
        this.longitude = longitude;
        this.latitude = latitude;
        this.updated_at = updated_at;
    }


    public int getSmartBadgeID(){
        return smartBadgeID;
    }
    public void setSmartBadgeID(int smartBadgeID){
        this.smartBadgeID = smartBadgeID;
    }

    public int getUserID(){
        return userID;
    }
    public void setUserID(int userID){
        this.userID = userID;
    }

    public float getLongitude() { return longitude; }
    public float getLatitude() { return latitude; }
    public String getUpdate_at() { return updated_at; }



}
