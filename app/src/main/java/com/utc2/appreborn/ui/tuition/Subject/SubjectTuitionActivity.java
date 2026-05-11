package com.utc2.appreborn.ui.tuition.Subject;

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
import com.utc2.appreborn.ui.tuition.adapter.SubjectTuitionAdapter;
import com.utc2.appreborn.ui.tuition.model.SubjectTuition;
import com.utc2.appreborn.ui.tuition.model.Tuition;
import com.utc2.appreborn.utils.NetworkUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SubjectTuitionActivity extends AppCompatActivity {

    private RecyclerView rvItems;
    private List<SubjectTuition> subjectList;
    private TextView tvTotalAmount;
    private Button btnPay;
    private long totalAmount = 0;
    private NetworkUtils networkUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_tuition);

        try {
            initViews();
            setupNetworkMonitoring();
            loadData();
            calculateTotal();
            setupRecyclerView();
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

    private void loadData() {
        subjectList = new ArrayList<>();
        subjectList.add(new SubjectTuition(1, "Lập trình Android", "3 tín chỉ", 1250000, Tuition.STATUS_UNPAID));
        subjectList.add(new SubjectTuition(2, "Cấu trúc dữ liệu", "4 tín chỉ", 1600000, Tuition.STATUS_UNPAID));
        subjectList.add(new SubjectTuition(3, "Anh văn chuyên ngành", "2 tín chỉ", 850000, Tuition.STATUS_UNPAID));
    }

    private void calculateTotal() {
        totalAmount = 0;
        for (SubjectTuition subject : subjectList) {
            // Chỉ tính khoản chưa đóng — STATUS_UNPAID hoặc STATUS_PARTIAL
            if (!subject.isPaid()) {
                totalAmount += subject.getAmount();
            }
        }
        tvTotalAmount.setText(String.format(Locale.getDefault(), "%,d VND", totalAmount));
    }

    private void setupRecyclerView() {
        rvItems.setLayoutManager(new LinearLayoutManager(this));
        SubjectTuitionAdapter adapter = new SubjectTuitionAdapter(subjectList);
        rvItems.setAdapter(adapter);
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
        // SỬA: đúng ID trong layout_payment_qr.xml là tvDialogAmount
        TextView tvDialogAmount = dialog.findViewById(R.id.tvDialogAmount);
        Button btnConfirm = dialog.findViewById(R.id.btnConfirmPayment);

        tvDialogAmount.setText(String.format(Locale.getDefault(), "%,d VND", totalAmount));

        String bankId = "ICB";
        String accountNo = "102882730986";
        String accountName = "HINH%20VINH%20PHAT";
        String description = "AppReborn%20Hoc%20phi%20mon%20hoc";

        String qrUrl = "https://img.vietqr.io/image/" + bankId + "-" + accountNo + "-compact.png"
                + "?amount=" + totalAmount
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