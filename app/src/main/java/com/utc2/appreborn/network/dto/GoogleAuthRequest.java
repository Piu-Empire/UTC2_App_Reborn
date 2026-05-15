package com.utc2.appreborn.network.dto;

import com.google.gson.annotations.SerializedName;

public class GoogleAuthRequest {
    @SerializedName("idToken")
    public String idToken;

    public GoogleAuthRequest(String idToken) {
        this.idToken = idToken;
    }
}
