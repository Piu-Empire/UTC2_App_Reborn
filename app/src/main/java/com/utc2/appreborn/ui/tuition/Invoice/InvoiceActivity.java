package com.utc2.appreborn.ui.tuition.Invoice;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.tuition.model.DormTuition;
import com.utc2.appreborn.ui.tuition.adapter.InvoiceAdapter;
import com.utc2.appreborn.ui.tuition.model.Invoice;
import com.utc2.appreborn.ui.tuition.model.SubjectTuition;
import com.utc2.appreborn.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * InvoiceActivity_2.java
 * Quản lý hiển thị lịch sử hóa đơn và giám sát trạng thái mạng[cite: 13].
 */
public class InvoiceActivity extends AppCompatActivity {

    private RecyclerView rvInvoices;
    private InvoiceAdapter adapter;
    private List<Invoice> invoiceList;
    private NetworkUtils networkUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        try {
            initViews();
            setupNetworkMonitoring();
            loadInvoiceData();
            setupRecyclerView();
        } catch (Exception e) {
            Log.e("InvoiceActivity", "Lỗi khởi tạo: " + e.getMessage());
        }
    }

    private void initViews() {
        // Nút Back - Dùng Expression Lambda để xử lý sự kiện đóng activity[cite: 13].
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        rvInvoices = findViewById(R.id.rvInvoices);
    }

    private void setupNetworkMonitoring() {
        // Khởi tạo giám sát mạng để thông báo cho người dùng khi mất kết nối[cite: 13].
        networkUtils = new NetworkUtils(this, new NetworkUtils.NetworkStatusListener() {
            @Override
            public void onNetworkAvailable() {
                // Có thể thực hiện làm mới dữ liệu từ Server tại đây khi có mạng lại[cite: 13].
                Log.d("Network", "Đã kết nối - Sẵn sàng cập nhật hóa đơn");
            }

            @Override
            public void onNetworkLost() {
                Toast.makeText(InvoiceActivity.this,
                        "Mất kết nối mạng! Lịch sử hóa đơn có thể chưa được cập nhật mới nhất.",
                        Toast.LENGTH_LONG).show();
            }
        });
        networkUtils.register();
    }

    private void setupRecyclerView() {
        // Cấu hình LayoutManager và Adapter để hiển thị danh sách hóa đơn[cite: 13].
        rvInvoices.setLayoutManager(new LinearLayoutManager(this));
        adapter = new InvoiceAdapter(invoiceList);
        rvInvoices.setAdapter(adapter);
    }

    private void loadInvoiceData() {
        invoiceList = new ArrayList<>();

        // Giả lập dữ liệu hóa đơn bao gồm cả học phí môn học và phí ký túc xá (Tính đa hình)[cite: 13].
        try {
            SubjectTuition monHoc = new SubjectTuition(1, "Lập trình Android", "Học kỳ 2", 2500000, 1);
            DormTuition tienPhong = new DormTuition("Phòng 403", "Tháng 03/2026", 1250000, 1);
            SubjectTuition monHoc2 = new SubjectTuition(2, "Cấu trúc dữ liệu", "Học kỳ 1", 650000, 1);

            // Thêm các đối tượng vào danh sách hiển thị[cite: 13].
            invoiceList.add(new Invoice("UTC2_2026_001", "10/04/2026", monHoc));
            invoiceList.add(new Invoice("UTC2_2026_002", "15/03/2026", tienPhong));
            invoiceList.add(new Invoice("UTC2_2026_003", "05/02/2026", monHoc2));
        } catch (Exception e) {
            Log.e("InvoiceData", "Lỗi tạo dữ liệu mẫu: " + e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Quan trọng: Hủy đăng ký lắng nghe mạng để tránh rò rỉ bộ nhớ (Memory Leak)[cite: 13].
        if (networkUtils != null) {
            networkUtils.unregister();
        }
    }
}