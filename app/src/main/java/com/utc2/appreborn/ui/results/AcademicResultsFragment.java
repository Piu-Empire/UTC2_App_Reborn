package com.utc2.appreborn.ui.results;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.card.MaterialCardView;
import com.utc2.appreborn.R;
import com.utc2.appreborn.network.dto.SemesterResponse;
import com.utc2.appreborn.ui.results.grades.GradesFragment;
import com.utc2.appreborn.ui.results.leaderboard.LeaderboardFragment;
import com.utc2.appreborn.ui.results.scholarship.ScholarshipFragment;
import com.utc2.appreborn.ui.results.warning.WarningsFragment;
import com.utc2.appreborn.utils.SessionManager;

import java.util.List;
import java.util.Locale;

public class AcademicResultsFragment extends Fragment {

    private TextView tvAvatar;
    private TextView tvStudentName;
    private TextView tvStudentId;
    private TextView tvMajor;
    private TextView tvGpa;

    private MaterialCardView cardGrades;
    private MaterialCardView cardLeaderboard;
    private MaterialCardView cardScholarship;
    private MaterialCardView cardWarnings;

    public AcademicResultsFragment() {
        super(R.layout.fragment_academic_results);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_academic_results, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            v.setPadding(0, statusBarHeight, 0, 0);
            return insets;
        });

        initViews(view);
        populateStudentCard();
        setupCardNavigation();

        // Bug fix: dùng getSemesters() để lấy GPA thay vì gọi thêm enrollment API
        AcademicResultViewModel viewModel =
                new ViewModelProvider(requireActivity()).get(AcademicResultViewModel.class);

        viewModel.getSemesters().observe(getViewLifecycleOwner(), semesters -> {
            if (semesters == null || semesters.isEmpty()) return;
            // Tính GPA tích lũy = tổng (gpa * totalCredits) / tổng totalCredits
            double totalWeighted = 0;
            int totalCredits = 0;
            for (SemesterResponse s : semesters) {
                if (s.gpa != null && s.totalCredits != null && s.totalCredits > 0) {
                    totalWeighted += s.gpa * s.totalCredits;
                    totalCredits  += s.totalCredits;
                }
            }
            if (totalCredits > 0) {
                double gpa = totalWeighted / totalCredits;
                tvGpa.setText(String.format(Locale.getDefault(), "%.2f", gpa));
            }
        });
    }

    private void initViews(View view) {
        tvAvatar      = view.findViewById(R.id.tv_avatar);
        tvStudentName = view.findViewById(R.id.tv_student_name);
        tvStudentId   = view.findViewById(R.id.tv_student_id);
        tvMajor       = view.findViewById(R.id.tv_major);
        tvGpa         = view.findViewById(R.id.tv_gpa);

        cardGrades      = view.findViewById(R.id.card_grades);
        cardLeaderboard = view.findViewById(R.id.card_leaderboard);
        cardScholarship = view.findViewById(R.id.card_scholarship);
        cardWarnings    = view.findViewById(R.id.card_warnings);
    }

    private void populateStudentCard() {
        SessionManager session = SessionManager.getInstance(requireContext());
        String name = session.getCachedFullName();
        String mssv = session.getStudentCode();

        String[] parts = (name != null ? name.trim() : "").split("\\s+");
        String initials = parts.length >= 2
                ? String.valueOf(parts[parts.length - 2].charAt(0))
                + parts[parts.length - 1].charAt(0)
                : (name != null && name.length() >= 2 ? name.substring(0, 2) : "SV");

        tvAvatar.setText(initials.toUpperCase());
        tvStudentName.setText(name != null ? name : "");
        tvStudentId.setText("MSSV: " + (mssv != null ? mssv : ""));
        tvMajor.setText("");
        tvGpa.setText("--");
    }

    private void setupCardNavigation() {
        cardGrades.setOnClickListener(v      -> navigateTo(new GradesFragment()));
        cardLeaderboard.setOnClickListener(v -> navigateTo(new LeaderboardFragment()));
        cardScholarship.setOnClickListener(v -> navigateTo(new ScholarshipFragment()));
        cardWarnings.setOnClickListener(v    -> navigateTo(new WarningsFragment()));
    }

    private void navigateTo(Fragment fragment) {
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
