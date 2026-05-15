package com.utc2.appreborn.ui.tuition.Dorm;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import com.utc2.appreborn.utils.LocaleHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.tuition.adapter.DormAdapter;
import com.utc2.appreborn.ui.tuition.model.DormTuition;
import com.utc2.appreborn.ui.tuition.model.Tuition;
import com.utc2.appreborn.utils.NetworkUtils;
import com.utc2.appreborn.utils.SessionManager;

// THÊM MỚI
import com.utc2.appreborn.network.ApiClient;
import com.utc2.appreborn.network.ApiResponse;
import com.utc2.appreborn.network.TuitionApiService;
import com.utc2.appreborn.network.dto.TuitionResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DormitoryTuitionActivity extends AppCompatActivity {

    private RecyclerView rvDormTuition;
    private List<DormTuition> dormList = new ArrayList<>();
    private Button btnPayDorm;
    private double totalAmount = 0.0;
    private NetworkUtils networkUtils;

    @Override
    protected void attachBaseContext(android.content.Context base) {
        super.attachBaseContext(LocaleHelper.applyLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dormitory_tuition);

        try {
            initViews();
            setupNetworkMonitoring();
            loadDormData();
        } catch (Exception e) {
            Log.e("DormTuition", "Lỗi khởi tạo: " + e.getMessage());
        }
    }

    private void initViews() {
        rvDormTuition = findViewById(R.id.rvDormTuition);
        btnPayDorm = findViewById(R.id.btnPayDorm);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        btnPayDorm.setOnClickListener(v -> {
            if (NetworkUtils.isNetworkAvailable(this)) {
                showPaymentDialog();
            } else {
                Toast.makeText(this, "Cần kết nối mạng để tạo mã QR thanh toán!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupNetworkMonitoring() {
        networkUtils = new NetworkUtils(this, new NetworkUtils.NetworkStatusListener() {
            @Override
            public void onNetworkAvailable() {
                Log.d("Network", "Sẵn sàng thanh toán tiền KTX");
            }

            @Override
            public void onNetworkLost() {
                Toast.makeText(DormitoryTuitionActivity.this,
                        "Mất kết nối mạng! Giao dịch có thể bị gián đoạn.",
                        Toast.LENGTH_LONG).show();
            }
        });
        networkUtils.register();
    }

    // ĐÃ SỬA: gọi API thay data cứng
    private void loadDormData() {
        String token = SessionManager.getInstance(this).getAuthToken();
        TuitionApiService tuitionApi = ApiClient.getInstance(token).create(TuitionApiService.class);

        tuitionApi.getSummary().enqueue(new Callback<ApiResponse<com.utc2.appreborn.network.dto.TuitionSummaryResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<com.utc2.appreborn.network.dto.TuitionSummaryResponse>> call,
                                   Response<ApiResponse<com.utc2.appreborn.network.dto.TuitionSummaryResponse>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess()) {
                    dormList.clear();
                    List<TuitionResponse> semesters = response.body().getData().semesters;
                    if (semesters != null) {
                        for (TuitionResponse t : semesters) {
                            dormList.add(new DormTuition(
                                    t.id != null ? t.id.intValue() : 0,
                                    "Học kỳ " + t.semesterId,
                                    t.dueDate != null ? t.dueDate : "",
                                    t.remainingAmount != null ? t.remainingAmount : 0.0,
                                    t.status
                            ));
                        }
                    }
                    calculateTotal();
                    setupRecyclerView();
                } else {
                    Toast.makeText(DormitoryTuitionActivity.this,
                            "Không tải được dữ liệu KTX", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<com.utc2.appreborn.network.dto.TuitionSummaryResponse>> call, Throwable t) {
                Toast.makeText(DormitoryTuitionActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculateTotal() {
        totalAmount = 0.0;
        for (DormTuition item : dormList) {
            if (!item.isPaid()) {
                totalAmount += item.getAmount();
            }
        }
    }

    private void setupRecyclerView() {
        rvDormTuition.setLayoutManager(new LinearLayoutManager(this));
        rvDormTuition.setAdapter(new DormAdapter(dormList));
    }

    private void showPaymentDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_payment_qr);

        Window window = dialog.getWindow();
        if (window != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
            window.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.CENTER;
            window.setAttributes(params);
        }

        ImageView imgQr = dialog.findViewById(R.id.imgQrCode);
        TextView tvDialogAmount = dialog.findViewById(R.id.tvDialogAmount);
        Button btnConfirm = dialog.findViewById(R.id.btnConfirmPayment);

        tvDialogAmount.setText(String.format(Locale.getDefault(), "%,.0f VND", totalAmount));

        String qrUrl = "https://img.vietqr.io/image/ICB-102882730986-compact.png"
                + "?amount=" + (long) totalAmount
                + "&addInfo=AppReborn%20Tien%20KTX"
                + "&accountName=HINH%20VINH%20PHAT";

        Glide.with(this).load(qrUrl).placeholder(R.drawable.logo_utc2).into(imgQr);

        btnConfirm.setOnClickListener(v -> {
            Toast.makeText(this, getString(R.string.msg_checking_transaction), Toast.LENGTH_SHORT).show();
            btnConfirm.postDelayed(() -> {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                    Toast.makeText(this, getString(R.string.msg_payment_success), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }, 2000);
        });

        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkUtils != null) networkUtils.unregister();
    }
}