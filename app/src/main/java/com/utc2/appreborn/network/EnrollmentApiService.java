package com.utc2.appreborn.network;

import com.utc2.appreborn.network.dto.CourseItemResponse;
import com.utc2.appreborn.network.dto.EnrollmentResponse;
import com.utc2.appreborn.network.dto.SemesterResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface EnrollmentApiService {

    /**
     * GET /api/v1/enrollment/my
     * Danh sách toàn bộ môn học + điểm của sinh viên đang đăng nhập.
     */
    @GET("api/v1/enrollment/my")
    Call<ApiResponse<List<EnrollmentResponse>>> getMyEnrollments();

    /**
     * GET /api/v1/enrollment/courses
     * Danh sách học phần có thể đăng ký (từ server, có courseId thật).
     */
    @GET("api/v1/enrollment/courses")
    Call<ApiResponse<List<CourseItemResponse>>> getAvailableCourses();

    /**
     * GET /api/v1/academic/semesters  (hoặc endpoint lấy học kỳ hiện tại)
     * Dùng để lấy semesterId hợp lệ khi đăng ký.
     */
    @GET("api/v1/academic/semesters")
    Call<ApiResponse<List<SemesterResponse>>> getSemesters();

    /**
     * POST /api/v1/enrollment
     * Body: { "courseId": 1, "semesterId": 1 }
     * Đăng ký học phần — tạo enrollment + tự động tạo fee record trên server.
     */
    @POST("api/v1/enrollment")
    Call<ApiResponse<EnrollmentResponse>> enroll(@Body Map<String, Long> body);

    /**
     * DELETE /api/v1/enrollment/{enrollmentId}
     * Hủy đăng ký học phần.
     */
    @DELETE("api/v1/enrollment/{enrollmentId}")
    Call<ApiResponse<Void>> cancelEnrollment(@Path("enrollmentId") long enrollmentId);
}