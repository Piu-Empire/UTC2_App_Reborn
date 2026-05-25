package com.utc2.appreborn.network.dto;

import com.google.gson.annotations.SerializedName;

/** Mapping với LeaderboardEntryDto của backend */
public class LeaderboardEntryResponse {

    @SerializedName("rank")          public Integer rank;
    @SerializedName("studentCode")   public String  studentCode;
    @SerializedName("fullName")      public String  fullName;
    @SerializedName("initials")      public String  initials;
    @SerializedName("totalCredits")  public Integer totalCredits;
    @SerializedName("gpa")           public Double  gpa;
    @SerializedName("isCurrentUser") public Boolean isCurrentUser;
}