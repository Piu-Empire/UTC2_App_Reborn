package com.utc2.appreborn.network.dto;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("studentCode")
    public String studentCode;
    @SerializedName("password")
    public String password;

    public LoginRequest(String studentCode, String password) {
        this.studentCode = studentCode;
        this.password = password;
    }
}