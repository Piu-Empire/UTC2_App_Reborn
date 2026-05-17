package com.utc2.appreborn.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Public endpoints — không cần token.
 *
 *  GET /api/v1/schedule/meta?studentCode=MSSV  — kiểm tra timestamp (nhẹ)
 *  GET /api/v1/schedule/file?studentCode=MSSV  — tải toàn bộ JSON lịch
 */
public interface ScheduleApiService {

    @GET("api/v1/schedule/meta")
    Call<ApiResponse<ScheduleMetaResponse>> getMeta(@Query("studentCode") String studentCode);

    @GET("api/v1/schedule/file")
    Call<ApiResponse<ScheduleFileResponse>> getScheduleFile(@Query("studentCode") String studentCode);

    // ── Inner DTOs (mirror server DTOs) ──────────────────────

    class ScheduleMetaResponse {
        public String studentCode;
        public String lastUpdated; // "yyyy-MM-dd'T'HH:mm:ss"
    }

    class ScheduleFileResponse {
        public String studentCode;
        public String lastUpdated;
        public java.util.List<ScheduleItemResponse> schedules;
    }

    class ScheduleItemResponse {
        public String subjectCode;
        public String subjectName;
        public String type;
        public String lecturer;
        public int dayOfWeek;
        public int startPeriod;
        public int endPeriod;
        public String startTime;
        public String endTime;
        public String startDate;
        public String endDate;
        public String room;
        public String building;
    }
}
