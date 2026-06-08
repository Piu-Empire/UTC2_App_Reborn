package com.utc2.appreborn.network.dto;

import com.google.gson.annotations.SerializedName;

public class GmailStatusResponse {
    @SerializedName("linked")
    public boolean linked;

    @SerializedName("expired")
    public boolean expired;

    @SerializedName("expiry")
    public String expiry;
}
