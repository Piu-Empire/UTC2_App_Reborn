package com.utc2.appreborn.ui.public_services.LoanSupportService;

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
import com.utc2.appreborn.network.PublicServicesApiService;
import com.utc2.appreborn.network.dto.LoanSupportRequest;
import com.utc2.appreborn.network.dto.ServiceRequestResponse;
import com.utc2.appreborn.utils.NetworkUtils;
import com.utc2.appreborn.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoanSupportActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView btnConfirm;
    private EditText edtAmount, edtReason, edtPhone;

    @Override
    protected void attachBaseContext(android.content.Context base) {
        super.attachBaseContext(LocaleHelper.applyLocale(base));
    }

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
        btnBack    = findViewById(R.id.btnBack);
        btnConfirm = findViewById(R.id.btnConfirmLoan);
        edtAmount  = findViewById(R.id.edtAmount);
        edtReason  = findViewById(R.id.edtReason);
        edtPhone   = findViewById(R.id.edtPhone);
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());
        btnConfirm.setOnClickListener(v -> handleLoanRegistration());
    }

    // ĐÃ SỬA
    private void handleLoanRegistration() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            com.utc2.appreborn.utils.CustomToastHelper.showToast(this, "Không có kết nối mạng. Không thể gửi đơn lúc này!");
            return;
        }

        String amount = edtAmount.getText().toString().trim();
        String reason = edtReason.getText().toString().trim();
        String phone  = edtPhone.getText().toString().trim();

        if (amount.isEmpty() || reason.isEmpty() || phone.isEmpty()) {
            com.utc2.appreborn.utils.CustomToastHelper.showToast(this, "Vui lòng nhập đầy đủ thông tin");
            return;
        }

        if (phone.length() < 10) {
            edtPhone.setError("Số điện thoại không hợp lệ");
            return;
        }

        String token = SessionManager.getInstance(this).getAuthToken();
        PublicServicesApiService servicesApi = ApiClient.getInstance(token).create(PublicServicesApiService.class);

        servicesApi.loanSupport(new LoanSupportRequest(amount, reason, phone))
                .enqueue(new Callback<ApiResponse<ServiceRequestResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<ServiceRequestResponse>> call,
                                           Response<ApiResponse<ServiceRequestResponse>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            com.utc2.appreborn.utils.CustomToastHelper.showToast(LoanSupportActivity.this, "Đăng ký thành công. Nhà trường sẽ liên hệ qua SĐT của bạn!");
                            finish();
                        } else {
                            com.utc2.appreborn.utils.CustomToastHelper.showToast(LoanSupportActivity.this, "Gửi thất bại, thử lại sau.");
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<ServiceRequestResponse>> call, Throwable t) {
                        com.utc2.appreborn.utils.CustomToastHelper.showToast(LoanSupportActivity.this, "Lỗi kết nối: " + t.getMessage());
                    }
                });
    }
}