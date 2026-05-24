package com.utc2.appreborn.network.dto;

import com.google.gson.annotations.SerializedName;

/** Mapping với ScholarshipDto của backend */
public class ScholarshipResponse {

    @SerializedName("scholarshipId")  public Long    scholarshipId;
    @SerializedName("name")           public String  name;
    @SerializedName("organization")   public String  organization;
    @SerializedName("amount")         public Long    amount;
    @SerializedName("unit")           public String  unit;
    @SerializedName("minGpa")         public Double  minGpa;
    @SerializedName("description")    public String  description;
    /** "received" | "not_received" | null */
    @SerializedName("status")         public String  status;
    @SerializedName("receivedAt")     public String  receivedAt;
}