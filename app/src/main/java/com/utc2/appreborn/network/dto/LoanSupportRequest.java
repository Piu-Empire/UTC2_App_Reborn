package com.utc2.appreborn.network.dto;

import com.google.gson.annotations.SerializedName;

public class LoanSupportRequest {
    @SerializedName("loanAmount")
    public String loanAmount;
    @SerializedName("loanReason")
    public String loanReason;
    @SerializedName("phoneNumber")
    public String phoneNumber;

    public LoanSupportRequest(String loanAmount, String loanReason, String phoneNumber) {
        this.loanAmount = loanAmount;
        this.loanReason = loanReason;
        this.phoneNumber = phoneNumber;
    }
}
