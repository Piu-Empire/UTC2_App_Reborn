package com.utc2.appreborn.ui.tuition.Invoice;

import android.os.Bundle;
import com.utc2.appreborn.utils.LocaleHelper;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.utc2.appreborn.R;
import com.utc2.appreborn.network.ApiClient;
import com.utc2.appreborn.network.ApiResponse;
import com.utc2.appreborn.network.DormApiService;
import com.utc2.appreborn.network.TuitionApiService;
import com.utc2.appreborn.network.dto.DormRegistrationResponse;
import com.utc2.appreborn.network.dto.TuitionResponse;
import com.utc2.appreborn.ui.tuition.adapter.InvoiceAdapter;
import com.utc2.appreborn.ui.tuition.model.Invoice;
import com.utc2.appreborn.utils.NetworkUtils;
import com.utc2.appreborn.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class InvoiceActivity extends AppCompatActivity {

    private RecyclerView   rvInvoices;
    private NetworkUtils   networkUtils;

    // Kết quả từ 2 API — ghép lại khi cả 2 xong
    private List<TuitionResponse>       tuitionPaid = new ArrayList<>();
    private List<DormRegistrationResponse> dormPaid  = new ArrayList<>();

    @Override
    protected void attachBaseContext(android.content.Context base) {
        super.attachBaseContext(LocaleHelper.applyLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        try {
            initViews();
            setupNetworkMonitoring();
            loadAllInvoices();
        } catch (Exception e) {
            Log.e("InvoiceActivity", "Lỗi khởi tạo: " + e.getMessage());
        }
    }

    private void initViews() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        rvInvoices = findViewById(R.id.rvInvoices);
    }

    private void setupNetworkMonitoring() {
        networkUtils = new NetworkUtils(this, new NetworkUtils.NetworkStatusListener() {
            @Override public void onNetworkAvailable() {}
            @Override public void onNetworkLost() {
                com.utc2.appreborn.utils.CustomToastHelper.showToast(InvoiceActivity.this, "Mất kết nối mạng!");
            }
        });
        networkUtils.register();
    }

    /**
     * Gọi 2 API song song, đợi cả 2 xong rồi render.
     * AtomicInteger đếm số API đã trả về để biết khi nào render.
     */
    private void loadAllInvoices() {
        String token = SessionManager.getInstance(this).getAuthToken();
        TuitionApiService tuitionApi = ApiClient.getInstance(token).create(TuitionApiService.class);
        DormApiService    dormApi    = ApiClient.getInstance(token).create(DormApiService.class);

        AtomicInteger done = new AtomicInteger(0); // đếm API đã xong

        // ── API 1: học phí đã đóng ───────────────────────────
        tuitionApi.getPaid().enqueue(new Callback<ApiResponse<List<TuitionResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<TuitionResponse>>> call,
                                   Response<ApiResponse<List<TuitionResponse>>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess() && response.body().getData() != null) {
                    tuitionPaid = response.body().getData();
                }
                if (done.incrementAndGet() == 2) renderInvoices();
            }
            @Override
            public void onFailure(Call<ApiResponse<List<TuitionResponse>>> call, Throwable t) {
                Log.e("InvoiceActivity", "Lỗi tải học phí: " + t.getMessage());
                if (done.incrementAndGet() == 2) renderInvoices();
            }
        });

        // ── API 2: KTX đã đóng ───────────────────────────────
        dormApi.getMyRegistrations().enqueue(new Callback<ApiResponse<List<DormRegistrationResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<DormRegistrationResponse>>> call,
                                   Response<ApiResponse<List<DormRegistrationResponse>>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess() && response.body().getData() != null) {
                    // Chỉ lấy đăng ký đã đóng tiền
                    for (DormRegistrationResponse dto : response.body().getData()) {
                        if (dto.isPaid()) dormPaid.add(dto);
                    }
                }
                if (done.incrementAndGet() == 2) renderInvoices();
            }
            @Override
            public void onFailure(Call<ApiResponse<List<DormRegistrationResponse>>> call, Throwable t) {
                Log.e("InvoiceActivity", "Lỗi tải KTX: " + t.getMessage());
                if (done.incrementAndGet() == 2) renderInvoices();
            }
        });
    }

    private void renderInvoices() {
        runOnUiThread(() -> {
            List<Object> items = new ArrayList<>();

            // ── Section 1: Học phí môn học ───────────────────
            items.add(getString(R.string.invoice_section_tuition));

            // Local invoices lên đầu
            java.util.List<String[]> localList =
                    com.utc2.appreborn.ui.courseregistration.model.CourseStorage
                            .loadLocalInvoices(this);
            for (String[] inv : localList) {
                double amt = 0;
                try { amt = Double.parseDouble(inv[2]); } catch (Exception ignore) {}
                items.add(new Invoice(Invoice.Type.TUITION, inv[0], 0L, 0L,
                        inv[1], inv[3], "online", amt, amt));
            }

            if (tuitionPaid.isEmpty() && localList.isEmpty()) {
                items.add(getString(R.string.invoice_empty_tuition));
            } else {
                for (TuitionResponse t : tuitionPaid) {
                    long feeId = t.id         != null ? t.id         : 0L;
                    long semId = t.semesterId != null ? t.semesterId : 0L;
                    items.add(new Invoice(Invoice.Type.TUITION, "HP_" + feeId,
                            feeId, semId,
                            getString(R.string.semester_label, semId),
                            t.paidAt        != null ? t.paidAt        : "",
                            t.paymentMethod != null ? t.paymentMethod : "",
                            t.getTotalAmountAsDouble(), t.getPaidAmountAsDouble()));
                }
            }

            // Section 2: Phí ký túc xá
            items.add(getString(R.string.invoice_section_dorm));
            if (dormPaid.isEmpty()) {
                items.add(getString(R.string.invoice_empty_dorm));
            } else {
                for (DormRegistrationResponse dto : dormPaid) {
                    long   regId  = dto.dormRegId != null ? dto.dormRegId : 0L;
                    String label  = (dto.building != null ? dto.building + " \u00b7 " : "")
                            + (dto.roomCode != null ? dto.roomCode : "KTX");
                    items.add(new Invoice(
                            Invoice.Type.DORM,
                            "KTX_" + regId,
                            regId, 0L,
                            label,
                            dto.endDate != null ? dto.endDate : "",
                            "online",
                            dto.getTotalFeeAsDouble(),
                            dto.getTotalFeeAsDouble()
                    ));
                }
            }

            rvInvoices.setLayoutManager(new LinearLayoutManager(this));
            rvInvoices.setAdapter(new InvoiceAdapter(items));
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkUtils != null) networkUtils.unregister();
    }
}