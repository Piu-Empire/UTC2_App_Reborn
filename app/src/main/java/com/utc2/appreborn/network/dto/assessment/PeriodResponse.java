package com.utc2.appreborn.network.dto.assessment;

import com.google.gson.annotations.SerializedName;

public class PeriodResponse {
    @SerializedName("periodId")
    public String periodId;

    @SerializedName("label")
    public String label;

    @SerializedName("active")
    public boolean active;
}