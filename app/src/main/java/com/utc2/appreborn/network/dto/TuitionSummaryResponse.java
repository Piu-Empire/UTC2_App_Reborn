package com.utc2.appreborn.network.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TuitionSummaryResponse {
    @SerializedName("studentId")
    public String studentId;
    @SerializedName("fullName")
    public String fullName;
    @SerializedName("totalDebt")
    public Double totalDebt;
    @SerializedName("semesters")
    public List<TuitionResponse> semesters;
}
