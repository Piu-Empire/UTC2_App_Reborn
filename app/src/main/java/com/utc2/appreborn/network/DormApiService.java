package com.utc2.appreborn.network;

import com.utc2.appreborn.network.dto.DormRegistrationResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface DormApiService {

    /**
     * GET /api/v1/dormitory/my
     * Lịch sử đăng ký KTX của sinh viên đang đăng nhập.
     * Trả DormRegistrationDto từ backend — có totalFee, paidStatus, roomCode, building.
     */
    @GET("api/v1/dormitory/my")
    Call<ApiResponse<List<DormRegistrationResponse>>> getMyRegistrations();
}