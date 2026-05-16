package com.utc2.appreborn.network;

import com.utc2.appreborn.network.dto.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;
public interface PublicServicesApiService {

    /** POST /api/v1/services/card-reissue  →  xin cấp lại thẻ */
    @POST("api/v1/services/card-reissue")
    Call<ApiResponse<ServiceRequestResponse>> cardReissue(@Body CardReissueRequest body);

    /** POST /api/v1/services/loan-support  →  xin hỗ trợ vay vốn */
    @POST("api/v1/services/loan-support")
    Call<ApiResponse<ServiceRequestResponse>> loanSupport(@Body LoanSupportRequest body);

    /** POST /api/v1/services/transcript  →  xin bảng điểm */
    @POST("api/v1/services/transcript")
    Call<ApiResponse<ServiceRequestResponse>> transcript(@Body TranscriptRequest body);

    /** POST /api/v1/services/student-confirmation  →  xin giấy xác nhận */
    @POST("api/v1/services/student-confirmation")
    Call<ApiResponse<ServiceRequestResponse>> studentConfirmation(@Body StudentConfirmationRequest body);

    /** GET /api/v1/services/my-requests  →  lịch sử tất cả yêu cầu */
    @GET("api/v1/services/my-requests")
    Call<ApiResponse<List<ServiceRequestResponse>>> myRequests();

    /** GET /api/v1/services/my-requests/{type}
     *  type = TRANSCRIPT | CONFIRMATION_LETTER | CARD_REISSUE | LOAN_SUPPORT */
    @GET("api/v1/services/my-requests/{type}")
    Call<ApiResponse<List<ServiceRequestResponse>>> myRequestsByType(@Path("type") String type);
}
