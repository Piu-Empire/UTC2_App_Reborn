package com.utc2.appreborn.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SessionManager
 * ──────────────────────────────────────────────────────────────
 * Lưu session sau khi đăng nhập thành công.
 *
 * Mapping với TABLE USER (MySQL schema):
 *   user_id       → KEY_USER_ID       (long)
 *   email         → KEY_EMAIL         (String)
 *   auth_provider → KEY_LOGIN_TYPE    (String)  "EMAIL" | "GOOGLE"
 *
 * Mapping với TABLE STUDENT_PROFILE:
 *   student_code  → KEY_STUDENT_CODE  (String)  MSSV
 *
 * Mapping với TABLE USER_PROFILE:
 *   full_name     → KEY_FULL_NAME     (String)  Họ và tên hiển thị
 */
public class SessionManager {
    private static final String PREF_NAME        = "AppRebornSession";
    private static final String KEY_TOKEN        = "auth_token";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_LOGIN_TYPE   = "login_type";   // auth_provider
    private static final String KEY_USER_ID      = "user_id";      // USER.user_id (BIGINT)
    private static final String KEY_EMAIL        = "email";         // USER.email
    private static final String KEY_STUDENT_CODE = "student_code"; // STUDENT_PROFILE.student_code (MSSV)
    private static final String KEY_FULL_NAME    = "full_name";    // USER_PROFILE.full_name

    private static SessionManager instance;
    private final SharedPreferences sharedPreferences;

    private SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Tạo session sau khi đăng nhập.
     *
     * @param token       JWT / Google IdToken
     * @param loginType   "EMAIL" hoặc "GOOGLE" — tương ứng auth_provider trong DB
     * @param email       USER.email
     * @param userId      USER.user_id (BIGINT); truyền -1 nếu chưa có (guest/skip)
     * @param studentCode STUDENT_PROFILE.student_code (MSSV); null nếu chưa biết
     */
    public void createLoginSession(String token, String loginType,
                                   String email, long userId, String studentCode) {
        sharedPreferences.edit()
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .putString(KEY_TOKEN, token)
                .putString(KEY_LOGIN_TYPE, loginType)
                .putString(KEY_EMAIL, email)
                .putLong(KEY_USER_ID, userId)
                .putString(KEY_STUDENT_CODE, studentCode != null ? studentCode : "")
                .apply();
    }

    /**
     * Overload tương thích ngược cho code cũ dùng (token, type, studentId).
     * studentId cũ được map vào KEY_STUDENT_CODE; userId set -1 (chưa xác định).
     *
     * @deprecated Dùng {@link #createLoginSession(String, String, String, long, String)} thay thế.
     */
    @Deprecated
    public void createLoginSession(String token, String type, String studentId) {
        createLoginSession(token, type, studentId, -1L, studentId);
    }

    // ── Getters ───────────────────────────────────────────────

    /** USER.user_id — trả về -1 nếu chưa có (guest). */
    public long getUserId() {
        return sharedPreferences.getLong(KEY_USER_ID, -1L);
    }

    /** USER.email */
    public String getEmail() {
        return sharedPreferences.getString(KEY_EMAIL, "");
    }

    /** STUDENT_PROFILE.student_code (MSSV) */
    public String getStudentCode() {
        return sharedPreferences.getString(KEY_STUDENT_CODE, "");
    }

    public String getAuthToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /** auth_provider: "EMAIL" | "GOOGLE" */
    public String getLoginType() {
        return sharedPreferences.getString(KEY_LOGIN_TYPE, "");
    }

    /**
     * Lưu profile sinh viên sau khi fetch từ GET /api/v1/profile/me.
     * Gọi sau khi đăng nhập thành công và nhận response từ server.
     */
    public void saveProfile(String fullName, String studentCode) {
        sharedPreferences.edit()
                .putString(KEY_FULL_NAME,    fullName    != null ? fullName    : "")
                .putString(KEY_STUDENT_CODE, studentCode != null ? studentCode : "")
                .apply();
    }

    /** USER_PROFILE.full_name — trả về "" nếu chưa fetch về */
    public String getFullName() {
        return sharedPreferences.getString(KEY_FULL_NAME, "");
    }

    /** Alias cho getFullName() — tương thích với code cũ */
    public String getCachedFullName() {
        return getFullName();
    }

    public void logout() {
        sharedPreferences.edit().clear().apply();
    }
}