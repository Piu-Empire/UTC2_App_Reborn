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

            if (!NetworkUtils.isNetworkAvailable(this)) {
                showToast(getString(R.string.offline_message));
            }

            BaseService service = (BaseService) getIntent().getSerializableExtra("SERVICE_DATA");
            if (service != null) {
                populateData(service);
            }

            findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        } catch (Exception e) {
            Log.e("ServiceDetail", "Loi khoi tao: " + e.getMessage());
        }
    }

    private void initViews() {
        tvTitle             = findViewById(R.id.tvDetailTitle);
        tvStatus            = findViewById(R.id.tvStatusBadge);
        tvTime              = findViewById(R.id.tvTime);
        layoutDynamicContent = findViewById(R.id.layoutDynamicContent);
    }

    private void populateData(BaseService service) {
        tvTitle.setText(service.getTitle());
        tvTime.setText(getString(R.string.date_placeholder, service.getDate()));

        String status = service.getStatus();
        if (BaseService.STATUS_COMPLETED.equals(status)) {      // FIX: STATUS_DONE → STATUS_COMPLETED
            tvStatus.setText(R.string.status_approved);
            tvStatus.setBackgroundResource(R.drawable.bg_status_done);
        } else if (BaseService.STATUS_REJECTED.equals(status)) {
            tvStatus.setText(R.string.status_rejected);
            tvStatus.setBackgroundResource(R.drawable.bg_status_rejected);
        } else {
            // STATUS_PENDING hoac STATUS_PROCESSING
            tvStatus.setText(R.string.status_pending);
            tvStatus.setBackgroundResource(R.drawable.bg_status_pending);
        }

        layoutDynamicContent.removeAllViews();

        if (service instanceof CardReissueService) {
            CardReissueService s = (CardReissueService) service;
            addInfoRow(getString(R.string.name_title),  s.getStudentName());
            addInfoRow(getString(R.string.id_title),    s.getStudentCode());
            addInfoRow(getString(R.string.class_title), s.getClassName());
            addInfoRow(getString(R.string.reason_title), s.getDescription());

        } else if (service instanceof LoanSupportService) {
            LoanSupportService s = (LoanSupportService) service;
            addInfoRow(getString(R.string.loan_amount),          s.getLoanAmount());
            addInfoRow(getString(R.string.contact_number_title), s.getPhoneNumber());
            addInfoRow(getString(R.string.note_title),           s.getDescription());

        } else if (service instanceof TranscriptService) {
            TranscriptService s = (TranscriptService) service;
            addInfoRow(getString(R.string.name_title),               s.getStudentName());
            addInfoRow(getString(R.string.id_title),                 s.getStudentCode());
            addInfoRow(getString(R.string.class_title),              s.getClassName());
            addInfoRow(getString(R.string.transcript_academic_year), s.getAcademicYear());
            addInfoRow(getString(R.string.transcript_semester),      s.getSemester());
            addInfoRow(getString(R.string.transcript_quantity),      String.valueOf(s.getQuantity())); // FIX: int → String

        } else if (service instanceof StudentConfirmationService) {
            StudentConfirmationService s = (StudentConfirmationService) service;
            addInfoRow(getString(R.string.name_title),         s.getStudentName());
            addInfoRow(getString(R.string.id_title),           s.getStudentCode());
            addInfoRow(getString(R.string.class_title),        s.getClassName());
            addInfoRow(getString(R.string.reason_title),       s.getDescription());
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