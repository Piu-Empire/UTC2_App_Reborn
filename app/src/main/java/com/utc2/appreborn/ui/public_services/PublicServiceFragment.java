package com.utc2.appreborn.ui.public_services;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.utc2.appreborn.ui.public_services.CardReissueService.CardReissueActivity;
import com.utc2.appreborn.ui.public_services.model.CardReissueService;
import com.utc2.appreborn.ui.public_services.LoanSupportService.LoanSupportActivity;
import com.utc2.appreborn.ui.public_services.model.LoanSupportService;
import com.utc2.appreborn.ui.public_services.StudentConfirmationService.StudentConfirmationActivity;
import com.utc2.appreborn.ui.public_services.model.StudentConfirmationService;
import com.utc2.appreborn.ui.public_services.TranscriptService.TranscriptRegistrationActivity;
import com.utc2.appreborn.ui.public_services.model.TranscriptService;
import com.utc2.appreborn.ui.public_services.adapter.PublicServiceAdapter;
import com.utc2.appreborn.ui.public_services.model.BaseService;
import com.utc2.appreborn.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

public class PublicServiceFragment extends Fragment {

    private ScrollView layoutDichVuMenu;
    private RecyclerView rvKetQua;
    private TextView btnDichVu, btnKetQua, txtSectionTitle;
    private PublicServiceAdapter adapter;
    private final List<BaseService> historyList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Nạp layout fragment_public_service
        return inflater.inflate(R.layout.fragment_public_service, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            initViews(view);
            setupRecyclerView();
            setupEvents(view);

            // Mặc định hiển thị tab Dịch vụ khi vừa vào
            showTabDichVu();
        } catch (Exception e) {
            Log.e("PublicServiceFragment", "Lỗi khởi tạo: " + e.getMessage());
        }
    }

    private void initViews(View view) {
        layoutDichVuMenu = view.findViewById(R.id.layoutDichVuMenu);
        rvKetQua = view.findViewById(R.id.rvKetQua);
        btnDichVu = view.findViewById(R.id.btnDichVu);
        btnKetQua = view.findViewById(R.id.btnKetQua);
        txtSectionTitle = view.findViewById(R.id.sectionTitle);
    }

    private void setupRecyclerView() {
        rvKetQua.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void setupEvents(View view) {
        btnDichVu.setOnClickListener(v -> showTabDichVu());
        btnKetQua.setOnClickListener(v -> showTabKetQua());

        // Nút Back để quay lại trang trước đó
        view.findViewById(R.id.btnBack).setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        });

        // Xử lý click các dịch vụ (Có kiểm tra mạng tức thời)
        view.findViewById(R.id.btnCardReissueMenu).setOnClickListener(v -> checkNetAndNavigate(CardReissueActivity.class));
        view.findViewById(R.id.btnLoanSupportMenu).setOnClickListener(v -> checkNetAndNavigate(LoanSupportActivity.class));
        view.findViewById(R.id.btnTranscriptMenu).setOnClickListener(v -> checkNetAndNavigate(TranscriptRegistrationActivity.class));
        view.findViewById(R.id.btnConfirmationMenu).setOnClickListener(v -> checkNetAndNavigate(StudentConfirmationActivity.class));
    }

    private void checkNetAndNavigate(Class<?> destination) {
        // Sử dụng phương thức tĩnh để kiểm tra mạng giúp tiết kiệm RAM
        if (NetworkUtils.isNetworkAvailable(requireContext())) {
            startActivity(new Intent(requireContext(), destination));
        } else {
            Toast.makeText(requireContext(), "Cần kết nối mạng để thực hiện thủ tục đăng ký!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showTabDichVu() {
        updateToggleUI(true);
        layoutDichVuMenu.setVisibility(View.VISIBLE);
        rvKetQua.setVisibility(View.GONE);

        if (txtSectionTitle != null) {
            txtSectionTitle.setText(R.string.section_public_service);
            txtSectionTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_hand_platter, 0, 0, 0);
        }
    }

    private void showTabKetQua() {
        updateToggleUI(false);
        layoutDichVuMenu.setVisibility(View.GONE);
        rvKetQua.setVisibility(View.VISIBLE);

        if (txtSectionTitle != null) {
            txtSectionTitle.setText(R.string.tab_results);
            txtSectionTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_scroll_text, 0, 0, 0);
        }

        // Tải dữ liệu lịch sử ngay khi chuyển sang tab Kết quả
        loadHistoryData();
    }

    private void loadHistoryData() {
        historyList.clear();
        long now = System.currentTimeMillis();

        // Giả lập dữ liệu cho sinh viên Nguyễn Minh Phúc
        historyList.add(new CardReissueService(getString(R.string.reissue_card_title), "Lý do: Thẻ bị hỏng chip", now, 1, "CARD_REISSUE", getString(R.string.default_name), getString(R.string.default_mssv), getString(R.string.default_class)));
        historyList.add(new TranscriptService(getString(R.string.transcript_registration_title), "Số lượng: 03 bản", now - 3600000, 0, "TRANSCRIPT_REG", getString(R.string.default_name), getString(R.string.default_mssv), getString(R.string.default_class), "2023 - 2024", "Học kỳ 2", "03"));
        historyList.add(new StudentConfirmationService(getString(R.string.student_confirmation_title), "Lý do: Làm hồ sơ thực tập", now - 86400000, 1, "STUDENT_CONFIRM", getString(R.string.default_name), getString(R.string.default_mssv), getString(R.string.default_class)));
        historyList.add(new LoanSupportService(getString(R.string.loan_support_title), "Số tiền: 10.000.000đ", now - 172800000, 0, "LOAN_SUPPORT", "10000000", "Học kỳ 1", getString(R.string.default_phone)));

        adapter = new PublicServiceAdapter(historyList, true);
        rvKetQua.setAdapter(adapter);
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