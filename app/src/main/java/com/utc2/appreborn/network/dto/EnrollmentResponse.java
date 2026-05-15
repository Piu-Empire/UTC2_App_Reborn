package com.utc2.appreborn.network.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Mapping với backend EnrollmentResponse.
 * Các field khớp 1-1 với JSON backend trả về.
 */
public class EnrollmentResponse {

    @SerializedName("courseCode")
    public String  courseCode;

    @SerializedName("courseName")
    public String  courseName;

    @SerializedName("credits")
    public Integer credits;

    @SerializedName("semesterNumber")
    public Integer semesterNumber;

    /** null nếu chưa có điểm — hiển thị "N/A" ở UI */
    @SerializedName("totalScore")
    public String  totalScore;

    @SerializedName("letterGrade")
    public String  letterGrade;

    @SerializedName("gradePoint")
    public Double  gradePoint;

    @SerializedName("isPassed")
    public Boolean isPassed;
}