package com.utc2.appreborn.ui.results.grades;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.adapter.GradeAdapter;
import com.utc2.appreborn.model.CourseGrade;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GradesFragment extends Fragment {

    private View btnBack;
    private View btnSemesterFilter;
    private TextView tvSelectedSemester;
    private TextView tvCurrentGpa;
    private TextView tvTotalCredits;
    private TextView tvPassedSubjects;
    private RecyclerView recyclerView;
    private GradeAdapter gradeAdapter;

    private final List<CourseGrade> allGrades = new ArrayList<>();
    private String currentSemester = "Tất cả";

    private final String[] semesters = {
            "Tất cả", "HK1 2024-2025", "HK2 2023-2024", "HK1 2023-2024"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_grades, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnBack            = view.findViewById(R.id.btn_back);
        btnSemesterFilter  = view.findViewById(R.id.btn_semester_filter);
        tvSelectedSemester = view.findViewById(R.id.tv_selected_semester);
        tvCurrentGpa       = view.findViewById(R.id.tv_current_gpa);
        tvTotalCredits     = view.findViewById(R.id.tv_total_credits);
        tvPassedSubjects   = view.findViewById(R.id.tv_passed_subjects);
        recyclerView       = view.findViewById(R.id.recycler_grades);

        // Nút quay lại
        btnBack.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        loadMockData();
        setupSemesterFilter();
        setupRecyclerView();
        refreshDisplay();
    }

    private void setupSemesterFilter() {
        btnSemesterFilter.setOnClickListener(v -> {
            int currentIndex = 0;
            for (int i = 0; i < semesters.length; i++) {
                if (semesters[i].equals(currentSemester)) { currentIndex = i; break; }
            }
            new AlertDialog.Builder(requireContext())
                    .setTitle("Chọn học kỳ")
                    .setSingleChoiceItems(semesters, currentIndex, (dialog, which) -> {
                        currentSemester = semesters[which];
                        tvSelectedSemester.setText(currentSemester);
                        refreshDisplay();
                        dialog.dismiss();
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }

    private void loadMockData() {
        allGrades.add(new CourseGrade("IT3040", "Lập trình Java", 3, 8.5, 7.8, 3.48, "B+", true, "HK1 2024-2025"));
        allGrades.add(new CourseGrade("IT3050", "Cơ sở dữ liệu", 3, 7.0, 8.0, 3.32, "B+", true, "HK1 2024-2025"));
        allGrades.add(new CourseGrade("IT3060", "Hệ điều hành", 3, 4.5, 3.5, 1.72, "F", false, "HK1 2024-2025"));
        allGrades.add(new CourseGrade("IT3070", "Mạng máy tính", 3, 6.0, 7.5, 3.04, "B", true, "HK1 2024-2025"));
        allGrades.add(new CourseGrade("ML3010", "Triết học Mác-Lênin", 3, 4.0, 3.0, 1.48, "F", false, "HK1 2024-2025"));
        allGrades.add(new CourseGrade("IT3080", "Lập trình Web", 2, 8.0, 7.5, 3.32, "B+", true, "HK1 2024-2025"));
        allGrades.add(new CourseGrade("IT2010", "Giải tích 1", 3, 7.5, 8.0, 3.40, "B+", true, "HK2 2023-2024"));
        allGrades.add(new CourseGrade("IT2020", "Đại số tuyến tính", 3, 8.0, 9.0, 3.84, "A", true, "HK2 2023-2024"));
        allGrades.add(new CourseGrade("IT2030", "Cấu trúc dữ liệu", 3, 7.0, 7.5, 3.12, "B", true, "HK2 2023-2024"));
        allGrades.add(new CourseGrade("EN2010", "Tiếng Anh cơ bản", 3, 8.5, 8.0, 3.48, "B+", true, "HK2 2023-2024"));
        allGrades.add(new CourseGrade("IT1010", "Nhập môn CNTT", 2, 9.0, 9.5, 3.96, "A", true, "HK1 2023-2024"));
        allGrades.add(new CourseGrade("IT1020", "Lập trình C++", 3, 8.0, 8.5, 3.60, "A-", true, "HK1 2023-2024"));
        allGrades.add(new CourseGrade("MA1010", "Giải tích 2", 3, 6.5, 7.0, 2.96, "B", true, "HK1 2023-2024"));
    }

    private void setupRecyclerView() {
        gradeAdapter = new GradeAdapter(requireContext(), new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(gradeAdapter);
        recyclerView.setNestedScrollingEnabled(false);
    }

    private void refreshDisplay() {
        List<CourseGrade> filtered = getFilteredGrades();
        gradeAdapter.updateList(filtered);
        updateSummary(filtered);
    }

    private List<CourseGrade> getFilteredGrades() {
        if ("Tất cả".equals(currentSemester)) return new ArrayList<>(allGrades);
        List<CourseGrade> result = new ArrayList<>();
        for (CourseGrade g : allGrades) {
            if (g.getSemester().equals(currentSemester)) result.add(g);
        }
        return result;
    }

    private void updateSummary(List<CourseGrade> grades) {
        if (grades.isEmpty()) {
            tvCurrentGpa.setText("—");
            tvTotalCredits.setText("0");
            tvPassedSubjects.setText("0/0");
            return;
        }
        double totalPoints = 0;
        int totalCredits = 0;
        int passed = 0;
        for (CourseGrade g : grades) {
            totalPoints += g.getGpaScore() * g.getCredits();
            totalCredits += g.getCredits();
            if (g.isPassed()) passed++;
        }
        double gpa = totalCredits > 0 ? totalPoints / totalCredits : 0;
        tvCurrentGpa.setText(String.format(Locale.getDefault(), "%.2f", gpa));
        tvTotalCredits.setText(String.valueOf(totalCredits));
        tvPassedSubjects.setText(passed + "/" + grades.size());
    }
}
