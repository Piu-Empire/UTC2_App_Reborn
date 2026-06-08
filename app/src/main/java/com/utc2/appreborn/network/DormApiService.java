package com.utc2.appreborn.network;

import com.utc2.appreborn.network.dto.DormRegistrationResponse;
import com.utc2.appreborn.network.dto.DormRoomResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface DormApiService {

    /**
     * GET /api/v1/dormitory/rooms
     * Danh sách tất cả phòng KTX (thay thế dữ liệu hardcode local).
     */
    @GET("api/v1/dormitory/rooms")
    Call<ApiResponse<List<DormRoomResponse>>> getRooms();

    /**
     * GET /api/v1/dormitory/my
     * Lịch sử đăng ký KTX của sinh viên đang đăng nhập.
     */
    @GET("api/v1/dormitory/my")
    Call<ApiResponse<List<DormRegistrationResponse>>> getMyRegistrations();

    /**
     * POST /api/v1/dormitory/register
     * Body: { "roomId": 1, "months": 8 }
     * Đăng ký phòng KTX — tạo dormRegistration + tự động tạo fee record trên server.
     */
    @POST("api/v1/dormitory/register")
    Call<ApiResponse<DormRegistrationResponse>> register(@Body Map<String, Object> body);

    /**
     * POST /api/v1/dormitory/pay/{dormRegId}
     * Thanh toán phí KTX 1 lần đủ.
     */
    @POST("api/v1/dormitory/pay/{dormRegId}")
    Call<ApiResponse<DormRegistrationResponse>> pay(@Path("dormRegId") long dormRegId);

    /**
     * DELETE /api/v1/dormitory/{dormRegId}
     * Hủy đăng ký KTX.
     */
    @DELETE("api/v1/dormitory/{dormRegId}")
    Call<ApiResponse<Void>> cancel(@Path("dormRegId") long dormRegId);
}