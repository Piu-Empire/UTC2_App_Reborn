package com.utc2.appreborn.ui.login;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.utc2.appreborn.R;
import com.utc2.appreborn.utils.NetworkUtils;

public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initViews();
    }

    private void initViews() {
        EditText edtEmail = findViewById(R.id.edtEmail);
        Button btnReset = findViewById(R.id.btnReset);
        ImageButton btnBack = findViewById(R.id.btnBack);

        // Thiết lập giao diện từ strings.xml của AppReborn
        btnReset.setText(R.string.send_reset_email);
        edtEmail.setHint(R.string.enter_email);

        // Nút quay lại màn hình đăng nhập
        btnBack.setOnClickListener(v -> finish());

        // Nút gửi yêu cầu reset mật khẩu
        btnReset.setOnClickListener(v -> {
            // Sử dụng NetworkUtils để tiết kiệm tài nguyên hệ thống
            if (!NetworkUtils.isNetworkAvailable(this)) {
                showToast(getString(R.string.error_no_network));
                return;
            }

            String email = edtEmail.getText().toString().trim();

            if (email.isEmpty()) {
                edtEmail.setError(getString(R.string.email_required));
                edtEmail.requestFocus();
                return;
            }

            // Thực hiện tác vụ giả lập vì đang dùng dữ liệu cứng
            performResetTask(btnReset);
        });
    }

    private void performResetTask(View btn) {
        btn.setEnabled(false);

        // Thông báo sinh viên kiểm tra email (ví dụ: gửi tới email sv UTC2)
        showToast(getString(R.string.check_email_for_pass));

        // Giả lập độ trễ xử lý 1.5 giây trước khi tự động quay về Login
        btn.postDelayed(this::finish, 1500);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}