package com.utc2.appreborn.ui.login;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.utc2.appreborn.R;
import com.utc2.appreborn.network.ApiClient;
import com.utc2.appreborn.network.ApiResponse;
import com.utc2.appreborn.network.AuthApiService;
import com.utc2.appreborn.network.dto.ForgotPasswordRequest;
import com.utc2.appreborn.network.dto.ResetPasswordRequest;
import com.utc2.appreborn.utils.LocaleHelper;
import com.utc2.appreborn.utils.NetworkUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Màn hình quên mật khẩu — 2 bước:
 * Bước 1: nhập MSSV/email → nhấn "Gửi OTP" → server gửi email
 * Bước 2: nhập OTP + mật khẩu mới → nhấn "Đặt lại mật khẩu"
 */
public class ForgotPasswordActivity extends AppCompatActivity {

    private AuthApiService authApi;
    private String pendingEmail; // lưu email dùng cho bước 2

    private LinearLayout layoutStep1, layoutStep2;
    private EditText edtEmail, edtOtp, edtNewPassword, edtConfirmPassword;
    private Button btnSendOtp, btnResetPassword, btnResendOtp;

    @Override
    protected void attachBaseContext(android.content.Context base) {
        super.attachBaseContext(LocaleHelper.applyLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        authApi = ApiClient.getPublicInstance().create(AuthApiService.class);

        initViews();
    }

    private void initViews() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        layoutStep1 = findViewById(R.id.layoutStep1);
        layoutStep2 = findViewById(R.id.layoutStep2);

        edtEmail = findViewById(R.id.edtEmail);
        edtOtp = findViewById(R.id.edtOtp);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);

        btnSendOtp = findViewById(R.id.btnSendOtp);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        btnResendOtp = findViewById(R.id.btnResendOtp);

        btnBack.setOnClickListener(v -> finish());

        btnSendOtp.setOnClickListener(v -> sendOtp());
        btnResendOtp.setOnClickListener(v -> sendOtp());
        btnResetPassword.setOnClickListener(v -> doResetPassword());
    }

    // ─── Bước 1: Gửi OTP về email ────────────────────────────────────────────

    private void sendOtp() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            showToast("Không có kết nối mạng");
            return;
        }

        String input = edtEmail.getText().toString().trim();
        if (input.isEmpty()) {
            edtEmail.setError("Vui lòng nhập MSSV hoặc email");
            edtEmail.requestFocus();
            return;
        }

        btnSendOtp.setEnabled(false);
        btnResendOtp.setEnabled(false);

        authApi.forgotPassword(new ForgotPasswordRequest(input))
                .enqueue(new Callback<ApiResponse<String>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<String>> call,
                                           Response<ApiResponse<String>> response) {
                        btnSendOtp.setEnabled(true);
                        btnResendOtp.setEnabled(true);
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            pendingEmail = input;
                            showToast("Đã gửi OTP đến email của bạn!");
                            // Chuyển sang bước 2
                            layoutStep1.setVisibility(View.GONE);
                            layoutStep2.setVisibility(View.VISIBLE);
                        } else {
                            String msg = response.body() != null
                                    ? response.body().getMessage()
                                    : "Không tìm thấy tài khoản";
                            showToast(msg);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                        btnSendOtp.setEnabled(true);
                        btnResendOtp.setEnabled(true);
                        showToast("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }

    // ─── Bước 2: Đặt lại mật khẩu bằng OTP ──────────────────────────────────

    private void doResetPassword() {
        String otp = edtOtp.getText().toString().trim();
        String newPass = edtNewPassword.getText().toString().trim();
        String confirmPass = edtConfirmPassword.getText().toString().trim();

        if (otp.isEmpty()) {
            edtOtp.setError("Nhập mã OTP");
            edtOtp.requestFocus();
            return;
        }
        if (newPass.isEmpty() || newPass.length() < 6) {
            edtNewPassword.setError("Mật khẩu tối thiểu 6 ký tự");
            edtNewPassword.requestFocus();
            return;
        }
        if (!newPass.equals(confirmPass)) {
            edtConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            edtConfirmPassword.requestFocus();
            return;
        }

        if (!NetworkUtils.isNetworkAvailable(this)) {
            showToast("Không có kết nối mạng");
            return;
        }

        btnResetPassword.setEnabled(false);

        authApi.resetPassword(new ResetPasswordRequest(pendingEmail, otp, newPass))
                .enqueue(new Callback<ApiResponse<String>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<String>> call,
                                           Response<ApiResponse<String>> response) {
                        btnResetPassword.setEnabled(true);
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            showToast("Đặt lại mật khẩu thành công! Vui lòng đăng nhập.");
                            finish();
                        } else {
                            String msg = response.body() != null
                                    ? response.body().getMessage()
                                    : "OTP không đúng hoặc đã hết hạn";
                            showToast(msg);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                        btnResetPassword.setEnabled(true);
                        showToast("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}