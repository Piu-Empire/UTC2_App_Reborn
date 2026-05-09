package com.utc2.appreborn.ui.public_services;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.public_services.model.CardReissueService;
import com.utc2.appreborn.ui.public_services.model.LoanSupportService;
import com.utc2.appreborn.ui.public_services.model.StudentConfirmationService;
import com.utc2.appreborn.ui.public_services.model.TranscriptService;
import com.utc2.appreborn.ui.public_services.model.BaseService;
import com.utc2.appreborn.utils.NetworkUtils;

public class ServiceDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvStatus, tvTime;
    private LinearLayout layoutDynamicContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_detail);

        try {
            initViews();

            // Kiểm tra mạng nhanh khi vừa mở trang chi tiết dịch vụ
            if (!NetworkUtils.isNetworkAvailable(this)) {
                showToast("Thông tin đang hiển thị ngoại tuyến.");
            }

            BaseService service = (BaseService) getIntent().getSerializableExtra("SERVICE_DATA");
            if (service != null) {
                populateData(service);
            }

            findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        } catch (Exception e) {
            Log.e("ServiceDetail", "Lỗi khởi tạo: " + e.getMessage());
        }
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tvDetailTitle);
        tvStatus = findViewById(R.id.tvStatusBadge);
        tvTime = findViewById(R.id.tvTime);
        layoutDynamicContent = findViewById(R.id.layoutDynamicContent);
    }

    private void populateData(BaseService service) {
        tvTitle.setText(service.getTitle());
        tvTime.setText(getString(R.string.date_placeholder, service.getDate()));

        // Thiết lập Badge trạng thái dịch vụ công
        if (service.getStatus() == 1) {
            tvStatus.setText(R.string.status_approved);
            tvStatus.setBackgroundResource(R.drawable.bg_status_done);
        } else {
            tvStatus.setText(R.string.status_pending);
            tvStatus.setBackgroundResource(R.drawable.bg_status_pending);
        }

        layoutDynamicContent.removeAllViews();

        // Xử lý hiển thị thông tin chi tiết dựa trên từng loại dịch vụ[cite: 2]
        if (service instanceof CardReissueService) {
            CardReissueService s = (CardReissueService) service;
            addInfoRow(getString(R.string.name_title), s.getStudentName());
            addInfoRow(getString(R.string.id_title), s.getStudentId());
            addInfoRow(getString(R.string.class_title), s.getClassName());
            addInfoRow("Lý do", s.getDescription());
        }
        else if (service instanceof LoanSupportService) {
            LoanSupportService s = (LoanSupportService) service;
            addInfoRow(getString(R.string.loan_amount), s.getLoanAmount());
            addInfoRow(getString(R.string.contact_number_title), s.getPhoneNumber());
            addInfoRow("Ghi chú", s.getDescription());
        }
        else if (service instanceof TranscriptService) {
            TranscriptService s = (TranscriptService) service;
            addInfoRow(getString(R.string.name_title), s.getStudentName());
            addInfoRow(getString(R.string.id_title), s.getStudentId());
            addInfoRow(getString(R.string.class_title), s.getClassName());
            addInfoRow(getString(R.string.transcript_academic_year), s.getAcademicYear());
            addInfoRow(getString(R.string.transcript_semester), s.getSemester());
            addInfoRow(getString(R.string.transcript_quantity), s.getQuantity());
        }
        else if (service instanceof StudentConfirmationService) {
            StudentConfirmationService s = (StudentConfirmationService) service;
            addInfoRow(getString(R.string.name_title), s.getStudentName());
            addInfoRow(getString(R.string.id_title), s.getStudentId());
            addInfoRow(getString(R.string.class_title), s.getClassName());
            addInfoRow("Lý do xác nhận", s.getDescription());
        }
    }

    private void addInfoRow(String label, String value) {
        View rowView = LayoutInflater.from(this).inflate(R.layout.item_detail_info_row, layoutDynamicContent, false);
        TextView tvLabel = rowView.findViewById(R.id.tvLabel);
        TextView tvValue = rowView.findViewById(R.id.tvValue);

        tvLabel.setText(label);
        tvValue.setText(value != null && !value.isEmpty() ? value : "---");

        layoutDynamicContent.addView(rowView);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}