package com.utc2.appreborn.ui.public_services.StudentConfirmationService;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.public_services.model.StudentConfirmationService;
import com.utc2.appreborn.utils.NetworkUtils;

public class StudentConfirmationActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView txtName, txtMSSV, txtClass, btnConfirm;
    private EditText edtReason;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_confirmation);

        try {
            initViews();
            setupData();
            setupEvents();
        } catch (Exception e) {
            Log.e("StudentConfirmation", "Lỗi khởi tạo: " + e.getMessage());
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        txtName = findViewById(R.id.txtName);
        txtMSSV = findViewById(R.id.txtMSSV);
        txtClass = findViewById(R.id.txtClass);
        edtReason = findViewById(R.id.edtReason);
        btnConfirm = findViewById(R.id.btnConfirm);
    }

    private void setupData() {
        txtName.setText(getString(R.string.default_name));
        txtMSSV.setText(getString(R.string.default_mssv));
        txtClass.setText(getString(R.string.default_class));
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());
        btnConfirm.setOnClickListener(v -> {
            // Kiểm tra mạng trước khi gửi đơn[cite: 7]
            if (!NetworkUtils.isNetworkAvailable(this)) {
                Toast.makeText(this, "Không có mạng! Vui lòng kết nối để gửi yêu cầu xác nhận.", Toast.LENGTH_SHORT).show();
                return;
            }

            String reason = edtReason.getText().toString().trim();
            if (reason.isEmpty()) {
                edtReason.setError("Bạn cần nhập lý do");
                return;
            }

            // Logic xử lý gửi giấy xác nhận[cite: 7]
            Toast.makeText(this, "Đăng ký giấy xác nhận thành công!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}