package com.utc2.appreborn.ui.tuition.Dorm;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DormitoryTuitionActivity extends AppCompatActivity {

    private RecyclerView rvDormTuition;
    private List<DormTuition> dormList;
    private Button btnPayDorm;
    private NetworkUtils networkUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dormitory_tuition);

        try {
            initViews();
            setupNetworkMonitoring();
            loadDormData();
            calculateTotal();
            setupRecyclerView();
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

    private void loadDormData() {
        dormList = new ArrayList<>();
        // dormRegId, roomName, details, totalFee, status — dùng hằng Tuition.STATUS_*
        // Mapping: DORMITORY_REGISTRATION.dorm_reg_id, DORMITORY_ROOM.room_code
        dormList.add(new DormTuition(1, "Phòng 402 - Dãy B", "Tháng 03/2026 - Điện nước", 650000, Tuition.STATUS_UNPAID));
        dormList.add(new DormTuition(2, "Phòng 402 - Dãy B", "Tháng 02/2026 - Điện nước", 720000, Tuition.STATUS_UNPAID));
        dormList.add(new DormTuition(3, "Phòng 402 - Dãy B", "Học kỳ 2 - Tiền phòng",    1500000, Tuition.STATUS_PAID));
    }

    private void calculateTotal() {
        for (DormTuition item : dormList) {
            // Chỉ tính khoản chưa đóng — STATUS_UNPAID hoặc STATUS_PARTIAL
            if (!item.isPaid()) {
                totalAmount += item.getAmount();
            }
        }
    }

    private void setupRecyclerView() {
        rvDormTuition.setLayoutManager(new LinearLayoutManager(this));
        DormAdapter adapter = new DormAdapter(dormList);
        rvDormTuition.setAdapter(adapter);
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

        String bankId = "ICB";
        String accountNo = "102882730986";
        String accountName = "HINH%20VINH%20PHAT";
        // Đổi description cho KTX
        String description = "AppReborn%20Tien%20KTX";

        String qrUrl = "https://img.vietqr.io/image/" + bankId + "-" + accountNo + "-compact.png"
                + "?amount=" + (long) totalAmount
                + "&addInfo=" + description
                + "&accountName=" + accountName;

        Glide.with(this)
                .load(qrUrl)
                .placeholder(R.drawable.logo_utc2)
                .into(imgQr);

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