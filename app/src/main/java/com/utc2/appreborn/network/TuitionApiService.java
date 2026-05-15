package com.utc2.appreborn.network;

import com.utc2.appreborn.network.dto.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;
public interface TuitionApiService {

    /** GET /api/v1/tuition/summary  →  tổng nợ + danh sách kỳ */
    @GET("api/v1/tuition/summary")
    Call<ApiResponse<TuitionSummaryResponse>> getSummary();

    /** GET /api/v1/tuition/history  →  lịch sử học phí */
    @GET("api/v1/tuition/history")
    Call<ApiResponse<List<TuitionResponse>>> getHistory();

    /** GET /api/v1/tuition/semester/{semester}  →  học phí 1 kỳ */
    @GET("api/v1/tuition/semester/{semester}")
    Call<ApiResponse<TuitionResponse>> getBySemester(@Path("semester") String semester);
}