package com.utc2.appreborn.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.profile.TrainingProgram.TrainingProgramActivity;
import com.utc2.appreborn.utils.NetworkUtils;

public class ProfileFragment extends Fragment {

    private LinearLayout layoutSubjectList, layoutGraduationReq;
    private AppCompatButton btnInfo, btnChangePassword;
    private ImageView btnNotification;
    private TextView tvStudentName, tvStudentId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Nạp layout fragment_profile
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupStudentData();
        setClickListeners();
    }

    private void initViews(View view) {
        layoutSubjectList = view.findViewById(R.id.layoutSubjectList);
        layoutGraduationReq = view.findViewById(R.id.layoutGraduationReq);
        btnInfo = view.findViewById(R.id.btnProfileInfo);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnNotification = view.findViewById(R.id.btnNotification);
        tvStudentName = view.findViewById(R.id.tvStudentName);
        tvStudentId = view.findViewById(R.id.tvStudentId);
    }

    private void setupStudentData() {
        // Hiển thị dữ liệu mẫu từ strings.xml để app chuyên nghiệp hơn
        if (isAdded()) {
            tvStudentName.setText(getString(R.string.default_name)); // Nguyễn Minh Phúc
            tvStudentId.setText(getString(R.string.default_mssv));  // 2251050001
        }
    }

    private void setClickListeners() {
        // Chuyển sang màn hình Thông tin chi tiết
        btnInfo.setOnClickListener(v -> {
            InfoFragment infoFragment = new InfoFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            // Đảm bảo R.id.fragment_container khớp với ID trong MainActivity của bạn
            transaction.replace(R.id.fragment_container, infoFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // Chuyển sang màn hình Đổi mật khẩu (Chỉ kiểm tra mạng khi nhấn nút - Tối ưu RAM)[cite: 1]
        btnChangePassword.setOnClickListener(v -> {
            if (NetworkUtils.isNetworkAvailable(requireContext())) {
                startActivity(new Intent(requireContext(), ChangePasswordActivity.class));
            } else {
                // Sử dụng thông báo lỗi từ resources của bạn[cite: 1]
                showToast(getString(R.string.error_connect_network));
            }
        });

        // Mở Chương trình đào tạo
        layoutSubjectList.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), TrainingProgramActivity.class));
        });

        // Mở Điều kiện tốt nghiệp
        layoutGraduationReq.setOnClickListener(v -> {
            // Lưu ý: Đảm bảo bạn đã tạo GraduationRequirementsActivity
            startActivity(new Intent(requireContext(), GraduationRequirementsActivity.class));
        });

        btnNotification.setOnClickListener(v -> showToast("Không có thông báo mới"));
    }

    private void showToast(String msg) {
        if (isAdded()) {
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }
}