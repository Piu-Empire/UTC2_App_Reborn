package com.utc2.appreborn.network;

import com.utc2.appreborn.network.dto.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;
public interface TuitionApiService {

    /** GET /api/v1/tuition/summary  →  tổng nợ + danh sách kỳ */
    @GET("api/v1/tuition/summary")
    Call<ApiResponse<TuitionSummaryResponse>> getSummary();

    /** GET /api/v1/tuition/history  →  toàn bộ lịch sử (cả chưa đóng) */
    @GET("api/v1/tuition/history")
    Call<ApiResponse<List<TuitionResponse>>> getHistory();

    /** GET /api/v1/tuition/paid  →  chỉ các kỳ đã đóng đủ — dùng cho Invoice screen */
    @GET("api/v1/tuition/paid")
    Call<ApiResponse<List<TuitionResponse>>> getPaid();

    /** GET /api/v1/tuition/semester/{semester}  →  học phí 1 kỳ */
    @GET("api/v1/tuition/semester/{semester}")
    Call<ApiResponse<TuitionResponse>> getBySemester(@Path("semester") String semester);

    /** POST /api/v1/tuition/pay/{semesterId}  →  đóng toàn bộ học phí kỳ đó */
    @POST("api/v1/tuition/pay/{semesterId}")
    Call<ApiResponse<TuitionResponse>> pay(
            @Path("semesterId") long semesterId,
            @Query("paymentMethod") String paymentMethod);
}