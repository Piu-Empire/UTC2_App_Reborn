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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.button.MaterialButton;
import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.login.LoginActivity;
import com.utc2.appreborn.ui.profile.TrainingProgram.TrainingProgramActivity;
import com.utc2.appreborn.ui.profile.adapter.StudentInfoAdapter;
import com.utc2.appreborn.ui.profile.model.StudentInfoItem;
import com.utc2.appreborn.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class InfoFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        ImageView btnBack = view.findViewById(R.id.btnBack);
        ImageView imgStudentCard = view.findViewById(R.id.imgStudentCard);
        RecyclerView rvStudentDetails = view.findViewById(R.id.rvStudentDetails);
        MaterialButton btnTrainingProgram = view.findViewById(R.id.btnTrainingProgram);
        MaterialButton btnSupport = view.findViewById(R.id.btnSupport);
        MaterialButton btnLogout = view.findViewById(R.id.btnLogout);

        setupRecyclerView(rvStudentDetails);
        setupClickListeners(btnBack, imgStudentCard, btnTrainingProgram, btnSupport, btnLogout);
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        List<StudentInfoItem> infoList = new ArrayList<>();
        infoList.add(new StudentInfoItem("Họ và Tên", "Nguyễn Văn A"));
        infoList.add(new StudentInfoItem("Mã số sinh viên", "5971030001"));
        infoList.add(new StudentInfoItem("Lớp", "Kỹ thuật Phần mềm 1 - K60"));
        infoList.add(new StudentInfoItem("Ngành", "Công nghệ Thông tin"));
        infoList.add(new StudentInfoItem("Hệ đào tạo", "Chính quy"));

        StudentInfoAdapter adapter = new StudentInfoAdapter(infoList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupClickListeners(View btnBack, View imgStudentCard, View btnTrainingProgram, View btnSupport, View btnLogout) {
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) getActivity().getOnBackPressedDispatcher().onBackPressed();
        });

        btnTrainingProgram.setOnClickListener(v ->
                startActivity(new Intent(getContext(), TrainingProgramActivity.class)));

        btnSupport.setOnClickListener(v ->
                startActivity(new Intent(getContext(), SupportActivity.class)));

        btnLogout.setOnClickListener(v -> handleLogout());

        imgStudentCard.setOnClickListener(v ->
                Toast.makeText(getContext(), "Chi tiết thẻ sinh viên", Toast.LENGTH_SHORT).show());
    }

    private void handleLogout() {
        if (getActivity() == null) return;

        SessionManager sessionManager = SessionManager.getInstance(getActivity());

        if ("google".equals(sessionManager.getLoginType())) {
            // Sign out khỏi Google SDK để lần sau bấm login GG hiện lại màn hình chọn tài khoản
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .build();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
            googleSignInClient.signOut();
        }

        // Xóa session local
        sessionManager.logout();

        // Về LoginActivity, xóa sạch back stack
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}