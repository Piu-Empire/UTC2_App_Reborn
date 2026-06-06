package com.utc2.appreborn.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.button.MaterialButton;
import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.login.LoginActivity;
import com.utc2.appreborn.ui.profile.SubjectList.SubjectListActivity;
import com.utc2.appreborn.ui.profile.adapter.StudentInfoAdapter;
import com.utc2.appreborn.ui.profile.model.StudentInfoItem;
import com.utc2.appreborn.utils.SessionManager;

import com.utc2.appreborn.network.ApiClient;
import com.utc2.appreborn.network.ApiResponse;
import com.utc2.appreborn.network.ProfileApiService;
import com.utc2.appreborn.network.dto.ProfileResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class InfoFragment extends Fragment {

    private RecyclerView rvStudentDetails;
    private ImageView    imgStudentCard;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        ImageView      btnBack            = view.findViewById(R.id.btnBack);
        imgStudentCard                    = view.findViewById(R.id.imgStudentCard);
        rvStudentDetails                  = view.findViewById(R.id.rvStudentDetails);
        MaterialButton btnTrainingProgram = view.findViewById(R.id.btnTrainingProgram);
        MaterialButton btnSupport         = view.findViewById(R.id.btnSupport);
        MaterialButton btnLogout          = view.findViewById(R.id.btnLogout);

        setupRecyclerView();
        setupClickListeners(btnBack, imgStudentCard, btnTrainingProgram, btnSupport, btnLogout);
    }

    private void setupRecyclerView() {
        String token = SessionManager.getInstance(getContext()).getAuthToken();
        ProfileApiService profileApi = ApiClient.getInstance(token).create(ProfileApiService.class);

        profileApi.getMyProfile().enqueue(new Callback<ApiResponse<ProfileResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ProfileResponse>> call,
                                   Response<ApiResponse<ProfileResponse>> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess()) {
                    bindProfile(response.body().getData());
                } else {
                    com.utc2.appreborn.utils.CustomToastHelper.showToast(getContext(), "Không tải được thông tin");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ProfileResponse>> call, Throwable t) {
                if (!isAdded()) return;
                com.utc2.appreborn.utils.CustomToastHelper.showToast(getContext(), "Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void bindProfile(ProfileResponse p) {
        List<StudentInfoItem> infoList = new ArrayList<>();
        infoList.add(new StudentInfoItem(getString(R.string.info_label_fullname),      p.fullName));
        infoList.add(new StudentInfoItem(getString(R.string.info_label_mssv),          p.studentId));
        infoList.add(new StudentInfoItem(getString(R.string.info_label_class),         p.className));
        infoList.add(new StudentInfoItem(getString(R.string.info_label_major),         p.major));
        infoList.add(new StudentInfoItem(getString(R.string.info_label_faculty),       p.faculty));
        infoList.add(new StudentInfoItem(getString(R.string.info_label_course),        p.academicYear));
        infoList.add(new StudentInfoItem(getString(R.string.info_label_training_type), p.status));
        // dateOfBirth bây giờ là String "yyyy-MM-dd" từ backend (nhờ @JsonFormat)
        infoList.add(new StudentInfoItem(getString(R.string.info_label_dob),           p.dateOfBirth));
        infoList.add(new StudentInfoItem(getString(R.string.info_label_gender),        p.gender));
        infoList.add(new StudentInfoItem(getString(R.string.info_label_email),         p.email));
        infoList.add(new StudentInfoItem(getString(R.string.info_label_phone),         p.phoneNumber));

        rvStudentDetails.setLayoutManager(new LinearLayoutManager(getContext()));
        rvStudentDetails.setAdapter(new StudentInfoAdapter(infoList));

        if (p.studentCardUrl != null && !p.studentCardUrl.isEmpty()) {
            Glide.with(this).load(p.studentCardUrl)
                    .placeholder(R.drawable.logo_utc2)
                    .into(imgStudentCard);
        }
    }

    private void setupClickListeners(View btnBack, View imgStudentCard,
                                     View btnTrainingProgram, View btnSupport, View btnLogout) {
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) getActivity().getOnBackPressedDispatcher().onBackPressed();
        });

        btnTrainingProgram.setOnClickListener(v ->
                startActivity(new Intent(getContext(), SubjectListActivity.class)));

        btnSupport.setOnClickListener(v ->
                startActivity(new Intent(getContext(), SupportActivity.class)));

        btnLogout.setOnClickListener(v -> handleLogout());

        imgStudentCard.setOnClickListener(v ->
                Toast.makeText(getContext(), getString(R.string.student_card), Toast.LENGTH_SHORT).show());
    }

    private void handleLogout() {
        if (getActivity() == null) return;
        SessionManager sessionManager = SessionManager.getInstance(getActivity());

        // FIX WARN 4: reset Retrofit cache khi logout
        ApiClient.clearAuth();

        if ("GOOGLE".equals(sessionManager.getLoginType())) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .build();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
            googleSignInClient.signOut().addOnCompleteListener(task -> {
                sessionManager.logout();
                navigateToLogin();
            });
        } else {
            sessionManager.logout();
            navigateToLogin();
        }
    }

    private void navigateToLogin() {
        if (getActivity() == null) return;
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}