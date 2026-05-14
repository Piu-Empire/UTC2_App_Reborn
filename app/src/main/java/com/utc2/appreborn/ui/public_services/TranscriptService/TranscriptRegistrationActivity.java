package com.utc2.appreborn.ui.public_services.TranscriptService;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.public_services.model.TranscriptService;
import com.utc2.appreborn.utils.NetworkUtils;

public class TranscriptRegistrationActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView txtName, txtMSSV, txtClass, btnConfirm;
    private AutoCompleteTextView dropAcademicYear, dropSemester;
    private EditText edtQuantity, edtNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transcript_registration);

        try {
            initViews();
            setupData();
            setupDropdowns();
            setupEvents();
        } catch (Exception e) {
            Log.e("TranscriptReg", "Lỗi khởi tạo: " + e.getMessage());
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        txtName = findViewById(R.id.txtName);
        txtMSSV = findViewById(R.id.txtMSSV);
        txtClass = findViewById(R.id.txtClass);
        dropAcademicYear = findViewById(R.id.dropAcademicYear);
        dropSemester = findViewById(R.id.dropSemester);
        edtQuantity = findViewById(R.id.edtQuantity);
        edtNote = findViewById(R.id.edtNote);
        btnConfirm = findViewById(R.id.btnConfirm);
    }

    private void setupData() {
        txtName.setText(getString(R.string.default_name));
        txtMSSV.setText(getString(R.string.default_mssv));
        txtClass.setText(getString(R.string.default_class));
    }

    private void setupDropdowns() {
        String[] academicYears = {"2023 - 2024", "2024 - 2025", "Tất cả các năm"};
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, academicYears);
        dropAcademicYear.setAdapter(yearAdapter);

        String[] semesters = {"Học kỳ 1", "Học kỳ 2"};
        ArrayAdapter<String> semesterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, semesters);
        dropSemester.setAdapter(semesterAdapter);

        dropAcademicYear.setOnClickListener(v -> dropAcademicYear.showDropDown());
        dropSemester.setOnClickListener(v -> dropSemester.showDropDown());
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());
        btnConfirm.setOnClickListener(v -> {
            if (!NetworkUtils.isNetworkAvailable(this)) {
                Toast.makeText(this, "Không có mạng! Vui lòng kết nối để gửi yêu cầu.", Toast.LENGTH_SHORT).show();
                return;
            }

            String academicYear = dropAcademicYear.getText().toString();
            String semester = dropSemester.getText().toString();
            String quantity = edtQuantity.getText().toString();

            if (academicYear.isEmpty() || semester.isEmpty() || quantity.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show();
                return;
            }

            // Logic xử lý gửi bảng điểm[cite: 6]
            Toast.makeText(this, "Đăng ký bảng điểm thành công!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}