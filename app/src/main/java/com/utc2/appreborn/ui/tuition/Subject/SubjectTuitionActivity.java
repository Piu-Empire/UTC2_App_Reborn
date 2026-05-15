package com.utc2.appreborn.ui.tuition.Subject;

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
import com.utc2.appreborn.ui.tuition.adapter.SubjectTuitionAdapter;
import com.utc2.appreborn.ui.tuition.model.SubjectTuition;
import com.utc2.appreborn.ui.tuition.model.Tuition;
import com.utc2.appreborn.utils.NetworkUtils;
import com.utc2.appreborn.utils.SessionManager;

// THÊM MỚI
import com.utc2.appreborn.network.ApiClient;
import com.utc2.appreborn.network.ApiResponse;
import com.utc2.appreborn.network.TuitionApiService;
import com.utc2.appreborn.network.dto.TuitionSummaryResponse;
import com.utc2.appreborn.network.dto.TuitionResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SubjectTuitionActivity extends AppCompatActivity {

    private RecyclerView rvItems;
    private List<SubjectTuition> subjectList = new ArrayList<>();
    private TextView tvTotalAmount;
    private Button btnPay;
    private double totalAmount = 0.0;
    private NetworkUtils networkUtils;

    @Override
    protected void attachBaseContext(android.content.Context base) {
        super.attachBaseContext(LocaleHelper.applyLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_tuition);

        try {
            initViews();
            setupNetworkMonitoring();
            loadData(); // gọi API
        } catch (Exception e) {
            Log.e("SubjectTuition", "Lỗi khởi tạo: " + e.getMessage());
        }
    }

    private void initViews() {
        rvItems = findViewById(R.id.rvItems);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btnPay = findViewById(R.id.btnPay);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        btnPay.setOnClickListener(v -> {
            if (NetworkUtils.isNetworkAvailable(this)) {
                showPaymentDialog();
            } else {
                Toast.makeText(this, "Vui lòng kết nối mạng để tạo mã thanh toán!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupNetworkMonitoring() {
        networkUtils = new NetworkUtils(this, new NetworkUtils.NetworkStatusListener() {
            @Override
            public void onNetworkAvailable() {
                Log.d("Network", "Sẵn sàng thanh toán học phí môn học");
            }

            @Override
            public void onNetworkLost() {
                Toast.makeText(SubjectTuitionActivity.this,
                        "Mất kết nối mạng! Giao dịch có thể bị gián đoạn.",
                        Toast.LENGTH_LONG).show();
            }
        });
        networkUtils.register();
    }

    // ĐÃ SỬA: gọi API thay data cứng
    private void loadData() {
        String token = SessionManager.getInstance(this).getAuthToken();
        TuitionApiService tuitionApi = ApiClient.getInstance(token).create(TuitionApiService.class);

        tuitionApi.getSummary().enqueue(new Callback<ApiResponse<TuitionSummaryResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<TuitionSummaryResponse>> call,
                                   Response<ApiResponse<TuitionSummaryResponse>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess()) {
                    TuitionSummaryResponse summary = response.body().getData();
                    subjectList.clear();
                    if (summary.semesters != null) {
                        for (TuitionResponse t : summary.semesters) {
                            subjectList.add(new SubjectTuition(
                                    t.id != null ? t.id.intValue() : 0,
                                    "Học kỳ " + t.semesterId,
                                    "",
                                    t.getRemainingAmountAsDouble(),
                                    t.status
                            ));
                        }
                    }
                    calculateTotal();
                    setupRecyclerView();
                } else {
                    Toast.makeText(SubjectTuitionActivity.this,
                            "Không tải được học phí", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<TuitionSummaryResponse>> call, Throwable t) {
                Toast.makeText(SubjectTuitionActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculateTotal() {
        totalAmount = 0.0;
        for (SubjectTuition subject : subjectList) {
            if (!subject.isPaid()) {
                totalAmount += subject.getAmount();
            }
        }
        tvTotalAmount.setText(String.format(Locale.getDefault(), "%,.0f VND", totalAmount));
    }

    private void setupRecyclerView() {
        rvItems.setLayoutManager(new LinearLayoutManager(this));
        rvItems.setAdapter(new SubjectTuitionAdapter(subjectList));
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
                + "&addInfo=AppReborn%20Hoc%20phi%20mon%20hoc"
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