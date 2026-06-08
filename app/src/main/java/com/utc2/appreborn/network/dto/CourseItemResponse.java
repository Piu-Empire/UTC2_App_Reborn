package com.utc2.appreborn.network.dto;

import com.google.gson.annotations.SerializedName;

/** Mapping với backend CourseItemDto — GET /api/v1/enrollment/courses */
public class CourseItemResponse {
    @SerializedName("courseId")        public Long    courseId;
    @SerializedName("courseCode")      public String  courseCode;
    @SerializedName("courseName")      public String  courseName;
    @SerializedName("credits")         public Integer credits;
    @SerializedName("theoryHours")     public Integer theoryHours;
    @SerializedName("practiceHours")   public Integer practiceHours;
    @SerializedName("department")      public String  department;
    @SerializedName("description")     public String  description;
    @SerializedName("alreadyEnrolled") public Boolean alreadyEnrolled;
}