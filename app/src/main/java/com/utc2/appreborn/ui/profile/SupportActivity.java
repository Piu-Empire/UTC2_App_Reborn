package com.utc2.appreborn.ui.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.utc2.appreborn.R;
import com.utc2.appreborn.network.ApiClient;
import com.utc2.appreborn.network.ApiResponse;
import com.utc2.appreborn.network.InteractionApiService;
import com.utc2.appreborn.network.dto.FeedbackRequest;
import com.utc2.appreborn.network.dto.FeedbackResponse;
import com.utc2.appreborn.ui.profile.adapter.FeedbackHistoryAdapter;
import com.utc2.appreborn.ui.profile.model.FeedbackItem;
import com.utc2.appreborn.utils.LocaleHelper;
import com.utc2.appreborn.utils.NetworkUtils;
import com.utc2.appreborn.utils.SessionManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SupportActivity extends AppCompatActivity {

    private static final String TAG         = "SupportActivity";
    private static final String FMT_PARSE   = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String FMT_DISPLAY = "dd/MM/yyyy HH:mm";

    // Form
    private AutoCompleteTextView dropType;
    private EditText             edtContent;
    private Button               btnSend;
    private ImageButton          btnBack;

    // Lịch sử
    private RecyclerView           rvHistory;
    private ProgressBar            progressHistory;
    private TextView               tvEmptyHistory;
    private FeedbackHistoryAdapter historyAdapter;

    private InteractionApiService api;

    private final String[] typeOptions = {"Lỗi", "Góp ý"};

    @Override
    protected void attachBaseContext(android.content.Context base) {
        super.attachBaseContext(LocaleHelper.applyLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        String token = SessionManager.getInstance(this).getAuthToken();
        api = ApiClient.getInstance(token).create(InteractionApiService.class);

        initViews();
        setupDropdown();
        setupRecyclerView();

        btnBack.setOnClickListener(v -> finish());
        btnSend.setOnClickListener(v -> validateAndSend());

        loadHistory();
    }

    private void initViews() {
        dropType       = findViewById(R.id.dropLoai);
        edtContent     = findViewById(R.id.edtContent);
        btnSend        = findViewById(R.id.btnSend);
        btnBack        = findViewById(R.id.btnBack);
        rvHistory      = findViewById(R.id.rvFeedbackHistory);
        progressHistory = findViewById(R.id.progressHistory);
        tvEmptyHistory = findViewById(R.id.tvEmptyHistory);
    }

    private void setupDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, typeOptions);
        dropType.setAdapter(adapter);
        dropType.setOnItemClickListener((parent, view, position, id) ->
                dropType.setText(typeOptions[position], false));
    }

    private void setupRecyclerView() {
        historyAdapter = new FeedbackHistoryAdapter(item ->
                Log.d(TAG, "Click: " + item.getId()));
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setNestedScrollingEnabled(false);
        rvHistory.setAdapter(historyAdapter);
    }

    // ── Gửi ──────────────────────────────────────────────────

    private void validateAndSend() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            showToast(getString(R.string.error_connect_network));
            return;
        }

        String type    = dropType.getText().toString().trim();
        String content = edtContent.getText().toString().trim();

        if (type.isEmpty()) {
            dropType.setError("Vui lòng chọn loại yêu cầu");
            dropType.requestFocus();
            return;
        }
        if (content.isEmpty()) {
            edtContent.setError("Vui lòng nhập nội dung phản hồi");
            edtContent.requestFocus();
            return;
        }

        sendFeedbackToBackend(type, content);
    }

    private void sendFeedbackToBackend(String type, String content) {
        btnSend.setEnabled(false);

        api.sendFeedback(new FeedbackRequest(type, content))
                .enqueue(new Callback<ApiResponse<FeedbackResponse>>() {

                    @Override
                    public void onResponse(Call<ApiResponse<FeedbackResponse>> call,
                                           Response<ApiResponse<FeedbackResponse>> response) {
                        btnSend.setEnabled(true);
                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()) {

                            showToast("Phản hồi của bạn đã được gửi. Cảm ơn bạn!");
                            clearForm();
                            loadHistory();
                        } else {
                            String msg = response.body() != null
                                    ? response.body().getMessage()
                                    : "Gửi thất bại (HTTP " + response.code() + ")";
                            showToast(msg);
                            Log.e(TAG, "sendFeedback lỗi: " + msg);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<FeedbackResponse>> call, Throwable t) {
                        btnSend.setEnabled(true);
                        showToast("Lỗi kết nối: " + t.getMessage());
                        Log.e(TAG, "sendFeedback onFailure: " + t.getMessage());
                    }
                });
    }

    // ── Load lịch sử ─────────────────────────────────────────

    private void loadHistory() {
        progressHistory.setVisibility(View.VISIBLE);
        tvEmptyHistory.setVisibility(View.GONE);

        api.myFeedbacks().enqueue(new Callback<ApiResponse<List<FeedbackResponse>>>() {

            @Override
            public void onResponse(Call<ApiResponse<List<FeedbackResponse>>> call,
                                   Response<ApiResponse<List<FeedbackResponse>>> response) {
                progressHistory.setVisibility(View.GONE);
                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().isSuccess()) {

                    List<FeedbackItem> items = mapToItems(response.body().getData());
                    historyAdapter.submitList(items);
                    tvEmptyHistory.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    tvEmptyHistory.setVisibility(View.VISIBLE);
                    tvEmptyHistory.setText("Không thể tải lịch sử phản hồi");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<FeedbackResponse>>> call, Throwable t) {
                progressHistory.setVisibility(View.GONE);
                tvEmptyHistory.setVisibility(View.VISIBLE);
                tvEmptyHistory.setText("Lỗi kết nối");
                Log.e(TAG, "loadHistory onFailure: " + t.getMessage());
            }
        });
    }

    // ── Helpers ───────────────────────────────────────────────

    private List<FeedbackItem> mapToItems(List<FeedbackResponse> responses) {
        List<FeedbackItem> items = new ArrayList<>();
        if (responses == null) return items;

        SimpleDateFormat parser  = new SimpleDateFormat(FMT_PARSE,   Locale.getDefault());
        SimpleDateFormat display = new SimpleDateFormat(FMT_DISPLAY, Locale.getDefault());

        for (FeedbackResponse r : responses) {
            String timeLabel = "";
            if (r.submittedAt != null && !r.submittedAt.isEmpty()) {
                try {
                    String raw = r.submittedAt.contains(".")
                            ? r.submittedAt.substring(0, r.submittedAt.indexOf('.'))
                            : r.submittedAt;
                    Date date = parser.parse(raw);
                    timeLabel = date != null ? display.format(date) : r.submittedAt;
                } catch (ParseException e) {
                    timeLabel = r.submittedAt;
                }
            }
            items.add(new FeedbackItem(
                    r.id != null ? r.id : 0L,
                    r.type,
                    r.content,
                    r.status != null ? r.status : "chưa đọc",
                    r.adminReply,
                    timeLabel
            ));
        }
        return items;
    }

    private void clearForm() {
        dropType.setText("", false);
        edtContent.setText("");
    }

    private void showToast(String msg) {
        com.utc2.appreborn.utils.CustomToastHelper.showToast(this, msg);
    }
}