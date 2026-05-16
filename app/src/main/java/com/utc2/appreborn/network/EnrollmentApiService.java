package com.utc2.appreborn.network;

import com.utc2.appreborn.network.dto.EnrollmentResponse;
import retrofit2.Call;
import retrofit2.http.GET;

import java.util.List;

public interface EnrollmentApiService {

    /**
     * GET /api/v1/enrollment/my
     * Trả về danh sách toàn bộ môn học + điểm của sinh viên đang đăng nhập.
     * Header Authorization: Bearer <token> được đính kèm tự động bởi ApiClient.
     */
    @GET("api/v1/enrollment/my")
    Call<ApiResponse<List<EnrollmentResponse>>> getMyEnrollments();
}