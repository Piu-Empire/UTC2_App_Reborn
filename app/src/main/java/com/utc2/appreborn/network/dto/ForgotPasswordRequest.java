package com.utc2.appreborn.network.dto;

import com.google.gson.annotations.SerializedName;

public class ForgotPasswordRequest {
    @SerializedName("email")
    public String email;

    public ForgotPasswordRequest(String email) {
        this.email = email;
    }
}
