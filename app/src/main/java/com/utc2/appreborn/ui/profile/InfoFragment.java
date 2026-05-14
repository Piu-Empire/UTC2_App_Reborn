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
import com.utc2.appreborn.utils.MockHelper;
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
        SessionManager session = SessionManager.getInstance(getContext());

        String fullName   = MockHelper.getMockFullName();
        String phone      = MockHelper.getMockPhone();
        String dob        = MockHelper.getMockDateOfBirth();
        String gender     = MockHelper.getMockGender();
        String email      = session.getEmail();

        String studentCode   = MockHelper.getMockStudentCode();
        String faculty       = MockHelper.getMockFaculty();
        String major         = MockHelper.getMockMajor();
        String academicYear  = MockHelper.getMockAcademicYear();
        String className     = MockHelper.getMockClassName();
        String status        = MockHelper.getMockStatus();

        String advisorName   = MockHelper.getMockAdvisorName();

        List<StudentInfoItem> infoList = new ArrayList<>();
        infoList.add(new StudentInfoItem(getString(R.string.info_label_fullname),     fullName));
        infoList.add(new StudentInfoItem(getString(R.string.info_label_mssv),         studentCode));
        infoList.add(new StudentInfoItem(getString(R.string.info_label_class),        className));
        infoList.add(new StudentInfoItem(getString(R.string.info_label_major),        major));
        infoList.add(new StudentInfoItem(getString(R.string.info_label_faculty),      faculty));
        infoList.add(new StudentInfoItem(getString(R.string.info_label_course),       academicYear));
        infoList.add(new StudentInfoItem(getString(R.string.info_label_training_type), status));
        infoList.add(new StudentInfoItem(getString(R.string.info_label_dob),          dob));
        infoList.add(new StudentInfoItem(getString(R.string.info_label_gender),       gender));
        infoList.add(new StudentInfoItem(getString(R.string.info_label_email),        email.isEmpty() ? getString(R.string.default_email) : email));
        infoList.add(new StudentInfoItem(getString(R.string.info_label_phone),        phone));
        infoList.add(new StudentInfoItem(getString(R.string.info_label_advisor),      advisorName));

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
                Toast.makeText(getContext(), getString(R.string.student_card), Toast.LENGTH_SHORT).show());
    }

    private void handleLogout() {
        if (getActivity() == null) return;

        SessionManager sessionManager = SessionManager.getInstance(getActivity());

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