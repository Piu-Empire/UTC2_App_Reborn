package com.utc2.appreborn.network;

import com.utc2.appreborn.network.dto.FeedbackRequest;
import com.utc2.appreborn.network.dto.FeedbackResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * API gửi phản hồi / báo lỗi từ sinh viên lên backend.
 * Base URL: ApiClient.getInstance(token)
 */
public interface InteractionApiService {

    /** POST /api/v1/interaction/feedback  →  gửi phản hồi mới */
    @POST("api/v1/interaction/feedback")
    Call<ApiResponse<FeedbackResponse>> sendFeedback(@Body FeedbackRequest body);

    /** GET /api/v1/interaction/feedback/my  →  lịch sử phản hồi của sinh viên */
    @GET("api/v1/interaction/feedback/my")
    Call<ApiResponse<List<FeedbackResponse>>> myFeedbacks();
}