package com.utc2.appreborn.network.dto;

import com.google.gson.annotations.SerializedName;

public class StudentConfirmationRequest {
    @SerializedName("purpose")
    public String purpose;
    @SerializedName("quantity")
    public int quantity;

    public StudentConfirmationRequest(String purpose, int quantity) {
        this.purpose = purpose;
        this.quantity = quantity;
    }
}
