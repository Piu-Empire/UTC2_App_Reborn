package com.utc2.appreborn.network;

import com.utc2.appreborn.network.dto.DormRegistrationResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface DormApiService {

    /**
     * GET /api/v1/dormitory/my
     * Lịch sử đăng ký KTX của sinh viên đang đăng nhập.
     */
    @GET("api/v1/dormitory/my")
    Call<ApiResponse<List<DormRegistrationResponse>>> getMyRegistrations();

    /**
     * POST /api/v1/dormitory/pay/{dormRegId}
     * Thanh toán phí KTX 1 lần đủ.
     */
    @POST("api/v1/dormitory/pay/{dormRegId}")
    Call<ApiResponse<DormRegistrationResponse>> pay(@Path("dormRegId") long dormRegId);
}