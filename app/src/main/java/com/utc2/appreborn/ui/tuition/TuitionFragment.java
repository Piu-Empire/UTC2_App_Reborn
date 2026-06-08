package com.utc2.appreborn.ui.tuition;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.tuition.Dorm.DormitoryTuitionActivity;
import com.utc2.appreborn.ui.tuition.Invoice.InvoiceActivity;
import com.utc2.appreborn.ui.tuition.Subject.SubjectTuitionActivity;
import com.utc2.appreborn.utils.NetworkUtils;

public class TuitionFragment extends Fragment {

    private CardView cardTuitionSubject, cardDormitory, cardInvoice;
    private ImageButton btnBack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tuition, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            initViews(view);
            setClickListeners();
        } catch (Exception e) {
            Log.e("TuitionFragment", "Lỗi khởi tạo: " + e.getMessage());
        }
    }

    private void initViews(View view) {
        btnBack = view.findViewById(R.id.btnBack);
        cardTuitionSubject = view.findViewById(R.id.cardTuitionSubject);
        cardDormitory = view.findViewById(R.id.cardDormitory);
        cardInvoice = view.findViewById(R.id.cardInvoice);
    }

    private void setClickListeners() {
        btnBack.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        });

        // Kiểm tra mạng tức thời khi người dùng nhấn chọn chức năng
        cardTuitionSubject.setOnClickListener(v -> checkNetworkAndNavigate(SubjectTuitionActivity.class));
        cardDormitory.setOnClickListener(v -> checkNetworkAndNavigate(DormitoryTuitionActivity.class));
        // Invoice mở luôn không cần check mạng vì có local data
        cardInvoice.setOnClickListener(v -> startActivity(new Intent(requireContext(), InvoiceActivity.class)));
    }

    private void checkNetworkAndNavigate(Class<?> targetActivity) {
        if (NetworkUtils.isNetworkAvailable(requireContext())) {
            startActivity(new Intent(requireContext(), targetActivity));
        } else {
            com.utc2.appreborn.utils.CustomToastHelper.showToast(requireContext(), "Vui lòng kết nối mạng để xem thông tin học phí!");
        }
    }
}