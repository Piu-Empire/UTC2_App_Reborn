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
import com.utc2.appreborn.network.dto.DormRegistrationResponse;
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
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
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
        dormApi = ApiClient.getInstance(token).create(DormApiService.class);

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
                    calculateTotal();
                    setupRecyclerView();
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

    private void calculateTotal() {
        totalAmount = 0.0;
        for (DormTuition item : dormList) {
            // FIX: isPaid() trong DormTuition so sánh status với Tuition.STATUS_PAID ("đã đóng đủ")
            // nhưng paid_status của KTX là "đã đóng" → kiểm tra getDormPaidStatus() trực tiếp
            if (!"đã đóng".equals(item.getDormPaidStatus())) {
                totalAmount += item.getRemainingAmount();
            }
        }
        if (tvTotalAmount != null) {
            tvTotalAmount.setText(String.format(Locale.getDefault(), "%,.0f VND", totalAmount));
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