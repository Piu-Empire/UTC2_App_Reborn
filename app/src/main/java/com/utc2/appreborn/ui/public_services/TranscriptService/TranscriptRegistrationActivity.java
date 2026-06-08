package com.utc2.appreborn.ui.public_services.TranscriptService;

import android.os.Bundle;
import com.utc2.appreborn.utils.LocaleHelper;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.utc2.appreborn.network.dto.TranscriptRequest;
import com.utc2.appreborn.utils.NetworkUtils;
import com.utc2.appreborn.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TranscriptRegistrationActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView txtName, txtMSSV, txtClass, btnConfirm;
    private AutoCompleteTextView dropAcademicYear, dropSemester;
    private EditText edtQuantity, edtNote;

    @Override
    protected void attachBaseContext(android.content.Context base) {
        super.attachBaseContext(LocaleHelper.applyLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transcript_registration);

        try {
            initViews();
            setupData();
            setupDropdowns();
            setupEvents();
        } catch (Exception e) {
            Log.e("TranscriptReg", "Lỗi khởi tạo: " + e.getMessage());
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        txtName = findViewById(R.id.txtName);
        txtMSSV = findViewById(R.id.txtMSSV);
        txtClass = findViewById(R.id.txtClass);
        dropAcademicYear = findViewById(R.id.dropAcademicYear);
        dropSemester = findViewById(R.id.dropSemester);
        edtQuantity = findViewById(R.id.edtQuantity);
        edtNote = findViewById(R.id.edtNote);
        btnConfirm = findViewById(R.id.btnConfirm);
    }

    // ĐÃ SỬA: lấy profile thật
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
                Log.e("TranscriptReg", "Lỗi load profile: " + t.getMessage());
            }
        });
    }

    private void setupDropdowns() {
        String[] academicYears = {"2023 - 2024", "2024 - 2025", "Tất cả các năm"};
        dropAcademicYear.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, academicYears));

        String[] semesters = {"Học kỳ 1", "Học kỳ 2"};
        dropSemester.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, semesters));

        dropAcademicYear.setOnClickListener(v -> dropAcademicYear.showDropDown());
        dropSemester.setOnClickListener(v -> dropSemester.showDropDown());
    }

    // ĐÃ SỬA: gửi API thay Toast giả
    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());
        btnConfirm.setOnClickListener(v -> {
            if (!NetworkUtils.isNetworkAvailable(this)) {
                com.utc2.appreborn.utils.CustomToastHelper.showToast(this, "Không có mạng! Vui lòng kết nối để gửi yêu cầu.");
                return;
            }

            String academicYear = dropAcademicYear.getText().toString();
            String semester     = dropSemester.getText().toString();
            String quantityStr  = edtQuantity.getText().toString();
            String note         = edtNote.getText().toString().trim();

            if (academicYear.isEmpty() || semester.isEmpty() || quantityStr.isEmpty()) {
                com.utc2.appreborn.utils.CustomToastHelper.showToast(this, "Vui lòng nhập đầy đủ thông tin bắt buộc");
                return;
            }

            int quantity;
            try {
                quantity = Integer.parseInt(quantityStr);
                if (quantity < 1) {
                    edtQuantity.setError("Số lượng tối thiểu là 1");
                    return;
                }
            } catch (NumberFormatException e) {
                edtQuantity.setError("Số lượng không hợp lệ");
                return;
            }
            String token = SessionManager.getInstance(this).getAuthToken();
            PublicServicesApiService servicesApi = ApiClient.getInstance(token).create(PublicServicesApiService.class);

            servicesApi.transcript(new TranscriptRequest(academicYear, semester, quantity, note))
                    .enqueue(new Callback<ApiResponse<ServiceRequestResponse>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<ServiceRequestResponse>> call,
                                               Response<ApiResponse<ServiceRequestResponse>> response) {
                            if (response.isSuccessful() && response.body() != null
                                    && response.body().isSuccess()) {
                                com.utc2.appreborn.utils.CustomToastHelper.showToast(TranscriptRegistrationActivity.this, "Đăng ký bảng điểm thành công!");
                                finish();
                            } else {
                                com.utc2.appreborn.utils.CustomToastHelper.showToast(TranscriptRegistrationActivity.this, "Gửi thất bại, thử lại sau.");
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<ServiceRequestResponse>> call, Throwable t) {
                            com.utc2.appreborn.utils.CustomToastHelper.showToast(TranscriptRegistrationActivity.this, "Lỗi kết nối: " + t.getMessage());
                        }
                    });
        });
    }
}