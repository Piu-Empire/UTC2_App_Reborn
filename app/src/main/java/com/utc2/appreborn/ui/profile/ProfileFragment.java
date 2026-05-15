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
import com.utc2.appreborn.network.ApiClient;
import com.utc2.appreborn.network.ApiResponse;
import com.utc2.appreborn.network.ProfileApiService;
import com.utc2.appreborn.network.dto.ProfileResponse;
import com.utc2.appreborn.ui.profile.TrainingProgram.TrainingProgramActivity;
import com.utc2.appreborn.ui.settings.SettingsActivity;
import com.utc2.appreborn.utils.NetworkUtils;
import com.utc2.appreborn.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private LinearLayout layoutSubjectList, layoutGraduationReq;
    private AppCompatButton btnInfo, btnChangePassword;
    private ImageView btnSettings;
    private TextView tvStudentName, tvStudentId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        loadProfileData();
        setClickListeners();
    }

    private void initViews(View view) {
        layoutSubjectList   = view.findViewById(R.id.layoutSubjectList);
        layoutGraduationReq = view.findViewById(R.id.layoutGraduationReq);
        btnInfo             = view.findViewById(R.id.btnProfileInfo);
        btnChangePassword   = view.findViewById(R.id.btnChangePassword);
        btnSettings         = view.findViewById(R.id.btnNotification);
        tvStudentName       = view.findViewById(R.id.tvStudentName);
        tvStudentId         = view.findViewById(R.id.tvStudentId);
    }

    /** Gọi API lấy profile thực, hiển thị tên + MSSV trên header. */
    private void loadProfileData() {
        if (!isAdded()) return;

        String token = SessionManager.getInstance(requireContext()).getAuthToken();
        ProfileApiService profileApi = ApiClient.getInstance(token).create(ProfileApiService.class);

        profileApi.getMyProfile().enqueue(new Callback<ApiResponse<ProfileResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ProfileResponse>> call,
                                   Response<ApiResponse<ProfileResponse>> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess()) {
                    ProfileResponse p = response.body().getData();
                    tvStudentName.setText(p.fullName  != null ? p.fullName  : "");
                    tvStudentId.setText(p.studentId != null ? p.studentId : "");
                } else {
                    // Fallback hiển thị trống — không crash
                    tvStudentName.setText("");
                    tvStudentId.setText("");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ProfileResponse>> call, Throwable t) {
                if (!isAdded()) return;
                // Không toast lỗi ở đây để không làm phiền UX; InfoFragment đã xử lý chi tiết
                tvStudentName.setText("");
                tvStudentId.setText("");
            }
        });
    }

    private void setClickListeners() {
        btnInfo.setOnClickListener(v -> {
            InfoFragment infoFragment = new InfoFragment();
            FragmentTransaction tx = getParentFragmentManager().beginTransaction();
            tx.replace(R.id.fragment_container, infoFragment);
            tx.addToBackStack(null);
            tx.commit();
        });

        btnChangePassword.setOnClickListener(v -> {
            if (NetworkUtils.isNetworkAvailable(requireContext())) {
                startActivity(new Intent(requireContext(), ChangePasswordActivity.class));
            } else {
                showToast(getString(R.string.error_connect_network));
            }
        });

        layoutSubjectList.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), TrainingProgramActivity.class)));

        layoutGraduationReq.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), GraduationRequirementsActivity.class)));

        btnSettings.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), SettingsActivity.class)));
    }

    private void showToast(String msg) {
        if (isAdded()) {
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }
}