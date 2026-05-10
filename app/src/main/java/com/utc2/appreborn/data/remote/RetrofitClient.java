package com.utc2.appreborn.data.remote;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * RetrofitClient v2 — FORCE RESET SINGLETON
 * Log tag: UTC2_HTTP
 */
public final class RetrofitClient {

    public static final String BASE_URL = "https://utc2.edu.vn/api/v1.0/";
    private static final String LOG_TAG = "UTC2_HTTP";
    private static final int    TIMEOUT = 30;

    // Đổi tên biến buộc compiler tạo instance mới
    private static volatile Retrofit client;

    private RetrofitClient() {}

    public static Retrofit getInstance() {
        if (client == null) {
            synchronized (RetrofitClient.class) {
                if (client == null) { client = build(); }
            }
        }
        return client;
    }

    public static ApiService api() {
        return getInstance().create(ApiService.class);
    }

    private static Retrofit build() {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT,    TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT,   TimeUnit.SECONDS)
                .addInterceptor(logInterceptor())
                .build();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private static Interceptor logInterceptor() {
        return chain -> {
            Request req = chain.request();
            Log.i(LOG_TAG, "╔══════════════════════════════════════");
            Log.i(LOG_TAG, "║ REQUEST: " + req.method() + " " + req.url());

            long t1 = System.currentTimeMillis();
            Response resp;
            try {
                resp = chain.proceed(req);
            } catch (IOException e) {
                Log.e(LOG_TAG, "║ FAILED: " + e.getClass().getSimpleName() + ": " + e.getMessage());
                Log.e(LOG_TAG, "╚══════════════════════════════════════");
                throw e;
            }

            long ms = System.currentTimeMillis() - t1;
            Log.i(LOG_TAG, "║ RESPONSE: HTTP " + resp.code() + " (" + ms + "ms)");

            try {
                ResponseBody peeked = resp.peekBody(Long.MAX_VALUE);
                String raw = peeked.string();
                int chunkSize = 3000;
                for (int i = 0; i < raw.length(); i += chunkSize) {
                    Log.i(LOG_TAG, "║ BODY[" + (i/chunkSize) + "]: "
                            + raw.substring(i, Math.min(i + chunkSize, raw.length())));
                }
            } catch (Exception e) {
                Log.w(LOG_TAG, "║ Cannot peek body: " + e.getMessage());
            }

            Log.i(LOG_TAG, "╚══════════════════════════════════════");
            return resp;
        };
    }
}