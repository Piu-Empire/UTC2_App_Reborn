package com.utc2.appreborn.network;

import com.utc2.appreborn.network.ApiResponse;
import com.utc2.appreborn.network.dto.assessment.ExternalAssessmentResponse;
import com.utc2.appreborn.network.dto.assessment.PeriodResponse;
import com.utc2.appreborn.network.dto.assessment.SaveAdvisorRequest;
import com.utc2.appreborn.network.dto.assessment.SaveStudentRequest;
import com.utc2.appreborn.network.dto.assessment.StudentAssessmentResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AssessmentApiService {

    // Danh sách học kỳ
    @GET("api/v1/assessment/periods")
    Call<ApiResponse<List<PeriodResponse>>> getPeriods();

    // SV lưu tự đánh giá
    @POST("api/v1/assessment/student")
    Call<ApiResponse<Void>> saveStudentAssessment(@Body SaveStudentRequest body);

    // SV xem lại dữ liệu đã lưu
    @GET("api/v1/assessment/student")
    Call<ApiResponse<StudentAssessmentResponse>> getStudentAssessment(@Query("periodId") String periodId);

    // SV lưu đánh giá CVHT
    @POST("api/v1/assessment/advisor")
    Call<ApiResponse<Void>> saveAdvisorAssessment(@Body SaveAdvisorRequest body);

    // App đọc điểm readonly Khoa/Lớp/Trường
    @GET("api/v1/assessment/external")
    Call<ApiResponse<ExternalAssessmentResponse>> getExternalAssessment(@Query("periodId") String periodId);
}