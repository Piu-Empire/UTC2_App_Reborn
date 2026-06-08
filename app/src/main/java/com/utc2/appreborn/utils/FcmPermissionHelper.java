package com.utc2.appreborn.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessaging;
import com.utc2.appreborn.network.ApiClient;
import com.utc2.appreborn.network.ApiResponse;
import com.utc2.appreborn.network.NotificationApiService;
import com.utc2.appreborn.network.dto.FcmTokenRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FcmPermissionHelper {

    private static final String TAG = "FcmPermissionHelper";

    /**
     * Kiểm tra và xin quyền POST_NOTIFICATIONS cho Android 13+.
     * Nếu có quyền, tự động lấy token và gửi lên server.
     * 
     * @param activity Context
     * @param launcher ActivityResultLauncher đăng ký trong Activity/Fragment
     */
    public static void requestIfNeeded(Activity activity, ActivityResultLauncher<String> launcher) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // Đã có quyền, lấy token luôn
                registerToken(activity);
            } else if (activity.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // Hiển thị Rationale Dialog
                showRationaleDialog(activity, launcher);
            } else {
                // Xin quyền trực tiếp
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            // Android < 13 không cần xin quyền POST_NOTIFICATIONS runtime
            registerToken(activity);
        }
    }

    private static void showRationaleDialog(Activity activity, ActivityResultLauncher<String> launcher) {
        new AlertDialog.Builder(activity)
                .setTitle("🔔 Cho phép thông báo")
                .setMessage("Nhận tin tức từ trường, học phí, lịch học và cập nhật đăng ký môn học để không bỏ lỡ thông tin quan trọng.")
                .setPositiveButton("Cho phép", (dialog, which) -> {
                    launcher.launch(Manifest.permission.POST_NOTIFICATIONS);
                })
                .setNegativeButton("Để sau", (dialog, which) -> {
                    // Người dùng từ chối tạm thời
                    Log.d(TAG, "User chose 'Để sau' for notifications.");
                })
                .show();
    }

    /**
     * Lấy token FCM và gọi API lưu lên server.
     */
    public static void registerToken(Context context) {
        SessionManager sessionManager = SessionManager.getInstance(context);
        if (!sessionManager.isLoggedIn()) return;

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Lấy FCM registration token thất bại", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();
                    Log.d(TAG, "FCM Token: " + token);

                    sendTokenToServer(context, sessionManager.getAuthToken(), token);
                });
    }

    private static void sendTokenToServer(Context context, String jwt, String fcmToken) {
        NotificationApiService api = ApiClient.getInstance(jwt).create(NotificationApiService.class);
        api.registerFcmToken(new FcmTokenRequest(fcmToken)).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Gửi token FCM lên server thành công!");
                } else {
                    Log.e(TAG, "Lỗi lưu token trên server: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối khi gửi token: " + t.getMessage());
            }
        });
    }
}
