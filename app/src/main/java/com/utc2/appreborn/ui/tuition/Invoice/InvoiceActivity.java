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
import com.utc2.appreborn.ui.tuition.adapter.InvoiceAdapter;
import com.utc2.appreborn.ui.tuition.model.Invoice;
import com.utc2.appreborn.utils.NetworkUtils;
import com.utc2.appreborn.utils.SessionManager;

import com.utc2.appreborn.network.ApiClient;
import com.utc2.appreborn.network.ApiResponse;
import com.utc2.appreborn.network.TuitionApiService;
import com.utc2.appreborn.network.dto.TuitionResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class InvoiceActivity extends AppCompatActivity {

    private RecyclerView   rvInvoices;
    private InvoiceAdapter adapter;
    private List<Invoice>  invoiceList = new ArrayList<>();
    private NetworkUtils   networkUtils;

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
            loadInvoiceData();
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
            @Override
            public void onNetworkAvailable() {
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
        rvInvoices.setLayoutManager(new LinearLayoutManager(this));
        adapter = new InvoiceAdapter(invoiceList);
        rvInvoices.setAdapter(adapter);
    }

    private void loadInvoiceData() {
        String token = SessionManager.getInstance(this).getAuthToken();
        TuitionApiService tuitionApi = ApiClient.getInstance(token).create(TuitionApiService.class);

        tuitionApi.getHistory().enqueue(new Callback<ApiResponse<List<TuitionResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<TuitionResponse>>> call,
                                   Response<ApiResponse<List<TuitionResponse>>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess()) {
                    List<TuitionResponse> list = response.body().getData();
                    invoiceList.clear();
                    for (TuitionResponse t : list) {
                        // FIX: null-safe unboxing Long → long
                        long feeId      = t.id         != null ? t.id         : 0L;
                        long semesterId = t.semesterId != null ? t.semesterId : 0L;

                        // FIX NPE: dùng constructor với amount tường minh,
                        // không truyền tuition=null nữa
                        double totalAmount = t.getTotalAmountAsDouble();
                        double paidAmount  = t.getPaidAmountAsDouble();

                        invoiceList.add(new Invoice(
                                "UTC2_" + feeId,
                                feeId,
                                semesterId,
                                t.paidAt        != null ? t.paidAt        : "",
                                t.paymentMethod != null ? t.paymentMethod : "",
                                totalAmount,
                                paidAmount
                        ));
                    }
                    setupRecyclerView();
                } else {
                    Toast.makeText(InvoiceActivity.this,
                            "Không tải được lịch sử hóa đơn", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<TuitionResponse>>> call, Throwable t) {
                Toast.makeText(InvoiceActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkUtils != null) networkUtils.unregister();
    }
}