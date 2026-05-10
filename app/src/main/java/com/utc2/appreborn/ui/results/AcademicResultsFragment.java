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
import com.utc2.appreborn.ui.results.grades.GradesFragment;
import com.utc2.appreborn.ui.results.leaderboard.LeaderboardFragment;
import com.utc2.appreborn.ui.results.scholarship.ScholarshipFragment;
import com.utc2.appreborn.ui.results.warning.WarningsFragment;

/**
 * AcademicResultsFragment — Dashboard "Kết quả học tập"
 *
 * Hiển thị thẻ thông tin sinh viên + 4 card điều hướng.
 * Mỗi card click → push Fragment tương ứng vào back stack.
 *
 * Lý do card trắng trước đây:
 *   Lần trước các field trong XML bị đổi sang android:text="" để dẹp warning
 *   nhưng Fragment chưa có code setText() → runtime trống rỗng.
 *   tools:text chỉ hiển thị trong Layout Editor, không chạy trên máy thật.
 */
public class AcademicResultsFragment extends Fragment {

    // Views trong student info card
    private TextView tvAvatar;
    private TextView tvStudentName;
    private TextView tvStudentId;
    private TextView tvMajor;
    private TextView tvGpa;

    // 4 navigation cards
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

        // Fix Status Bar overlap
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

    /**
     * Fill dữ liệu sinh viên vào student info card.
     *
     * TODO: Thay mock data này bằng dữ liệu thật từ Firebase Auth / API
     *       ví dụ: FirebaseAuth.getInstance().getCurrentUser().getDisplayName()
     */
    private void populateStudentCard() {
        // Mock data — thay bằng data thật khi có API
        String name    = "Trần Văn Bình";
        String mssv    = "2021001245";
        String major   = "Công nghệ thông tin - K2021";
        String gpa     = "3.62";

        // Tạo chữ viết tắt cho avatar (lấy chữ cái đầu của 2 từ cuối)
        String[] parts    = name.trim().split("\\s+");
        String initials   = parts.length >= 2
                ? String.valueOf(parts[parts.length - 2].charAt(0))
                + parts[parts.length - 1].charAt(0)
                : name.substring(0, Math.min(2, name.length()));

        tvAvatar.setText(initials.toUpperCase());
        tvStudentName.setText(name);
        tvStudentId.setText("MSSV: " + mssv);
        tvMajor.setText(major);
        tvGpa.setText(gpa);
    }

    private void setupCardNavigation() {
        cardGrades.setOnClickListener(v      -> navigateTo(new GradesFragment()));
        cardLeaderboard.setOnClickListener(v -> navigateTo(new LeaderboardFragment()));
        cardScholarship.setOnClickListener(v -> navigateTo(new ScholarshipFragment()));
        cardWarnings.setOnClickListener(v    -> navigateTo(new WarningsFragment()));
    }

    /**
     * Push fragment mới vào back stack.
     * Nhấn Back → quay về Dashboard.
     */
    private void navigateTo(Fragment fragment) {
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}