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
            Log.e("PublicServiceFragment", "Loi khoi tao: " + e.getMessage());
        }
    }

    private void initViews(View view) {
        layoutDichVuMenu = view.findViewById(R.id.layoutDichVuMenu);
        rvKetQua         = view.findViewById(R.id.rvKetQua);
        btnDichVu        = view.findViewById(R.id.btnDichVu);
        btnKetQua        = view.findViewById(R.id.btnKetQua);
        txtSectionTitle  = view.findViewById(R.id.sectionTitle);
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

        view.findViewById(R.id.btnCardReissueMenu).setOnClickListener(v -> checkNetAndNavigate(CardReissueActivity.class));
        view.findViewById(R.id.btnLoanSupportMenu).setOnClickListener(v -> checkNetAndNavigate(LoanSupportActivity.class));
        view.findViewById(R.id.btnTranscriptMenu).setOnClickListener(v -> checkNetAndNavigate(TranscriptRegistrationActivity.class));
        view.findViewById(R.id.btnConfirmationMenu).setOnClickListener(v -> checkNetAndNavigate(StudentConfirmationActivity.class));
    }

    private void checkNetAndNavigate(Class<?> destination) {
        if (NetworkUtils.isNetworkAvailable(requireContext())) {
            startActivity(new Intent(requireContext(), destination));
        } else {
            Toast.makeText(requireContext(), getString(R.string.error_connect_network), Toast.LENGTH_SHORT).show();
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

        loadHistoryData();
    }

    private void loadHistoryData() {
        historyList.clear();
        long now = System.currentTimeMillis();

        historyList.add(new CardReissueService(
                getString(R.string.reissue_card_title),
                "Ly do: The bi hong chip",
                now,
                BaseService.STATUS_COMPLETED,   // FIX: STATUS_DONE → STATUS_COMPLETED
                BaseService.TYPE_CARD_REISSUE,
                getString(R.string.default_name),
                getString(R.string.default_mssv),
                getString(R.string.default_class)));

        historyList.add(new TranscriptService(
                getString(R.string.transcript_registration_title),
                "So luong: 03 ban",
                now - 3_600_000L,
                BaseService.STATUS_PENDING,
                BaseService.TYPE_TRANSCRIPT,
                getString(R.string.default_name),
                getString(R.string.default_mssv),
                getString(R.string.default_class),
                "2023 - 2024",
                "Hoc ky 2",
                "03"));

        historyList.add(new StudentConfirmationService(
                getString(R.string.student_confirmation_title),
                "Ly do: Lam ho so thuc tap",
                now - 86_400_000L,
                BaseService.STATUS_COMPLETED,   // FIX: STATUS_DONE → STATUS_COMPLETED
                BaseService.TYPE_CONFIRMATION,
                getString(R.string.default_name),
                getString(R.string.default_mssv),
                getString(R.string.default_class)));

        historyList.add(new LoanSupportService(
                getString(R.string.loan_support_title),
                "So tien: 10.000.000d",
                now - 172_800_000L,
                BaseService.STATUS_PROCESSING,
                BaseService.TYPE_LOAN_SUPPORT,
                "10000000",
                "Hoc ky 1",
                getString(R.string.default_phone)));

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