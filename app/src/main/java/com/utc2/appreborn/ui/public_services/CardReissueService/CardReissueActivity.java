package com.utc2.appreborn.ui.public_services.CardReissueService;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.public_services.model.CardReissueService;
import com.utc2.appreborn.utils.NetworkUtils;

public class CardReissueActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView btnConfirm, txtName, txtMSSV, txtClass;
    private EditText edtReason;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_reissue);

        try {
            initViews();
            setupData();
            setupEvents();
        } catch (Exception e) {
            Log.e("CardReissue", "Lỗi khởi tạo: " + e.getMessage());
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnConfirm = findViewById(R.id.btnConfirm);
        txtName = findViewById(R.id.txtName);
        txtMSSV = findViewById(R.id.txtMSSV);
        txtClass = findViewById(R.id.txtClass);
        edtReason = findViewById(R.id.edtReason);
    }

    private void setupData() {
        txtName.setText(getString(R.string.default_name));
        txtMSSV.setText(getString(R.string.default_mssv));
        txtClass.setText(getString(R.string.default_class));
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());

        btnConfirm.setOnClickListener(v -> {
            // Kiểm tra kết nối mạng trước khi xử lý[cite: 4]
            if (!NetworkUtils.isNetworkAvailable(this)) {
                Toast.makeText(this, "Không có mạng, không thể gửi đơn đăng ký!", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                String name = txtName.getText().toString();
                String mssv = txtMSSV.getText().toString();
                String className = txtClass.getText().toString();
                String reason = edtReason.getText().toString().trim();
                String finalReason = reason.isEmpty() ? "Không có lý do cụ thể" : reason;

                CardReissueService newRequest = new CardReissueService(
                        getString(R.string.reissue_card_title),
                        finalReason,
                        System.currentTimeMillis(),
                        0,
                        "CARD_REISSUE",
                        name,
                        mssv,
                        className
                );

                // API call here
                Toast.makeText(this, R.string.cardReissue_registration_success, Toast.LENGTH_SHORT).show();
                finish();

            } catch (Exception e) {
                Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}