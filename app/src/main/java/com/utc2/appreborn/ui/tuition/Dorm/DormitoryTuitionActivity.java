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
import com.utc2.appreborn.utils.NetworkUtils;
import com.utc2.appreborn.utils.SessionManager;

import com.utc2.appreborn.network.ApiClient;
import com.utc2.appreborn.network.ApiResponse;
import com.utc2.appreborn.network.DormApiService;
import com.utc2.appreborn.network.TuitionApiService;
import com.utc2.appreborn.network.dto.DormRegistrationResponse;
import com.utc2.appreborn.network.dto.TuitionResponse;
import com.utc2.appreborn.network.dto.TuitionSummaryResponse;
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
    private TextView tvTotalAmount;
    private double totalAmount = 0.0;
    private NetworkUtils networkUtils;
    private DormApiService dormApi;
    private TuitionApiService tuitionApi;

    // dormRegId chưa đóng — dùng để gọi pay() sau khi xác nhận QR
    private long unpaidDormRegId = -1L;

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
        btnPayDorm    = findViewById(R.id.btnPayDorm);
        tvTotalAmount = findViewById(R.id.tvTotalDormAmount);
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

    private void loadDormData() {
        String token = SessionManager.getInstance(this).getAuthToken();
        dormApi    = ApiClient.getInstance(token).create(DormApiService.class);
        tuitionApi = ApiClient.getInstance(token).create(TuitionApiService.class);

        // Lấy thông tin đăng ký KTX (phòng, ngày, trạng thái)
        dormApi.getMyRegistrations().enqueue(new Callback<ApiResponse<List<DormRegistrationResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<DormRegistrationResponse>>> call,
                                   Response<ApiResponse<List<DormRegistrationResponse>>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess()) {
                    List<DormRegistrationResponse> list = response.body().getData();
                    dormList.clear();
                    unpaidDormRegId = -1L;
                    if (list != null) {
                        for (DormRegistrationResponse dto : list) {
                            long   regId    = dto.dormRegId      != null ? dto.dormRegId      : 0L;
                            long   roomId   = dto.roomId         != null ? dto.roomId         : 0L;
                            String roomName = dto.roomCode       != null ? dto.roomCode       : "";
                            String building = dto.building       != null ? dto.building       : "";
                            double price    = dto.pricePerMonth  != null ? dto.pricePerMonth  : 0.0;
                            String start    = dto.startDate      != null ? dto.startDate      : "";
                            String end      = dto.endDate        != null ? dto.endDate        : "";
                            String regSt    = dto.status         != null ? dto.status         : "";
                            double totalFee = dto.getTotalFeeAsDouble();
                            String paidSt   = dto.paidStatus     != null ? dto.paidStatus     : "chưa đóng";

                            dormList.add(new DormTuition(
                                    0L, 0L, 0L,
                                    totalFee, dto.isPaid() ? totalFee : 0.0,
                                    end, paidSt, "", "",
                                    regId, roomId,
                                    roomName, building,
                                    price, start, end,
                                    regSt, totalFee, paidSt
                            ));

                            // Track đăng ký KTX chưa đóng và đã duyệt để gọi pay()
                            if (!dto.isPaid() && "đã duyệt".equals(regSt) && unpaidDormRegId == -1L) {
                                unpaidDormRegId = regId;
                            }
                        }
                    }
                    // Sau khi có danh sách đăng ký, gọi tiếp API tuition để lấy số tiền thực từ bảng fee
                    loadDormFeeAmounts();
                } else {
                    Toast.makeText(DormitoryTuitionActivity.this,
                            "Không tải được dữ liệu KTX", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<DormRegistrationResponse>>> call, Throwable t) {
                Toast.makeText(DormitoryTuitionActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDormFeeAmounts() {
        tuitionApi.getSummary().enqueue(new Callback<ApiResponse<TuitionSummaryResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<TuitionSummaryResponse>> call,
                                   Response<ApiResponse<TuitionSummaryResponse>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess()) {
                    TuitionSummaryResponse summary = response.body().getData();
                    List<TuitionResponse> dormFees = summary != null ? summary.dormitory : null;
                    if (dormFees != null && !dormFees.isEmpty()) {
                        // Build map dormRegId → fee để map chính xác, tránh lệch index
                        java.util.Map<Long, TuitionResponse> feeByRegId = new java.util.HashMap<>();
                        for (TuitionResponse fee : dormFees) {
                            if (fee.dormRegId != null) {
                                feeByRegId.put(fee.dormRegId, fee);
                            }
                        }

                        for (int i = 0; i < dormList.size(); i++) {
                            DormTuition item = dormList.get(i);
                            TuitionResponse fee = feeByRegId.get(item.getDormRegId());
                            if (fee == null) continue; // chưa có fee record cho đăng ký này

                            double feeTotal  = fee.getTotalAmountAsDouble();
                            double feePaid   = fee.getPaidAmountAsDouble();
                            String feeStatus = fee.status != null ? fee.status : item.getDormPaidStatus();
                            dormList.set(i, new DormTuition(
                                    fee.id != null ? fee.id : 0L,
                                    0L, 0L,
                                    feeTotal, feePaid,
                                    fee.dueDate != null ? fee.dueDate : item.getDueDate(),
                                    feeStatus, "", "",
                                    item.getDormRegId(), item.getRoomId(),
                                    item.getName(), item.getBuilding(),
                                    item.getPricePerMonth(),
                                    item.getStartDate(), item.getEndDate(),
                                    item.getRegStatus(), feeTotal, feeStatus
                            ));
                        }
                    }
                }
                calculateTotal();
                setupRecyclerView();
            }

            @Override
            public void onFailure(Call<ApiResponse<TuitionSummaryResponse>> call, Throwable t) {
                calculateTotal();
                setupRecyclerView();
            }
        });
    }

    private void calculateTotal() {
        totalAmount = 0.0;
        boolean hasApprovedUnpaid = false;

        for (DormTuition item : dormList) {
            // CHỈ tính tiền cho đăng ký ĐÃ ĐƯỢC ADMIN DUYỆT và chưa đóng
            if (item.isApproved() && !DormTuition.DORM_PAY_PAID.equals(item.getDormPaidStatus())) {
                totalAmount += item.getRemainingAmount();
                hasApprovedUnpaid = true;
            }
        }

        // Hiện tổng tiền + nút thanh toán CHỈ khi có đăng ký đã duyệt chưa đóng
        if (tvTotalAmount != null) {
            if (hasApprovedUnpaid) {
                tvTotalAmount.setText(String.format(Locale.getDefault(), "%,.0f VND", totalAmount));
            } else {
                boolean hasPending = false;
                for (DormTuition item : dormList) {
                    if (item.isPendingReg()) { hasPending = true; break; }
                }
                tvTotalAmount.setText(hasPending
                        ? "Đang chờ admin duyệt"
                        : "—");
            }
        }

        // Nút thanh toán: ẩn khi chưa có đăng ký nào được duyệt và chưa đóng
        if (btnPayDorm != null) {
            btnPayDorm.setVisibility(hasApprovedUnpaid
                    ? android.view.View.VISIBLE
                    : android.view.View.GONE);
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

        ImageView imgQr          = dialog.findViewById(R.id.imgQrCode);
        TextView  tvDialogAmount = dialog.findViewById(R.id.tvDialogAmount);
        Button    btnConfirm     = dialog.findViewById(R.id.btnConfirmPayment);

        tvDialogAmount.setText(String.format(Locale.getDefault(), "%,.0f VND", totalAmount));

        String qrUrl = "https://img.vietqr.io/image/ICB-102882730986-compact.png"
                + "?amount=" + (long) totalAmount
                + "&addInfo=AppReborn%20Tien%20KTX"
                + "&accountName=HINH%20VINH%20PHAT";

        Glide.with(this).load(qrUrl).placeholder(R.drawable.logo_utc2).into(imgQr);

        btnConfirm.setOnClickListener(v -> {
            if (unpaidDormRegId == -1L) {
                dialog.dismiss();
                return;
            }
            btnConfirm.setEnabled(false);
            Toast.makeText(this, getString(R.string.msg_checking_transaction), Toast.LENGTH_SHORT).show();

            // Gọi POST /api/v1/dormitory/pay/{dormRegId}
            dormApi.pay(unpaidDormRegId).enqueue(new Callback<ApiResponse<DormRegistrationResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<DormRegistrationResponse>> call,
                                       Response<ApiResponse<DormRegistrationResponse>> response) {
                    if (dialog.isShowing()) dialog.dismiss();
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        Toast.makeText(DormitoryTuitionActivity.this,
                                getString(R.string.msg_payment_success), Toast.LENGTH_SHORT).show();
                        loadDormData(); // reload lại
                    } else {
                        Toast.makeText(DormitoryTuitionActivity.this,
                                "Lỗi xác nhận thanh toán KTX", Toast.LENGTH_SHORT).show();
                        btnConfirm.setEnabled(true);
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<DormRegistrationResponse>> call, Throwable t) {
                    if (dialog.isShowing()) dialog.dismiss();
                    Toast.makeText(DormitoryTuitionActivity.this,
                            "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    btnConfirm.setEnabled(true);
                }
            });
        });

        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkUtils != null) networkUtils.unregister();
    }
}