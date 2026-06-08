package com.utc2.appreborn.network.dto;

import com.google.gson.annotations.SerializedName;

/** Mapping với AcademicWarningDto của backend */
public class AcademicWarningResponse {

    @SerializedName("warningId")   public Long   warningId;
    @SerializedName("semesterId")  public Long   semesterId;
    @SerializedName("warningType") public String warningType;
    @SerializedName("description") public String description;
    @SerializedName("issuedAt")    public String issuedAt;
    @SerializedName("resolvedAt")  public String resolvedAt;
    @SerializedName("status")      public String status;
    @com.google.gson.annotations.SerializedName("approved")
    public Boolean approved;
}