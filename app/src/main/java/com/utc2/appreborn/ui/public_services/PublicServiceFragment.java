package com.utc2.appreborn.ui.public_services;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.utc2.appreborn.R;
import com.utc2.appreborn.network.ApiClient;
import com.utc2.appreborn.network.ApiResponse;
import com.utc2.appreborn.network.PublicServicesApiService;
import com.utc2.appreborn.network.dto.ServiceRequestResponse;
import com.utc2.appreborn.ui.public_services.CardReissueService.CardReissueActivity;
import com.utc2.appreborn.ui.public_services.LoanSupportService.LoanSupportActivity;
import com.utc2.appreborn.ui.public_services.StudentConfirmationService.StudentConfirmationActivity;
import com.utc2.appreborn.ui.public_services.TranscriptService.TranscriptRegistrationActivity;
import com.utc2.appreborn.ui.public_services.adapter.PublicServiceAdapter;
import com.utc2.appreborn.ui.public_services.model.BaseService;
import com.utc2.appreborn.ui.public_services.model.CardReissueService;
import com.utc2.appreborn.ui.public_services.model.LoanSupportService;
import com.utc2.appreborn.ui.public_services.model.StudentConfirmationService;
import com.utc2.appreborn.ui.public_services.model.TranscriptService;
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

public class PublicServiceFragment extends Fragment {

    private static final String TAG = "PublicServiceFragment";

    private ScrollView    layoutDichVuMenu;
    private RecyclerView  rvKetQua;
    private TextView      btnDichVu, btnKetQua, txtSectionTitle;
    private ProgressBar   progressBar;   // thêm id="progressBar" vào layout nếu chưa có
    private PublicServiceAdapter adapter;
    private final List<BaseService> historyList = new ArrayList<>();

