package com.utc2.appreborn.network.dto;

import com.google.gson.annotations.SerializedName;

/** Mapping với SemesterDto của backend */
public class SemesterResponse {

    @SerializedName("semesterId")    public Long    semesterId;
    @SerializedName("semesterName") public String  semesterName;
    @SerializedName("academicYear") public String  academicYear;
    @SerializedName("semesterNumber") public Integer semesterNumber;
    @SerializedName("startDate")    public String  startDate;
    @SerializedName("endDate")      public String  endDate;
    @SerializedName("gpa")          public Double  gpa;
    @SerializedName("totalCredits") public Integer totalCredits;
    @SerializedName("passedCredits") public Integer passedCredits;
}