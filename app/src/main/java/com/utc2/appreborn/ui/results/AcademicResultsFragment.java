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

import com.google.android.material.card.MaterialCardView;
import com.utc2.appreborn.R;
import com.utc2.appreborn.network.ApiClient;
import com.utc2.appreborn.network.ApiResponse;
import com.utc2.appreborn.network.EnrollmentApiService;
import com.utc2.appreborn.network.dto.EnrollmentResponse;
import com.utc2.appreborn.ui.results.grades.GradesFragment;
import com.utc2.appreborn.ui.results.leaderboard.LeaderboardFragment;
import com.utc2.appreborn.ui.results.scholarship.ScholarshipFragment;
import com.utc2.appreborn.ui.results.warning.WarningsFragment;
import com.utc2.appreborn.utils.SessionManager;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    }

    private void initViews(View view) {
        tvAvatar = view.findViewById(R.id.tv_avatar);
        tvStudentName = view.findViewById(R.id.tv_student_name);
        tvStudentId = view.findViewById(R.id.tv_student_id);
        tvMajor = view.findViewById(R.id.tv_major);
        tvGpa = view.findViewById(R.id.tv_gpa);

        cardGrades = view.findViewById(R.id.card_grades);
        cardLeaderboard = view.findViewById(R.id.card_leaderboard);
        cardScholarship = view.findViewById(R.id.card_scholarship);
        cardWarnings = view.findViewById(R.id.card_warnings);
    }

    /**
     * Đọc tên + MSSV từ SessionManager cache (fetch 1 lần ở HomeViewModel).
     * Sau đó gọi enrollment API để tính GPA tích lũy.
     */
    private void populateStudentCard() {
        SessionManager session = SessionManager.getInstance(requireContext());
        String name = session.getCachedFullName();
        String mssv = session.getStudentCode();

        // Avatar: 2 chữ cái đầu từ 2 từ cuối của tên
        String[] parts = (name != null ? name.trim() : "").split("\\s+");
        String initials = parts.length >= 2
                ? String.valueOf(parts[parts.length - 2].charAt(0))
                + parts[parts.length - 1].charAt(0)
                : (name != null && name.length() >= 2 ? name.substring(0, 2) : "SV");

        tvAvatar.setText(initials.toUpperCase());
        tvStudentName.setText(name != null ? name : "");
        tvStudentId.setText("MSSV: " + (mssv != null ? mssv : ""));
        tvMajor.setText("");   // sẽ cập nhật từ enrollment nếu có
        tvGpa.setText("--");   // placeholder cho đến khi API trả về

        fetchGpa(session.getAuthToken());
    }

    /**
     * Gọi enrollment API, tính GPA tích lũy từ gradePoint + credits.
     */
    private void fetchGpa(String token) {
        if (token == null || token.isEmpty()) return;

        EnrollmentApiService api = ApiClient.getInstance(token).create(EnrollmentApiService.class);
        api.getMyEnrollments().enqueue(new Callback<ApiResponse<List<EnrollmentResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<EnrollmentResponse>>> call,
                                   Response<ApiResponse<List<EnrollmentResponse>>> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess()) {
                    List<EnrollmentResponse> list = response.body().getData();
                    if (list == null || list.isEmpty()) return;

                    double totalPoints = 0.0;
                    int totalCredits = 0;
                    for (EnrollmentResponse e : list) {
                        if (e.gradePoint != null && e.credits != null && e.credits > 0) {
                            totalPoints += e.gradePoint * e.credits;
                            totalCredits += e.credits;
                        }
                    }
                    if (totalCredits > 0) {
                        double gpa = totalPoints / totalCredits;
                        tvGpa.setText(String.format(Locale.getDefault(), "%.2f", gpa));
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<EnrollmentResponse>>> call, Throwable t) {
                // GPA giữ nguyên "--"
            }
        });
    }

    private void setupCardNavigation() {
        cardGrades.setOnClickListener(v -> navigateTo(new GradesFragment()));
        cardLeaderboard.setOnClickListener(v -> navigateTo(new LeaderboardFragment()));
        cardScholarship.setOnClickListener(v -> navigateTo(new ScholarshipFragment()));
        cardWarnings.setOnClickListener(v -> navigateTo(new WarningsFragment()));
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