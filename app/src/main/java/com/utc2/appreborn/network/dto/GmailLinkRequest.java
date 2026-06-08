package com.utc2.appreborn.network.dto;

import com.google.gson.annotations.SerializedName;

public class GmailLinkRequest {
    @SerializedName("googleAccessToken")
    public String googleAccessToken;
    
    public GmailLinkRequest(String googleAccessToken) {
        this.googleAccessToken = googleAccessToken;
    }
}
