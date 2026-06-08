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
    private final List<SubjectTuition> subjectList = new ArrayList<>();
    private TextView tvTotalAmount;
    private Button btnPay;
    private double totalAmount = 0.0;
    private NetworkUtils networkUtils;
    private TuitionApiService tuitionApi;

    // semesterId của các kỳ còn nợ — dùng để gọi pay() sau khi xác nhận QR
    private final List<Long> unpaidSemesterIds = new ArrayList<>();

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
            loadData();
        } catch (Exception e) {
            Log.e("SubjectTuition", "Lỗi khởi tạo: " + e.getMessage());
        }
    }

    private void initViews() {
        rvItems       = findViewById(R.id.rvItems);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btnPay        = findViewById(R.id.btnPay);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        btnPay.setOnClickListener(v -> {
            if (NetworkUtils.isNetworkAvailable(this)) {
                showPaymentDialog();
            } else {
                Toast.makeText(this,
                        "Vui lòng kết nối mạng để tạo mã thanh toán!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupNetworkMonitoring() {
        networkUtils = new NetworkUtils(this, new NetworkUtils.NetworkStatusListener() {
            @Override public void onNetworkAvailable() {
                Log.d("Network", "Sẵn sàng thanh toán học phí môn học");
            }
            @Override public void onNetworkLost() {
                Toast.makeText(SubjectTuitionActivity.this,
                        "Mất kết nối mạng! Giao dịch có thể bị gián đoạn.",
                        Toast.LENGTH_LONG).show();
            }
        });
        networkUtils.register();
    }

    private void loadData() {
        String token = SessionManager.getInstance(this).getAuthToken();
        tuitionApi = ApiClient.getInstance(token).create(TuitionApiService.class);

        tuitionApi.getSummary().enqueue(new Callback<ApiResponse<TuitionSummaryResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<TuitionSummaryResponse>> call,
                                   Response<ApiResponse<TuitionSummaryResponse>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess()) {
                    TuitionSummaryResponse summary = response.body().getData();
                    subjectList.clear();
                    unpaidSemesterIds.clear();

                    if (summary != null && summary.semesters != null) {
                        for (TuitionResponse t : summary.semesters) {
                            long   feeId     = t.id           != null ? t.id           : 0L;
                            long   semId     = t.semesterId   != null ? t.semesterId   : 0L;
                            double total     = t.getTotalAmountAsDouble();
                            double paid      = t.getPaidAmountAsDouble();
                            // FIX: dùng remainingAmount từ API (backend tính sẵn),
                            // tránh lỗi khi total/paid bị null hoặc mismatch.
                            double remaining = t.getRemainingAmountAsDouble();
                            String status    = t.status        != null ? t.status        : "";
                            String due       = t.dueDate       != null ? t.dueDate       : "";
                            String paidAt    = t.paidAt        != null ? t.paidAt        : "";
                            String method    = t.paymentMethod != null ? t.paymentMethod : "";
                            String semName   = (t.semesterName != null && !t.semesterName.isEmpty())
                                    ? t.semesterName : "Học kỳ " + semId;

                            // Tạo SubjectTuition với đầy đủ total + paid để
                            // getRemainingAmount() = total - paid tính đúng.
                            int credits = t.totalCredits != null ? t.totalCredits : 0;
                            subjectList.add(new SubjectTuition(
                                    feeId, 0L, semId,
                                    total, paid,
                                    due, status, method, paidAt,
                                    0L, "", semName, credits, ""
                            ));

                            // Track kỳ còn nợ để gọi pay()
                            if (!Tuition.STATUS_PAID.equals(status) && remaining > 0) {
                                unpaidSemesterIds.add(semId);
                            }
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

    private static final long PRICE_PER_CREDIT = 550_000L;

    private void calculateTotal() {
        totalAmount = 0.0;
        for (SubjectTuition subject : subjectList) {
            if (!subject.isPaid()) {
                totalAmount += subject.getRemainingAmount();
            }
        }

        // Fallback: nếu API trả về 0đ, tính từ số tín chỉ lưu local × 550.000
        if (totalAmount == 0.0) {
            totalAmount = calcLocalTotal();
        }

        tvTotalAmount.setText(String.format(Locale.getDefault(), "%,.0f VND", totalAmount));
        btnPay.setVisibility(totalAmount > 0 ? android.view.View.VISIBLE : android.view.View.GONE);
    }

    /** Tính học phí từ local storage: tổng tín chỉ đã đăng ký × 550.000đ */
    private double calcLocalTotal() {
        java.util.List<String> confirmedIds =
                com.utc2.appreborn.ui.courseregistration.model.CourseStorage.loadConfirmedIds(this);
        java.util.Map<String, Integer> creditsMap =
                com.utc2.appreborn.ui.courseregistration.model.CourseStorage.loadCreditsMap(this);
        if (confirmedIds.isEmpty()) return 0.0;

        // Fallback: nếu creditsMap thiếu entry, thử lấy từ CourseRepository (local data)
        com.utc2.appreborn.ui.courseregistration.model.CourseRepository repo =
                com.utc2.appreborn.ui.courseregistration.model.CourseRepository.getInstance();
        int totalCredits = 0;
        for (String id : confirmedIds) {
            Integer tc = creditsMap.get(id);
            if (tc == null) {
                com.utc2.appreborn.ui.courseregistration.model.Course c = repo.findById(id);
                tc = (c != null) ? c.getCredits() : 0;
            }
            totalCredits += tc;
        }
        return totalCredits * PRICE_PER_CREDIT;
    }

    private void setupRecyclerView() {
        rvItems.setLayoutManager(new LinearLayoutManager(this));
        rvItems.setAdapter(new SubjectTuitionAdapter(subjectList));
    }

    private void showPaymentDialog() {
        // Lách: nếu API không trả về semesterId nhưng có tiền local → vẫn cho thanh toán
        if (unpaidSemesterIds.isEmpty() && totalAmount <= 0) {
            Toast.makeText(this, "Không có học phí cần đóng!", Toast.LENGTH_SHORT).show();
            return;
        }

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
            Toast.makeText(this,
                    getString(R.string.msg_checking_transaction), Toast.LENGTH_SHORT).show();
            confirmPaymentToServer(dialog);
        });

        dialog.show();
    }

    /** Gọi pay API nếu có semesterId; nếu không có thì xóa local data coi như đã đóng */
    private void confirmPaymentToServer(Dialog dialog) {
        if (unpaidSemesterIds.isEmpty()) {
            String paidAt = new java.text.SimpleDateFormat(
                    "dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
                    .format(new java.util.Date());
            java.util.List<String> ids =
                    com.utc2.appreborn.ui.courseregistration.model.CourseStorage.loadConfirmedIds(this);
            int tc = 0;
            java.util.Map<String, Integer> cm =
                    com.utc2.appreborn.ui.courseregistration.model.CourseStorage.loadCreditsMap(this);
            com.utc2.appreborn.ui.courseregistration.model.CourseRepository repo =
                    com.utc2.appreborn.ui.courseregistration.model.CourseRepository.getInstance();
            for (String id : ids) {
                Integer c = cm.get(id);
                if (c == null) { com.utc2.appreborn.ui.courseregistration.model.Course co = repo.findById(id); c = co != null ? co.getCredits() : 0; }
                tc += c;
            }
            String label = "Học phí học phần";
            String details = tc + " TC — " + paidAt;
            String invoiceCode = "HP_LOCAL_" + System.currentTimeMillis();

            // Lưu invoice local (KHÔNG clearStorage để invoice không bị mất)
            com.utc2.appreborn.ui.courseregistration.model.CourseStorage
                    .saveLocalInvoice(this, invoiceCode, label, totalAmount, paidAt);
            // Xóa enrollment để số tiền về 0
            com.utc2.appreborn.ui.courseregistration.model.CourseStorage.clearStorage(this);

            if (dialog.isShowing()) dialog.dismiss();
            Toast.makeText(this, getString(R.string.msg_payment_success), Toast.LENGTH_SHORT).show();

            // Thêm card "Đã đóng" vào list ngay, không cần reload API
            subjectList.add(new SubjectTuition(
                    0L, 0L, 0L,
                    totalAmount, totalAmount,
                    "", com.utc2.appreborn.ui.tuition.model.Tuition.STATUS_PAID,
                    "online", paidAt,
                    0L, "", label, tc, details
            ));
            totalAmount = 0.0;
            tvTotalAmount.setText("0 VND");
            btnPay.setVisibility(android.view.View.GONE);
            setupRecyclerView();
            return;
        }
        callPayRecursive(dialog, new ArrayList<>(unpaidSemesterIds), 0);
    }

    private void callPayRecursive(Dialog dialog, List<Long> ids, int index) {
        if (index >= ids.size()) {
            if (dialog.isShowing()) dialog.dismiss();
            Toast.makeText(this,
                    getString(R.string.msg_payment_success), Toast.LENGTH_SHORT).show();
            loadData(); // reload lại để cập nhật UI
            return;
        }
        long semId = ids.get(index);
        tuitionApi.pay(semId, "online").enqueue(new Callback<ApiResponse<TuitionResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<TuitionResponse>> call,
                                   Response<ApiResponse<TuitionResponse>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess()) {
                    callPayRecursive(dialog, ids, index + 1);
                } else {
                    if (dialog.isShowing()) dialog.dismiss();
                    Toast.makeText(SubjectTuitionActivity.this,
                            "Lỗi xác nhận thanh toán kỳ " + semId, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<TuitionResponse>> call, Throwable t) {
                if (dialog.isShowing()) dialog.dismiss();
                Toast.makeText(SubjectTuitionActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkUtils != null) networkUtils.unregister();
    }
}