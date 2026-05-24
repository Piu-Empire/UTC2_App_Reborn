package com.utc2.appreborn.ui.results.leaderboard;

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
import com.utc2.appreborn.model.LeaderboardEntry;
import com.utc2.appreborn.network.dto.SemesterResponse;
import com.utc2.appreborn.ui.adapter.LeaderboardAdapter;
import com.utc2.appreborn.ui.results.AcademicResultViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LeaderboardFragment extends Fragment {

    private View btnBack;
    private View btnPeriodFilter;
    private TextView tvSelectedPeriod;
    private TextView tvMyRank;
    private TextView tvMyGpa;
    private TextView tvTopPercent;
    private RecyclerView recyclerView;
    private LeaderboardAdapter leaderboardAdapter;

    private AcademicResultViewModel viewModel;

    // Danh sách kỳ/năm sẽ load từ API
    private final List<String> periodLabels = new ArrayList<>();
    private final List<SemesterResponse> semesters = new ArrayList<>();
    private String currentPeriod = "Năm học";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_leaderboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnBack          = view.findViewById(R.id.btn_back);
        btnPeriodFilter  = view.findViewById(R.id.btn_period_filter);
        tvSelectedPeriod = view.findViewById(R.id.tv_selected_period);
        tvMyRank         = view.findViewById(R.id.tv_my_rank);
        tvMyGpa          = view.findViewById(R.id.tv_my_gpa);
        tvTopPercent     = view.findViewById(R.id.tv_top_percent);
        recyclerView     = view.findViewById(R.id.recycler_leaderboard);

        btnBack.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        setupRecyclerView();

        viewModel = new ViewModelProvider(requireActivity()).get(AcademicResultViewModel.class);

        // Load kỳ học để build filter
        viewModel.getSemesters().observe(getViewLifecycleOwner(), semList -> {
            semesters.clear();
            periodLabels.clear();
            periodLabels.add("Năm học");
            if (semList != null) {
                semesters.addAll(semList);
                for (SemesterResponse s : semList) {
                    String label = buildSemLabel(s);
                    if (!label.isEmpty()) periodLabels.add(label);
                }
            }
            setupPeriodFilter();
            // Load leaderboard theo năm học mặc định
            loadLeaderboard(null, getCurrentAcademicYear());
        });
    }

    private String buildSemLabel(SemesterResponse s) {
        if (s.semesterName == null || s.academicYear == null) return "";
        return s.semesterName.replace("Học kỳ ", "HK") + " " + s.academicYear;
    }

    private String getCurrentAcademicYear() {
        if (!semesters.isEmpty()) return semesters.get(0).academicYear;
        return "";
    }

    private void setupPeriodFilter() {
        btnPeriodFilter.setOnClickListener(v -> {
            String[] arr = periodLabels.toArray(new String[0]);
            int currentIndex = Math.max(0, periodLabels.indexOf(currentPeriod));
            new AlertDialog.Builder(requireContext())
                    .setTitle("Chọn kỳ / năm học")
                    .setSingleChoiceItems(arr, currentIndex, (dialog, which) -> {
                        currentPeriod = periodLabels.get(which);
                        tvSelectedPeriod.setText(currentPeriod);
                        if (which == 0) {
                            // "Năm học" — lấy năm hiện tại
                            loadLeaderboard(null, getCurrentAcademicYear());
                        } else {
                            // Kỳ học cụ thể — tìm semesterId tương ứng
                            SemesterResponse sem = findSemesterByLabel(currentPeriod);
                            if (sem != null) {
                                loadLeaderboard(sem.semesterId, null);
                            }
                        }
                        dialog.dismiss();
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }

    private SemesterResponse findSemesterByLabel(String label) {
        for (SemesterResponse s : semesters) {
            if (label.equals(buildSemLabel(s))) return s;
        }
        return null;
    }

    private void loadLeaderboard(Long semesterId, String academicYear) {
        viewModel.getLeaderboard(semesterId, academicYear)
                .observe(getViewLifecycleOwner(), entries -> {
                    leaderboardAdapter.updateList(entries != null ? entries : new ArrayList<>());
                    updateMyStats(entries);
                });
    }

    private void updateMyStats(List<LeaderboardEntry> entries) {
        if (entries == null || entries.isEmpty()) {
            tvMyRank.setText("—");
            tvMyGpa.setText("—");
            tvTopPercent.setText("—");
            return;
        }
        int total = entries.size();
        for (LeaderboardEntry e : entries) {
            if (e.isCurrentUser()) {
                tvMyRank.setText("# " + e.getRank());
                tvMyGpa.setText(String.format(Locale.getDefault(), "%.2f", e.getGpa())
                        .replace('.', ','));
                int topPct = total > 1
                        ? (int) (100.0 * (total - e.getRank()) / (total - 1))
                        : 100;
                tvTopPercent.setText(topPct + " %");
                return;
            }
        }
        tvMyRank.setText("—");
        tvMyGpa.setText("—");
        tvTopPercent.setText("—");
    }

    private void setupRecyclerView() {
        leaderboardAdapter = new LeaderboardAdapter(requireContext(), new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(leaderboardAdapter);
        recyclerView.setNestedScrollingEnabled(false);
    }
}