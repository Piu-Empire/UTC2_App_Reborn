package com.utc2.appreborn.network.dto;

import com.google.gson.annotations.SerializedName;

/** Mapping với CourseGradeDto của backend */
public class CourseGradeResponse {

    @SerializedName("enrollmentId")    public Long    enrollmentId;
    @SerializedName("courseCode")      public String  courseCode;
    @SerializedName("courseName")      public String  courseName;
    @SerializedName("credits")         public Integer credits;
    @SerializedName("midtermScore")    public Double  midtermScore;
    @SerializedName("finalScore")      public Double  finalScore;
    @SerializedName("assignmentScore") public Double  assignmentScore;
    @SerializedName("totalScore")      public Double  totalScore;
    @SerializedName("letterGrade")     public String  letterGrade;
    @SerializedName("gradePoint")      public Double  gradePoint;
    @SerializedName("isPassed")        public Boolean isPassed;
    @SerializedName("status")          public String  status;
    @SerializedName("semesterId")      public Long    semesterId;
    @SerializedName("semesterName")    public String  semesterName;
    @SerializedName("academicYear")    public String  academicYear;
}