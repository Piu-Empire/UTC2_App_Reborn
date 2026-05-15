package com.utc2.appreborn.network.dto;

import com.google.gson.annotations.SerializedName;

public class CardReissueRequest {
    @SerializedName("reason")
    public String reason;

    public CardReissueRequest(String reason) {
        this.reason = reason;
    }
}