    // Format ISO trả về từ backend: "yyyy-MM-dd'T'HH:mm:ss"
    private static final SimpleDateFormat ISO_FMT =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_public_service, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            initViews(view);
            setupRecyclerView();
            setupEvents(view);
            showTabDichVu();
        } catch (Exception e) {
            Log.e(TAG, "Loi khoi tao: " + e.getMessage());
        }
    }

    private void initViews(View view) {
        layoutDichVuMenu = view.findViewById(R.id.layoutDichVuMenu);
        rvKetQua         = view.findViewById(R.id.rvKetQua);
        btnDichVu        = view.findViewById(R.id.btnDichVu);
        btnKetQua        = view.findViewById(R.id.btnKetQua);
        txtSectionTitle  = view.findViewById(R.id.sectionTitle);
        progressBar      = view.findViewById(R.id.progressBar); // nullable – ok nếu layout chưa có
    }

    private void setupRecyclerView() {
        rvKetQua.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void setupEvents(View view) {
        btnDichVu.setOnClickListener(v -> showTabDichVu());
        btnKetQua.setOnClickListener(v -> showTabKetQua());

        view.findViewById(R.id.btnBack).setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        });

        view.findViewById(R.id.btnCardReissueMenu).setOnClickListener(v ->
                checkNetAndNavigate(CardReissueActivity.class));
        view.findViewById(R.id.btnLoanSupportMenu).setOnClickListener(v ->
                checkNetAndNavigate(LoanSupportActivity.class));
        view.findViewById(R.id.btnTranscriptMenu).setOnClickListener(v ->
                checkNetAndNavigate(TranscriptRegistrationActivity.class));
        view.findViewById(R.id.btnConfirmationMenu).setOnClickListener(v ->
                checkNetAndNavigate(StudentConfirmationActivity.class));
    }

    private void checkNetAndNavigate(Class<?> destination) {
        if (NetworkUtils.isNetworkAvailable(requireContext())) {
            startActivity(new Intent(requireContext(), destination));
        } else {
            com.utc2.appreborn.utils.CustomToastHelper.showToast(requireContext(), getString(R.string.error_connect_network));
        }
    }

    private void showTabDichVu() {
        updateToggleUI(true);
        layoutDichVuMenu.setVisibility(View.VISIBLE);
        rvKetQua.setVisibility(View.GONE);

        if (txtSectionTitle != null) {
            txtSectionTitle.setText(R.string.section_public_service);
            txtSectionTitle.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_hand_platter, 0, 0, 0);
        }
    }

    private void showTabKetQua() {
        updateToggleUI(false);
        layoutDichVuMenu.setVisibility(View.GONE);
        rvKetQua.setVisibility(View.VISIBLE);

        if (txtSectionTitle != null) {
            txtSectionTitle.setText(R.string.tab_results);
            txtSectionTitle.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_scroll_text, 0, 0, 0);
        }

        loadHistoryFromApi();
    }

    /** Gọi GET /api/v1/services/my-requests và map sang List<BaseService> để hiển thị. */
    private void loadHistoryFromApi() {
        if (!isAdded()) return;

        if (!NetworkUtils.isNetworkAvailable(requireContext())) {
            com.utc2.appreborn.utils.CustomToastHelper.showToast(requireContext(), getString(R.string.error_connect_network));
            return;
        }

        setLoading(true);

        String token = SessionManager.getInstance(requireContext()).getAuthToken();
        PublicServicesApiService servicesApi =
                ApiClient.getInstance(token).create(PublicServicesApiService.class);

        servicesApi.myRequests().enqueue(new Callback<ApiResponse<List<ServiceRequestResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ServiceRequestResponse>>> call,
                                   Response<ApiResponse<List<ServiceRequestResponse>>> response) {
                if (!isAdded()) return;
                setLoading(false);

                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess()) {
                    List<ServiceRequestResponse> data = response.body().getData();
                    bindHistory(data != null ? data : new ArrayList<>());
                } else {
                    com.utc2.appreborn.utils.CustomToastHelper.showToast(requireContext(), "Không tải được lịch sử yêu cầu");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ServiceRequestResponse>>> call,
                                  Throwable t) {
                if (!isAdded()) return;
                setLoading(false);
                com.utc2.appreborn.utils.CustomToastHelper.showToast(requireContext(), "Lỗi kết nối: " + t.getMessage());
                Log.e(TAG, "myRequests onFailure", t);
            }
        });
    }

    /**
     * Map từng ServiceRequestResponse → BaseService con tương ứng.
     * Dùng serviceType để phân loại (khớp với hằng trong BaseService).
     */
    private void bindHistory(List<ServiceRequestResponse> data) {
        historyList.clear();

        SessionManager session = SessionManager.getInstance(requireContext());
        String fullName   = session.getFullName();
        String studentCode = session.getStudentCode();
        String className   = session.getClassName();

        for (ServiceRequestResponse r : data) {
            long submittedMillis = parseIsoToMillis(r.submittedAt);
            String type  = r.serviceType != null ? r.serviceType : "";
            String desc  = r.description != null ? r.description : "";
            String status = r.status     != null ? r.status      : BaseService.STATUS_PENDING;

            BaseService item;
            switch (type) {
                case BaseService.TYPE_CARD_REISSUE:
                    item = new CardReissueService(
                            getString(R.string.reissue_card_title),
                            desc, submittedMillis, status,
                            BaseService.TYPE_CARD_REISSUE,
                            fullName, studentCode, className);
                    break;

                case BaseService.TYPE_TRANSCRIPT:
                    item = new TranscriptService(
                            getString(R.string.transcript_registration_title),
                            desc, submittedMillis, status,
                            BaseService.TYPE_TRANSCRIPT,
                            fullName, studentCode, className, "", "", "");
                    break;

                case BaseService.TYPE_CONFIRMATION:
                    item = new StudentConfirmationService(
                            getString(R.string.student_confirmation_title),
                            desc, submittedMillis, status,
                            BaseService.TYPE_CONFIRMATION,
                            fullName, studentCode, className);
                    break;

                case BaseService.TYPE_LOAN_SUPPORT:
                    item = new LoanSupportService(
                            getString(R.string.loan_support_title),
                            desc, submittedMillis, status,
                            BaseService.TYPE_LOAN_SUPPORT,
                            fullName, studentCode, className);
                    break;

                default:
                    // Loại không xác định — dùng CardReissue làm fallback
                    item = new CardReissueService(
                            type, desc, submittedMillis, status,
                            type, "", "", "");
                    break;
            }

            // Gắn thêm id và resultNote nếu adapter/ServiceDetailActivity cần
            item.setRequestId(r.id != null ? r.id : 0L);
            item.setResultNote(r.resultNote);
            historyList.add(item);
        }

        adapter = new PublicServiceAdapter(historyList, true);
        rvKetQua.setAdapter(adapter);
    }

    /** Parse "yyyy-MM-dd'T'HH:mm:ss" → epoch millis; trả về now nếu lỗi. */
    private long parseIsoToMillis(String iso) {
        if (iso == null || iso.isEmpty()) return System.currentTimeMillis();
        try {
            Date d = ISO_FMT.parse(iso);
            return d != null ? d.getTime() : System.currentTimeMillis();
        } catch (ParseException e) {
            return System.currentTimeMillis();
        }
    }

    private void setLoading(boolean loading) {
        if (progressBar != null) {
            progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        }
    }

    private void updateToggleUI(boolean isDichVuSelected) {
        int colorWhite = ContextCompat.getColor(requireContext(), R.color.white);
        int colorBlack = ContextCompat.getColor(requireContext(), R.color.black);

        if (isDichVuSelected) {
            btnDichVu.setBackgroundResource(R.drawable.bg_toggle_selected);
            btnDichVu.setTextColor(colorWhite);
            btnKetQua.setBackgroundResource(R.drawable.bg_toggle_container);
            btnKetQua.setTextColor(colorBlack);
        } else {
            btnKetQua.setBackgroundResource(R.drawable.bg_toggle_selected);
            btnKetQua.setTextColor(colorWhite);
            btnDichVu.setBackgroundResource(R.drawable.bg_toggle_container);
            btnDichVu.setTextColor(colorBlack);
        }
    }
}