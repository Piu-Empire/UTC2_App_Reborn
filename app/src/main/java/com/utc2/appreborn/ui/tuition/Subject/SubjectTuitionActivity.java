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
import android.widget.ProgressBar;
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
    private TuitionApiService tuitionApi;

    // Danh sách semesterId chưa đóng — dùng để gọi pay() sau khi xác nhận QR
    private final List<Long> unpaidSemesterIds = new ArrayList<>();

    private ProgressBar progressBar;

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
        progressBar = findViewById(R.id.progressBar);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        btnPay.setOnClickListener(v -> {
            if (NetworkUtils.isNetworkAvailable(this)) {
                showPaymentDialog();
            } else {
                com.utc2.appreborn.utils.CustomToastHelper.showToast(this, "Vui lòng kết nối mạng để tạo mã thanh toán!");
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
                com.utc2.appreborn.utils.CustomToastHelper.showToast(SubjectTuitionActivity.this, "Mất kết nối mạng! Giao dịch có thể bị gián đoạn.");
            }
        });
        networkUtils.register();
    }

    private void loadData() {
        if (progressBar != null) progressBar.setVisibility(android.view.View.VISIBLE);
        String token = SessionManager.getInstance(this).getAuthToken();
        tuitionApi = ApiClient.getInstance(token).create(TuitionApiService.class);

        tuitionApi.getSummary().enqueue(new Callback<ApiResponse<TuitionSummaryResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<TuitionSummaryResponse>> call,
                                   Response<ApiResponse<TuitionSummaryResponse>> response) {
                if (progressBar != null) progressBar.setVisibility(android.view.View.GONE);
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess()) {
                    TuitionSummaryResponse summary = response.body().getData();
                    subjectList.clear();
                    unpaidSemesterIds.clear();
                    if (summary.semesters != null) {
                        for (TuitionResponse t : summary.semesters) {
                            long   feeId  = t.id         != null ? t.id         : 0L;
                            long   semId  = t.semesterId != null ? t.semesterId : 0L;
                            double total  = t.getTotalAmountAsDouble();
                            double paid   = t.getPaidAmountAsDouble();
                            String status = t.status          != null ? t.status          : "";
                            String due    = t.dueDate         != null ? t.dueDate         : "";
                            String paidAt = t.paidAt          != null ? t.paidAt          : "";
                            String method = t.paymentMethod   != null ? t.paymentMethod   : "";

                            subjectList.add(new SubjectTuition(
                                    feeId, 0L, semId,
                                    total, paid,
                                    due, status, method, paidAt,
                                    0L, "", "Học kỳ " + semId, 0, ""
                            ));

                            // Track kỳ chưa đóng để gọi pay() sau khi xác nhận QR
                            if (!Tuition.STATUS_PAID.equals(status)) {
                                unpaidSemesterIds.add(semId);
                            }
                        }
                    }
                    calculateTotal();
                    setupRecyclerView();
                } else {
                    com.utc2.appreborn.utils.CustomToastHelper.showToast(SubjectTuitionActivity.this, "Không tải được học phí");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<TuitionSummaryResponse>> call, Throwable t) {
                if (progressBar != null) progressBar.setVisibility(android.view.View.GONE);
                com.utc2.appreborn.utils.CustomToastHelper.showToast(SubjectTuitionActivity.this, "Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void calculateTotal() {
        totalAmount = 0.0;
        for (SubjectTuition subject : subjectList) {
            if (!subject.isPaid()) {
                // FIX: dùng getRemainingAmount() (= total - paid), không phải getAmount()
                totalAmount += subject.getRemainingAmount();
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

        ImageView imgQr          = dialog.findViewById(R.id.imgQrCode);
        TextView  tvDialogAmount = dialog.findViewById(R.id.tvDialogAmount);
        Button    btnConfirm     = dialog.findViewById(R.id.btnConfirmPayment);

        tvDialogAmount.setText(String.format(Locale.getDefault(), "%,.0f VND", totalAmount));

        String qrUrl = "https://img.vietqr.io/image/ICB-102882730986-compact.png"
                + "?amount=" + (long) totalAmount
                + "&addInfo=AppReborn%20Hoc%20phi%20mon%20hoc"
                + "&accountName=HINH%20VINH%20PHAT";

        Glide.with(this).load(qrUrl).placeholder(R.drawable.logo_utc2).into(imgQr);

        btnConfirm.setOnClickListener(v -> {
            btnConfirm.setEnabled(false);
            com.utc2.appreborn.utils.CustomToastHelper.showToast(this, getString(R.string.msg_checking_transaction));
            // Gọi pay API cho từng kỳ chưa đóng
            confirmPaymentToServer(dialog);
        });

        dialog.show();
    }

    /** Gọi POST /api/v1/tuition/pay/{semesterId} cho từng kỳ chưa đóng */
    private void confirmPaymentToServer(Dialog dialog) {
        if (unpaidSemesterIds.isEmpty()) {
            dialog.dismiss();
            return;
        }
        // Gọi tuần tự từng kỳ — đơn giản vì thường chỉ có 1 kỳ chưa đóng
        callPayRecursive(dialog, new ArrayList<>(unpaidSemesterIds), 0);
    }

    private void callPayRecursive(Dialog dialog, List<Long> ids, int index) {
        if (index >= ids.size()) {
            // Tất cả kỳ đã gọi pay xong
            if (dialog.isShowing()) dialog.dismiss();
            com.utc2.appreborn.utils.CustomToastHelper.showToast(this, getString(R.string.msg_payment_success));
            // Reload lại data
            loadData();
            return;
        }
        long semId = ids.get(index);
        tuitionApi.pay(semId, "online").enqueue(new Callback<ApiResponse<TuitionResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<TuitionResponse>> call,
                                   Response<ApiResponse<TuitionResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    // Tiếp tục kỳ tiếp theo
                    callPayRecursive(dialog, ids, index + 1);
                } else {
                    if (dialog.isShowing()) dialog.dismiss();
                    com.utc2.appreborn.utils.CustomToastHelper.showToast(SubjectTuitionActivity.this, "Lỗi xác nhận thanh toán kỳ " + semId);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<TuitionResponse>> call, Throwable t) {
                if (dialog.isShowing()) dialog.dismiss();
                com.utc2.appreborn.utils.CustomToastHelper.showToast(SubjectTuitionActivity.this, "Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkUtils != null) networkUtils.unregister();
    }
}