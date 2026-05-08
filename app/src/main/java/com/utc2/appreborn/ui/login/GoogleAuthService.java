package com.utc2.appreborn.ui.login;

public class GoogleAuthService implements IAuthService {
    // Trong môi trường không Firebase, bạn sẽ gửi idToken về Backend của bạn
    // Ở đây tạm thời giả định xác thực thành công nếu nhận được token từ Google
    @Override
    public void loginWithGoogle(String idToken, IAuthService.AuthCallback callback) {
        if (idToken != null && !idToken.isEmpty()) {
            callback.onSuccess("Đăng nhập Google thành công!");
        } else {
            callback.onError("Không nhận được token từ Google");
        }
    }

    @Override
    public void login(String email, String password, IAuthService.AuthCallback callback) {
        // Có thể giữ lại hoặc để trống nếu bạn bỏ hẳn đăng nhập bằng mật khẩu
    }

    @Override
    public void resetPassword(String email, IAuthService.AuthCallback callback) {
        // Google tự xử lý quản lý tài khoản, app không cần can thiệp reset pass
    }

    @Override
    public void changePassword(String newPassword, AuthCallback callback) { }
}