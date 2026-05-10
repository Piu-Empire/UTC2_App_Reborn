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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.adapter.LeaderboardAdapter;
import com.utc2.appreborn.model.LeaderboardEntry;

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

    private static final String[] PERIODS = {
            "Năm học", "HK1 2024-2025", "HK2 2023-2024", "HK1 2023-2024"
    };
    private String currentPeriod = "Năm học";

    private static final double[][] PERIOD_STATS = {
            {15, 3.62, 83}, {8, 3.68, 72}, {12, 3.55, 68}, {5, 3.80, 88}
    };

    private final List<List<LeaderboardEntry>> periodData = new ArrayList<>();

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

        btnBack         = view.findViewById(R.id.btn_back);
        btnPeriodFilter = view.findViewById(R.id.btn_period_filter);
        tvSelectedPeriod = view.findViewById(R.id.tv_selected_period);
        tvMyRank        = view.findViewById(R.id.tv_my_rank);
        tvMyGpa         = view.findViewById(R.id.tv_my_gpa);
        tvTopPercent    = view.findViewById(R.id.tv_top_percent);
        recyclerView    = view.findViewById(R.id.recycler_leaderboard);

        btnBack.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        buildMockData();
        setupRecyclerView();
        setupPeriodFilter();
        showPeriodData(0);
    }

    private void setupPeriodFilter() {
        btnPeriodFilter.setOnClickListener(v -> {
            int currentIndex = 0;
            for (int i = 0; i < PERIODS.length; i++) {
                if (PERIODS[i].equals(currentPeriod)) { currentIndex = i; break; }
            }
            new AlertDialog.Builder(requireContext())
                    .setTitle("Chọn kỳ / năm học")
                    .setSingleChoiceItems(PERIODS, currentIndex, (dialog, which) -> {
                        currentPeriod = PERIODS[which];
                        tvSelectedPeriod.setText(currentPeriod);
                        showPeriodData(which);
                        dialog.dismiss();
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }

    private void buildMockData() {
        // Năm học
        List<LeaderboardEntry> yearly = new ArrayList<>();
        yearly.add(new LeaderboardEntry(4,  "Hoàng Văn Khánh",   "VK", 132, 3.85, false));
        yearly.add(new LeaderboardEntry(5,  "Nguyễn Thị Lan",    "TL", 136, 3.82, false));
        yearly.add(new LeaderboardEntry(6,  "Đặng Quốc",         "QT", 130, 3.79, false));
        yearly.add(new LeaderboardEntry(7,  "Thùy Linh",         "TL", 134, 3.76, false));
        yearly.add(new LeaderboardEntry(8,  "Bùi Minh Nhật",     "MN", 128, 3.74, false));
        yearly.add(new LeaderboardEntry(9,  "Lý Cơ Công",        "TC", 138, 3.72, false));
        yearly.add(new LeaderboardEntry(10, "Phan Thị Hoa",      "TH", 136, 3.70, false));
        yearly.add(new LeaderboardEntry(11, "Cao Ngọc",          "NH", 131, 3.68, false));
        yearly.add(new LeaderboardEntry(12, "Đinh Quang Vinh",   "QV", 135, 3.65, false));
        yearly.add(new LeaderboardEntry(13, "Ngô Thị Bích Ngọc", "BN", 133, 3.64, false));
        yearly.add(new LeaderboardEntry(14, "Tô Minh Khoa",      "MK", 129, 3.63, false));
        yearly.add(new LeaderboardEntry(15, "Trần Văn Bình",     "VB", 128, 3.62, true));
        yearly.add(new LeaderboardEntry(16, "Hồ Huệ Nam",        "SN", 126, 3.60, false));
        yearly.add(new LeaderboardEntry(17, "Chu Thị Đầu",       "TH", 130, 3.58, false));
        yearly.add(new LeaderboardEntry(18, "Dương Văn",         "VD", 127, 3.55, false));
        periodData.add(yearly);

        // HK1 2024-2025
        List<LeaderboardEntry> hk1 = new ArrayList<>();
        hk1.add(new LeaderboardEntry(1, "Nguyễn Thị Lan",   "TL", 18, 4.00, false));
        hk1.add(new LeaderboardEntry(2, "Đặng Quốc",        "QT", 17, 3.96, false));
        hk1.add(new LeaderboardEntry(3, "Hoàng Văn Khánh",  "VK", 18, 3.92, false));
        hk1.add(new LeaderboardEntry(4, "Bùi Minh Nhật",    "MN", 16, 3.88, false));
        hk1.add(new LeaderboardEntry(5, "Lý Cơ Công",       "TC", 19, 3.84, false));
        hk1.add(new LeaderboardEntry(6, "Phan Thị Hoa",     "TH", 17, 3.80, false));
        hk1.add(new LeaderboardEntry(7, "Cao Ngọc",         "NH", 18, 3.72, false));
        hk1.add(new LeaderboardEntry(8, "Trần Văn Bình",    "VB", 17, 3.68, true));
        hk1.add(new LeaderboardEntry(9, "Đinh Quang Vinh",  "QV", 16, 3.60, false));
        hk1.add(new LeaderboardEntry(10,"Ngô Thị Bích Ngọc","BN", 17, 3.52, false));
        periodData.add(hk1);

        // HK2 2023-2024
        List<LeaderboardEntry> hk2 = new ArrayList<>();
        hk2.add(new LeaderboardEntry(1,  "Hoàng Văn Khánh",  "VK", 19, 4.00, false));
        hk2.add(new LeaderboardEntry(2,  "Nguyễn Thị Lan",   "TL", 18, 3.92, false));
        hk2.add(new LeaderboardEntry(3,  "Bùi Minh Nhật",    "MN", 17, 3.88, false));
        hk2.add(new LeaderboardEntry(4,  "Phan Thị Hoa",     "TH", 18, 3.80, false));
        hk2.add(new LeaderboardEntry(5,  "Cao Ngọc",         "NH", 17, 3.76, false));
        hk2.add(new LeaderboardEntry(6,  "Đinh Quang Vinh",  "QV", 18, 3.70, false));
        hk2.add(new LeaderboardEntry(7,  "Tô Minh Khoa",     "MK", 17, 3.64, false));
        hk2.add(new LeaderboardEntry(12, "Trần Văn Bình",    "VB", 16, 3.55, true));
        periodData.add(hk2);

        // HK1 2023-2024
        List<LeaderboardEntry> hk1old = new ArrayList<>();
        hk1old.add(new LeaderboardEntry(1, "Lý Cơ Công",       "TC", 20, 4.00, false));
        hk1old.add(new LeaderboardEntry(2, "Đặng Quốc",        "QT", 19, 3.96, false));
        hk1old.add(new LeaderboardEntry(3, "Nguyễn Thị Lan",   "TL", 18, 3.92, false));
        hk1old.add(new LeaderboardEntry(4, "Bùi Minh Nhật",    "MN", 17, 3.88, false));
        hk1old.add(new LeaderboardEntry(5, "Trần Văn Bình",    "VB", 19, 3.80, true));
        hk1old.add(new LeaderboardEntry(6, "Hoàng Văn Khánh",  "VK", 18, 3.76, false));
        periodData.add(hk1old);
    }

    private void setupRecyclerView() {
        leaderboardAdapter = new LeaderboardAdapter(requireContext(), new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(leaderboardAdapter);
        recyclerView.setNestedScrollingEnabled(false);
    }

    private void showPeriodData(int index) {
        double[] stats = PERIOD_STATS[index];
        tvMyRank.setText("# " + (int) stats[0]);
        tvMyGpa.setText(String.format(Locale.getDefault(), "%.2f", stats[1]).replace('.', ','));
        tvTopPercent.setText((int) stats[2] + " %");
        leaderboardAdapter.updateList(periodData.get(index));
    }
}
