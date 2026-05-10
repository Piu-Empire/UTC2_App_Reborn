package com.utc2.appreborn.ui.public_services.LoanSupportService;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.public_services.model.LoanSupportService;
import com.utc2.appreborn.utils.NetworkUtils;

public class LoanSupportActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView btnConfirm;
    private EditText edtAmount, edtReason, edtPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_support);

        try {
            initViews();
            setupEvents();
        } catch (Exception e) {
            Log.e("LoanSupport", "Lỗi khởi tạo: " + e.getMessage());
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnConfirm = findViewById(R.id.btnConfirmLoan);
        edtAmount = findViewById(R.id.edtAmount);
        edtReason = findViewById(R.id.edtReason);
        edtPhone = findViewById(R.id.edtPhone);
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());
        btnConfirm.setOnClickListener(v -> handleLoanRegistration());
    }

    private void handleLoanRegistration() {
        // Kiểm tra mạng trước khi gửi dữ liệu[cite: 5]
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "Không có kết nối mạng. Không thể gửi đơn lúc này!", Toast.LENGTH_SHORT).show();
            return;
        }

        String amount = edtAmount.getText().toString().trim();
        String reason = edtReason.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        if (amount.isEmpty() || reason.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phone.length() < 10) {
            edtPhone.setError("Số điện thoại không hợp lệ");
            return;
        }

        try {
            LoanSupportService request = new LoanSupportService(
                    getString(R.string.loan_support_title),
                    reason,
                    System.currentTimeMillis(),
                    0,
                    "LOAN_SUPPORT",
                    amount,
                    reason,
                    phone
            );

            // API call here
            Toast.makeText(this, "Đăng ký thành công. Nhà trường sẽ liên hệ qua SĐT của bạn!", Toast.LENGTH_LONG).show();
            finish();

        } catch (Exception e) {
            Toast.makeText(this, "Đăng ký thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}