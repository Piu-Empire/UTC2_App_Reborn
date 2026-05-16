package com.utc2.appreborn.network;

import com.utc2.appreborn.network.dto.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

// ══════════════════════════════════════════════════════════
// 1. AUTH  –  /api/v1/auth
// ══════════════════════════════════════════════════════════
public interface AuthApiService {

    /** POST /api/v1/auth/login  →  { username, password } */
    @POST("api/v1/auth/login")
    Call<ApiResponse<AuthResponse>> login(@Body LoginRequest body);

    /** POST /api/v1/auth/google  →  { idToken } */
    @POST("api/v1/auth/google")
    Call<ApiResponse<AuthResponse>> googleLogin(@Body GoogleAuthRequest body);

    /** POST /api/v1/auth/logout  (cần Bearer token) */
    @POST("api/v1/auth/logout")
    Call<ApiResponse<Void>> logout(@Header("Authorization") String authHeader);
}