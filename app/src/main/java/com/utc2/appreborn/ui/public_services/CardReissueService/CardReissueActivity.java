package com.utc2.appreborn.ui.public_services.CardReissueService;

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
import com.utc2.appreborn.network.dto.CardReissueRequest;
import com.utc2.appreborn.network.dto.ProfileResponse;
import com.utc2.appreborn.network.dto.ServiceRequestResponse;
import com.utc2.appreborn.utils.NetworkUtils;
import com.utc2.appreborn.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CardReissueActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView btnConfirm, txtName, txtMSSV, txtClass;
    private EditText edtReason;

    @Override
    protected void attachBaseContext(android.content.Context base) {
        super.attachBaseContext(LocaleHelper.applyLocale(base));
    }

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

    // ĐÃ SỬA: lấy profile thật từ API
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
                Log.e("CardReissue", "Lỗi load profile: " + t.getMessage());
            }
        });
    }

    // ĐÃ SỬA: gửi API thay TODO
    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());

        btnConfirm.setOnClickListener(v -> {
            if (!NetworkUtils.isNetworkAvailable(this)) {
                com.utc2.appreborn.utils.CustomToastHelper.showToast(this, "Không có mạng, không thể gửi đơn đăng ký!");
                return;
            }

            String reason = edtReason.getText().toString().trim();
            String finalReason = reason.isEmpty() ? "Không có lý do cụ thể" : reason;

            String token = SessionManager.getInstance(this).getAuthToken();
            PublicServicesApiService servicesApi = ApiClient.getInstance(token).create(PublicServicesApiService.class);

            servicesApi.cardReissue(new CardReissueRequest(finalReason))
                    .enqueue(new Callback<ApiResponse<ServiceRequestResponse>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<ServiceRequestResponse>> call,
                                               Response<ApiResponse<ServiceRequestResponse>> response) {
                            if (response.isSuccessful() && response.body() != null
                                    && response.body().isSuccess()) {
                                com.utc2.appreborn.utils.CustomToastHelper.showToast(CardReissueActivity.this, getString(R.string.cardReissue_registration_success));
                                finish();
                            } else {
                                com.utc2.appreborn.utils.CustomToastHelper.showToast(CardReissueActivity.this, "Gửi thất bại, thử lại sau.");
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<ServiceRequestResponse>> call, Throwable t) {
                            com.utc2.appreborn.utils.CustomToastHelper.showToast(CardReissueActivity.this, "Lỗi kết nối: " + t.getMessage());
                        }
                    });
        });
    }
}