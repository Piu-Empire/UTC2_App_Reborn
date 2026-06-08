package com.utc2.appreborn.network;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

/**
 * ApiClient – Singleton Retrofit
 *
 * FIX WARN 4: Cache một OkHttpClient dùng chung và một Retrofit instance
 * cho mỗi loại (authed / public). Tránh tạo mới thread pool mỗi request.
 *
 * build.gradle (app):
 *   implementation 'com.squareup.retrofit2:retrofit:2.9.0'
 *   implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
 *   implementation 'com.squareup.okhttp3:logging-interceptor:4.12.0'
 */
public class ApiClient {

    // ⚠️ Đổi BASE_URL sang địa chỉ server thực tế của bạn
    private static final String BASE_URL = "http://10.11.2.61:8080/"; // emulator trỏ localhost
    // private static final String BASE_URL = "https://api.yourdomain.com/";

    // Gson lenient: cho phép parse JSON number → String field (BigDecimal từ backend)
    private static final Gson LENIENT_GSON = new GsonBuilder()
            .setLenient()
            .create();

    // FIX WARN 4: dùng chung một OkHttpClient (quản lý thread pool, connection pool)
    private static final OkHttpClient SHARED_HTTP_CLIENT = buildHttpClient();

    // Cache 2 instance: một public (không token) và một authed (có token hiện tại)
    private static Retrofit publicInstance;
    private static Retrofit authedInstance;
    private static String   cachedToken;

    private static OkHttpClient buildHttpClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build();
    }

    /**
     * Lấy Retrofit instance có Bearer token.
     * Tạo mới chỉ khi token thay đổi.
     */
    public static synchronized Retrofit getInstance(String token) {
        if (token == null || token.isEmpty()) {
            return getPublicInstance();
        }
        if (authedInstance == null || !token.equals(cachedToken)) {
            cachedToken = token;
            OkHttpClient authedClient = SHARED_HTTP_CLIENT.newBuilder()
                    .addInterceptor(chain -> {
                        Request req = chain.request().newBuilder()
                                .header("Content-Type", "application/json")
                                .header("Authorization", "Bearer " + token)
                                .build();
                        return chain.proceed(req);
                    })
                    .build();
            authedInstance = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(authedClient)
                    .addConverterFactory(GsonConverterFactory.create(LENIENT_GSON))
                    .build();
        }
        return authedInstance;
    }

    /** Retrofit không cần token (login, register, google) */
    public static synchronized Retrofit getPublicInstance() {
        if (publicInstance == null) {
            OkHttpClient publicClient = SHARED_HTTP_CLIENT.newBuilder()
                    .addInterceptor(chain -> {
                        Request req = chain.request().newBuilder()
                                .header("Content-Type", "application/json")
                                .build();
                        return chain.proceed(req);
                    })
                    .build();
            publicInstance = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(publicClient)
                    .addConverterFactory(GsonConverterFactory.create(LENIENT_GSON))
                    .build();
        }
        return publicInstance;
    }

    /** Gọi khi logout để reset cache token */
    public static synchronized void clearAuth() {
        authedInstance = null;
        cachedToken    = null;
    }
}