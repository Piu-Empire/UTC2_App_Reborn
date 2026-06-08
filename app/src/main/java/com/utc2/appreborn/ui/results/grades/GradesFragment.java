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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.utc2.appreborn.R;
import com.utc2.appreborn.model.CourseGrade;
import com.utc2.appreborn.network.dto.SemesterResponse;
import com.utc2.appreborn.ui.adapter.GradeAdapter;
import com.utc2.appreborn.ui.results.AcademicResultViewModel;

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

    private AcademicResultViewModel viewModel;

    private final List<CourseGrade> allGrades = new ArrayList<>();
    private String currentSemester = "Tất cả";

    // Danh sách kỳ học sẽ được build từ API
    private final List<String> semesterLabels = new ArrayList<>();

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

        btnBack.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        setupRecyclerView();
        setupSemesterFilter();

        viewModel = new ViewModelProvider(requireActivity()).get(AcademicResultViewModel.class);

        // Load tất cả điểm (không filter kỳ)
        viewModel.getGrades(null).observe(getViewLifecycleOwner(), grades -> {
            allGrades.clear();
            if (grades != null) allGrades.addAll(grades);
            buildSemesterLabels();
            refreshDisplay();
        });

        // Load danh sách kỳ học để build filter
        viewModel.getSemesters().observe(getViewLifecycleOwner(), semesters -> {
            buildSemesterLabelsFromSemesters(semesters);
        });
    }

    private void buildSemesterLabels() {
        semesterLabels.clear();
        semesterLabels.add("Tất cả");
        for (CourseGrade g : allGrades) {
            if (!semesterLabels.contains(g.getSemester())) {
                semesterLabels.add(g.getSemester());
            }
        }
    }

    private void buildSemesterLabelsFromSemesters(List<SemesterResponse> semesters) {
        if (semesters == null) return;
        semesterLabels.clear();
        semesterLabels.add("Tất cả");
        for (SemesterResponse s : semesters) {
            String label = s.semesterName != null && s.academicYear != null
                    ? s.semesterName.replace("Học kỳ ", "HK") + " " + s.academicYear
                    : "";
            if (!label.isEmpty() && !semesterLabels.contains(label)) {
                semesterLabels.add(label);
            }
        }
    }

    private void setupSemesterFilter() {
        btnSemesterFilter.setOnClickListener(v -> {
            String[] arr = semesterLabels.toArray(new String[0]);
            int currentIndex = semesterLabels.indexOf(currentSemester);
            if (currentIndex < 0) currentIndex = 0;
            new AlertDialog.Builder(requireContext())
                    .setTitle("Chọn học kỳ")
                    .setSingleChoiceItems(arr, currentIndex, (dialog, which) -> {
                        currentSemester = semesterLabels.get(which);
                        tvSelectedSemester.setText(currentSemester);
                        refreshDisplay();
                        dialog.dismiss();
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });
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
        double totalPoints  = 0;
        int    gpaCredits   = 0; // chỉ tính credits của môn có gradePoint
        int    totalCredits = 0; // tổng credits tất cả môn (hiển thị)
        int    passed       = 0;
        for (CourseGrade g : grades) {
            totalCredits += g.getCredits();
            if (g.isPassed()) passed++;
            // Chỉ tính GPA với môn đã có gradePoint (không tính môn đang học)
            if (g.getGpaScore() > 0) {
                totalPoints += g.getGpaScore() * g.getCredits();
                gpaCredits  += g.getCredits();
            }
        }
        double gpa = gpaCredits > 0 ? totalPoints / gpaCredits : 0;
        tvCurrentGpa.setText(String.format(Locale.getDefault(), "%.2f", gpa));
        tvTotalCredits.setText(String.valueOf(totalCredits));
        tvPassedSubjects.setText(passed + "/" + grades.size());
    }
}