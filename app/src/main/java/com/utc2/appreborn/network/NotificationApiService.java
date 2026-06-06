package com.utc2.appreborn.network;

import com.utc2.appreborn.network.dto.FcmTokenRequest;
import com.utc2.appreborn.network.dto.GmailLinkRequest;
import com.utc2.appreborn.network.dto.GmailMessageResponse;
import com.utc2.appreborn.network.dto.GmailStatusResponse;
import com.utc2.appreborn.network.dto.NotificationResponse;
import com.utc2.appreborn.network.dto.NotificationSettingRequest;
import com.utc2.appreborn.network.dto.NotificationSettingResponse;
import com.utc2.appreborn.network.dto.PageResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * NotificationApiService
 * ─────────────────────────────────────────────────────────────────
 * Tất cả endpoints yêu cầu Bearer token (dùng ApiClient.getInstance(token)).
 * OTP endpoints dùng ApiClient.getPublicInstance() (không cần token).
 */
public interface NotificationApiService {

    // ── System Notifications ──────────────────────────────────────

    /** GET /api/v1/notifications?page=0&size=20 */
    @GET("api/v1/notifications")
    Call<ApiResponse<PageResponse<NotificationResponse>>> getNotifications(
            @Query("page") int page,
            @Query("size") int size
    );

    /** GET /api/v1/notifications/unread-count */
    @GET("api/v1/notifications/unread-count")
    Call<ApiResponse<Map<String, Long>>> getUnreadCount();

    /** PATCH /api/v1/notifications/{id}/read */
    @PATCH("api/v1/notifications/{id}/read")
    Call<ApiResponse<Void>> markAsRead(@Path("id") long id);

    /** PATCH /api/v1/notifications/read-all */
    @PATCH("api/v1/notifications/read-all")
    Call<ApiResponse<Map<String, Integer>>> markAllRead();

    /** DELETE /api/v1/notifications/{id} */
    @DELETE("api/v1/notifications/{id}")
    Call<ApiResponse<Void>> deleteNotification(@Path("id") long id);

    // ── Settings ──────────────────────────────────────────────────

    /** GET /api/v1/notifications/settings */
    @GET("api/v1/notifications/settings")
    Call<ApiResponse<NotificationSettingResponse>> getSettings();

    /** PUT /api/v1/notifications/settings */
    @PUT("api/v1/notifications/settings")
    Call<ApiResponse<NotificationSettingResponse>> updateSettings(@Body NotificationSettingRequest request);

    // ── FCM Token ─────────────────────────────────────────────────

    /** POST /api/v1/notifications/fcm/token */
    @POST("api/v1/notifications/fcm/token")
    Call<ApiResponse<Void>> registerFcmToken(@Body FcmTokenRequest request);

    /** DELETE /api/v1/notifications/fcm/token */
    @DELETE("api/v1/notifications/fcm/token")
    Call<ApiResponse<Void>> removeFcmToken();

    // ── Gmail Proxy ───────────────────────────────────────────────

    /** POST /api/v1/notifications/gmail/link */
    @POST("api/v1/notifications/gmail/link")
    Call<ApiResponse<Void>> linkGmail(@Body GmailLinkRequest request);

    /** DELETE /api/v1/notifications/gmail/unlink */
    @DELETE("api/v1/notifications/gmail/unlink")
    Call<ApiResponse<Void>> unlinkGmail();

    /** GET /api/v1/notifications/gmail/status */
    @GET("api/v1/notifications/gmail/status")
    Call<ApiResponse<GmailStatusResponse>> getGmailStatus();

    /** GET /api/v1/notifications/gmail/inbox */
    @GET("api/v1/notifications/gmail/inbox")
    Call<ApiResponse<List<GmailMessageResponse>>> getGmailInbox();
}
