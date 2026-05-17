package com.utc2.appreborn.network.dto;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("accessToken")
    public String accessToken;
    @SerializedName("tokenType")
    public String tokenType;
    @SerializedName("email")
    public String email;
    @SerializedName("studentCode")
    public String studentCode;  // MSSV — dùng cho schedule API
}