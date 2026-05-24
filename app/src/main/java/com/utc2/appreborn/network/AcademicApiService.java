package com.utc2.appreborn.network;

import com.utc2.appreborn.network.dto.AcademicWarningResponse;
import com.utc2.appreborn.network.dto.CourseGradeResponse;
import com.utc2.appreborn.network.dto.LeaderboardEntryResponse;
import com.utc2.appreborn.network.dto.ScholarshipResponse;
import com.utc2.appreborn.network.dto.SemesterResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AcademicApiService {

    @GET("api/v1/academic/semesters")
    Call<ApiResponse<List<SemesterResponse>>> getSemesters();

    @GET("api/v1/academic/grades")
    Call<ApiResponse<List<CourseGradeResponse>>> getGrades(
            @Query("semesterId") Long semesterId
    );

    @GET("api/v1/academic/leaderboard")
    Call<ApiResponse<List<LeaderboardEntryResponse>>> getLeaderboard(
            @Query("semesterId")   Long   semesterId,
            @Query("academicYear") String academicYear
    );

    @GET("api/v1/academic/scholarships")
    Call<ApiResponse<List<ScholarshipResponse>>> getScholarships();

    @GET("api/v1/academic/warnings")
    Call<ApiResponse<List<AcademicWarningResponse>>> getWarnings(
            @Query("semesterId") Long semesterId
    );
}