package com.utc2.appreborn.ui.login;
public interface IAuthService {
    interface AuthCallback {
        void onSuccess(String message);
        void onError(String error);
    }

    void login(String email, String password, AuthCallback callback);

    // THÊM MỚI: Đăng nhập bằng Google
    void loginWithGoogle(String idToken, AuthCallback callback);

    void resetPassword(String email, AuthCallback callback);
    void changePassword(String newPassword, AuthCallback callback);
}