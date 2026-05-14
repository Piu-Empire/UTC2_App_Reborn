package com.utc2.appreborn.ui.public_services.CardReissueService;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.public_services.model.BaseService;
import com.utc2.appreborn.ui.public_services.model.CardReissueService;
import com.utc2.appreborn.utils.MockHelper;
import com.utc2.appreborn.utils.NetworkUtils;
import com.utc2.appreborn.utils.SessionManager;

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
        btnBack    = findViewById(R.id.btnBack);
        btnConfirm = findViewById(R.id.btnConfirm);
        txtName    = findViewById(R.id.txtName);
        txtMSSV    = findViewById(R.id.txtMSSV);
        txtClass   = findViewById(R.id.txtClass);
        edtReason  = findViewById(R.id.edtReason);
    }

    private void setupData() {
        // Mapping TABLE USER_PROFILE + STUDENT_PROFILE qua MockHelper / SessionManager
        // Khi có API thật: thay bằng Room query hoặc Retrofit call
        SessionManager session = SessionManager.getInstance(this);

        String studentCode = session.getStudentCode();  // STUDENT_PROFILE.student_code
        String fullName    = MockHelper.getMockFullName();  // USER_PROFILE.full_name
        String className   = MockHelper.getMockClassName(); // STUDENT_PROFILE.class_name

        txtName.setText(fullName);
        txtMSSV.setText(studentCode.isEmpty() ? MockHelper.getMockStudentCode() : studentCode);
        txtClass.setText(className);
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());

        btnConfirm.setOnClickListener(v -> {
            if (!NetworkUtils.isNetworkAvailable(this)) {
                Toast.makeText(this, "Không có mạng, không thể gửi đơn đăng ký!", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                String name      = txtName.getText().toString();
                String mssv      = txtMSSV.getText().toString();
                String className = txtClass.getText().toString();
                String reason    = edtReason.getText().toString().trim();
                String finalReason = reason.isEmpty() ? "Không có lý do cụ thể" : reason;

                // Mapping TABLE SERVICE_REQUEST:
                //   service_type = BaseService.TYPE_CARD_REISSUE  ("thẻ SV")
                //   status       = BaseService.STATUS_PENDING      ("chờ xử lý")
                //   submitted_at = System.currentTimeMillis()
                CardReissueService newRequest = new CardReissueService(
                        getString(R.string.reissue_card_title),
                        finalReason,
                        System.currentTimeMillis(),
                        BaseService.STATUS_PENDING,     // "chờ xử lý" — khớp SERVICE_REQUEST.status
                        BaseService.TYPE_CARD_REISSUE,  // "thẻ SV"    — khớp SERVICE_REQUEST.service_type
                        name,
                        mssv,
                        className
                );

                // TODO: gửi `newRequest` lên API / lưu vào Room
                Toast.makeText(this, R.string.cardReissue_registration_success, Toast.LENGTH_SHORT).show();
                finish();

            } catch (Exception e) {
                Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}