package com.youth.smartbadge.Login;

import com.google.gson.annotations.SerializedName;

public class SmartBadge {
    @SerializedName("smartBadgeID")
    private int smartBadgeID;
    @SerializedName("userID")
    private int userID;

    public SmartBadge(){}

    public SmartBadge(int smartBadgeID, int userID){
        this.smartBadgeID = smartBadgeID;
        this.userID = userID;
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


}
