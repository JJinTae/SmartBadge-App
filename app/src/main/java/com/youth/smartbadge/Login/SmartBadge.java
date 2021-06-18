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
    @SerializedName("safeState")
    private boolean safeState;
    @SerializedName("makeState")
    private boolean makeState;
    @SerializedName("updated_at")
    private String updated_at;
    @SerializedName("makeNewZoneState")
    private boolean makeNewZoneState;

    public SmartBadge(){}

    // POST userData
    public SmartBadge(int smartBadgeID, int userID){
        this.smartBadgeID = smartBadgeID;
        this.userID = userID;
    }

    public SmartBadge(int smartBadgeID, boolean makeState, boolean makeNewZoneState){
        this.smartBadgeID = smartBadgeID;
        this.makeState = makeState;
        this.makeNewZoneState = makeNewZoneState;
    }

    // GET locationData
    public SmartBadge(int smartBadgeID, float longitude, float latitude, boolean safeState, boolean makeState, String updated_at){
        this.smartBadgeID = smartBadgeID;
        this.longitude = longitude;
        this.latitude = latitude;
        this.safeState = safeState;
        this.makeState = makeState;
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
    public boolean getSafeState() { return safeState; }
    public boolean getMakeState() { return makeState; }
    public String getUpdate_at() { return updated_at; }

}
