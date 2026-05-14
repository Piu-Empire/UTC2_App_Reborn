package com.utc2.appreborn.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.utc2.appreborn.R;
import com.utc2.appreborn.utils.NetworkUtils;

public class SupportActivity extends AppCompatActivity {

    private AutoCompleteTextView dropLoai;
    private EditText edtContent;
    private RatingBar ratingBar;
    private Button btnSend;
    private ImageButton btnBack;

    private final String[] data = {"Lỗi", "Góp ý"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        initViews();
        setupDropdown();

        // Sự kiện Back
        btnBack.setOnClickListener(v -> finish());

        // Sự kiện Gửi
        btnSend.setOnClickListener(v -> validateAndSend());
    }

    private void initViews() {
        dropLoai = findViewById(R.id.dropLoai);
        edtContent = findViewById(R.id.edtContent);
        ratingBar = findViewById(R.id.ratingBar);
        btnSend = findViewById(R.id.btnSend);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                data
        );
        dropLoai.setAdapter(adapter);

        // Khi chọn item thì cập nhật text và đóng dropdown
        dropLoai.setOnItemClickListener((parent, view, position, id) -> {
            dropLoai.setText(data[position], false);
        });
    }

    private void validateAndSend() {
        // Kiểm tra mạng bằng hàm static để tối ưu tài nguyên
        if (!NetworkUtils.isNetworkAvailable(this)) {
            // Sử dụng string resource để thông báo chuyên nghiệp hơn
            showToast(getString(R.string.error_connect_network));
            return;
        }

        String loai = dropLoai.getText().toString().trim();
        String content = edtContent.getText().toString().trim();
        float rating = ratingBar.getRating();

        if (loai.isEmpty()) {
            dropLoai.setError("Vui lòng chọn loại yêu cầu");
            dropLoai.requestFocus();
            return;
        }

        if (content.isEmpty()) {
            edtContent.setError("Vui lòng nhập nội dung phản hồi");
            edtContent.requestFocus();
            return;
        }

        sendEmail(loai, content, rating);
    }

    private void sendEmail(String loai, String content, float rating) {
        String danhGia;
        // Chuyển đổi mức sao sang văn bản để người nhận email dễ hiểu
        if (rating >= 5) danhGia = "Rất tốt";
        else if (rating >= 4) danhGia = "Tốt";
        else if (rating >= 3) danhGia = "Trung bình";
        else if (rating >= 2) danhGia = "Kém";
        else if (rating >= 1) danhGia = "Rất kém";
        else danhGia = "Chưa đánh giá";

        // Tạo nội dung email gửi đến bộ phận hỗ trợ
        String message = "Phản hồi từ sinh viên UTC2" +
                "\nLoại yêu cầu: " + loai +
                "\nĐánh giá ứng dụng: " + danhGia + " (" + rating + "/5.0)" +
                "\n\nNội dung chi tiết:\n" + content;

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"hinhvinhphat@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "[AppReborn Support] - " + loai);
        intent.putExtra(Intent.EXTRA_TEXT, message);

        try {
            startActivity(Intent.createChooser(intent, "Chọn ứng dụng Email để gửi"));
        } catch (Exception e) {
            showToast("Không tìm thấy ứng dụng Email trên thiết bị này");
        }
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}