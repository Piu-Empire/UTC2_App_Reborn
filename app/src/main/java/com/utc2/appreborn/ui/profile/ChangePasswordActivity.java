package com.utc2.appreborn.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.utc2.appreborn.R;
import com.utc2.appreborn.utils.NetworkUtils;

public class ChangePasswordActivity extends AppCompatActivity {

    private AppCompatButton btnUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        initViews();
    }

    private void initViews() {
        EditText etNewPass = findViewById(R.id.etNewPassword);
        EditText etConfirmPass = findViewById(R.id.etConfirmPassword);
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnUpdate = findViewById(R.id.btnUpdatePassword);

        // Thiết lập Resource từ file strings.xml của AppReborn
        btnUpdate.setText(R.string.btn_update_password);
        etNewPass.setHint(R.string.hint_new_password);
        etConfirmPass.setHint(R.string.hint_confirm_password);

        btnBack.setOnClickListener(v -> finish());

        btnUpdate.setOnClickListener(v -> {
            // Kiểm tra mạng bằng cách gọi trực tiếp hàm static (Tối ưu RAM)
            if (!NetworkUtils.isNetworkAvailable(this)) {
                showToast(getString(R.string.error_connect_network));
                return;
            }

            String newPass = etNewPass.getText().toString().trim();
            String confirmPass = etConfirmPass.getText().toString().trim();
            handlePasswordChange(newPass, confirmPass);
        });
    }

    private void handlePasswordChange(String newPass, String confirmPass) {
        if (TextUtils.isEmpty(newPass)) {
            showToast(getString(R.string.msg_password_requirement));
            return;
        }

        if (newPass.length() < 6) {
            showToast(getString(R.string.msg_password_requirement));
            return;
        }

        if (!newPass.equals(confirmPass)) {
            // Sử dụng thông báo tiếng Việt khớp với ngữ cảnh sinh viên
            showToast("Mật khẩu xác nhận không khớp");
            return;
        }

        // Logic Hardcoded: Vì hệ thống đang dùng Google Sign-In hoặc Tài khoản mẫu
        performGoogleAccountRedirect();
    }

    private void performGoogleAccountRedirect() {
        // Thông báo yêu cầu đăng nhập lại sau khi đổi
        showToast(getString(R.string.msg_relogin_note));

        // Mở trang quản lý tài khoản Google thực tế
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://myaccount.google.com/signinoptions/password"));
            startActivity(intent);
            finish();
        } catch (Exception e) {
            showToast("Không thể mở trình duyệt");
        }
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}