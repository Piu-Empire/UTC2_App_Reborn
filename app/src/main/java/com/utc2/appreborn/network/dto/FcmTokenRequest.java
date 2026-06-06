package com.utc2.appreborn.network.dto;

import com.google.gson.annotations.SerializedName;

public class FcmTokenRequest {
    @SerializedName("fcmToken")
    public String fcmToken;
    
    public FcmTokenRequest(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
