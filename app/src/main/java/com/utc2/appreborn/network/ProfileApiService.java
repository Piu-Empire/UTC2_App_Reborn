package com.utc2.appreborn.network;

import com.utc2.appreborn.network.dto.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

// ══════════════════════════════════════════════════════════
// 2. PROFILE  –  /api/v1/profile
// ══════════════════════════════════════════════════════════
public interface ProfileApiService {

    /** GET /api/v1/profile/me  →  ProfileResponse (của mình) */
    @GET("api/v1/profile/me")
    Call<ApiResponse<ProfileResponse>> getMyProfile();

    /** GET /api/v1/profile/student/{studentId}  →  tra cứu bạn khác */
    @GET("api/v1/profile/student/{studentId}")
    Call<ApiResponse<ProfileResponse>> getByStudentId(@Path("studentId") String studentId);

    /** PUT /api/v1/profile/me  →  cập nhật thông tin */
    @PUT("api/v1/profile/me")
    Call<ApiResponse<ProfileResponse>> updateMyProfile(@Body UpdateProfileRequest body);
}
