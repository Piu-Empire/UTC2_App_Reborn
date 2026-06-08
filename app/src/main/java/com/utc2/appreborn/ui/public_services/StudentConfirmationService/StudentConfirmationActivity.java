package com.utc2.appreborn.ui.public_services.StudentConfirmationService;

import android.os.Bundle;
import com.utc2.appreborn.utils.LocaleHelper;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.utc2.appreborn.R;
import com.utc2.appreborn.network.ApiClient;
import com.utc2.appreborn.network.ApiResponse;
import com.utc2.appreborn.network.ProfileApiService;
import com.utc2.appreborn.network.PublicServicesApiService;
import com.utc2.appreborn.network.dto.ProfileResponse;
import com.utc2.appreborn.network.dto.ServiceRequestResponse;
import com.utc2.appreborn.network.dto.StudentConfirmationRequest;
import com.utc2.appreborn.utils.NetworkUtils;
import com.utc2.appreborn.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudentConfirmationActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView    txtName, txtMSSV, txtClass, btnConfirm;
    private EditText    edtReason;
    // FIX WARN 1: thêm field nhập số lượng — backend có @Min(1) cho quantity
    private EditText    edtQuantity;

    @Override
    protected void attachBaseContext(android.content.Context base) {
        super.attachBaseContext(LocaleHelper.applyLocale(base));
    }

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
        btnBack     = findViewById(R.id.btnBack);
        txtName     = findViewById(R.id.txtName);
        txtMSSV     = findViewById(R.id.txtMSSV);
        txtClass    = findViewById(R.id.txtClass);
        edtReason   = findViewById(R.id.edtReason);
        btnConfirm  = findViewById(R.id.btnConfirm);
        // FIX WARN 1: bind field edtQuantity từ layout
        // (cần thêm EditText id="edtQuantity" vào activity_student_confirmation.xml)
        edtQuantity = findViewById(R.id.edtQuantity);
    }

    private void setupData() {
        String token = SessionManager.getInstance(this).getAuthToken();
        ProfileApiService profileApi = ApiClient.getInstance(token).create(ProfileApiService.class);

        profileApi.getMyProfile().enqueue(new Callback<ApiResponse<ProfileResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ProfileResponse>> call,
                                   Response<ApiResponse<ProfileResponse>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess()) {
                    ProfileResponse p = response.body().getData();
                    txtName.setText(p.fullName);
                    txtMSSV.setText(p.studentId);
                    txtClass.setText(p.className);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ProfileResponse>> call, Throwable t) {
                Log.e("StudentConfirmation", "Lỗi load profile: " + t.getMessage());
            }
        });
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());

        btnConfirm.setOnClickListener(v -> {
            if (!NetworkUtils.isNetworkAvailable(this)) {
                com.utc2.appreborn.utils.CustomToastHelper.showToast(this, "Không có mạng! Vui lòng kết nối để gửi yêu cầu xác nhận.");
                return;
            }

            String reason = edtReason.getText().toString().trim();
            if (reason.isEmpty()) {
                edtReason.setError("Bạn cần nhập lý do");
                return;
            }

            // FIX WARN 1: đọc quantity từ input, fallback về 1 nếu trống hoặc không hợp lệ
            int quantity = 1;
            if (edtQuantity != null) {
                String qtyStr = edtQuantity.getText().toString().trim();
                if (!qtyStr.isEmpty()) {
                    try {
                        quantity = Integer.parseInt(qtyStr);
                        if (quantity < 1) {
                            edtQuantity.setError("Số lượng tối thiểu là 1");
                            return;
                        }
                    } catch (NumberFormatException e) {
                        edtQuantity.setError("Số lượng không hợp lệ");
                        return;
                    }
                }
            }

            String token = SessionManager.getInstance(this).getAuthToken();
            PublicServicesApiService servicesApi = ApiClient.getInstance(token).create(PublicServicesApiService.class);

            servicesApi.studentConfirmation(new StudentConfirmationRequest(reason, quantity))
                    .enqueue(new Callback<ApiResponse<ServiceRequestResponse>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<ServiceRequestResponse>> call,
                                               Response<ApiResponse<ServiceRequestResponse>> response) {
                            if (response.isSuccessful() && response.body() != null
                                    && response.body().isSuccess()) {
                                com.utc2.appreborn.utils.CustomToastHelper.showToast(StudentConfirmationActivity.this, "Đăng ký giấy xác nhận thành công!");
                                finish();
                            } else {
                                com.utc2.appreborn.utils.CustomToastHelper.showToast(StudentConfirmationActivity.this, "Gửi thất bại, thử lại sau.");
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<ServiceRequestResponse>> call, Throwable t) {
                            com.utc2.appreborn.utils.CustomToastHelper.showToast(StudentConfirmationActivity.this, "Lỗi kết nối: " + t.getMessage());
                        }
                    });
        });
    }
}