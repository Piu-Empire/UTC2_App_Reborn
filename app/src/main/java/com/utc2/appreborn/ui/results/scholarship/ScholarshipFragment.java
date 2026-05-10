package com.utc2.appreborn.ui.results.scholarship;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.adapter.ScholarshipAdapter;
import com.utc2.appreborn.model.Scholarship;

import java.util.ArrayList;
import java.util.List;

public class ScholarshipFragment extends Fragment {

    private View       btnBack;
    private ChipGroup  chipGroupFilter;
    private RecyclerView recyclerView;
    private ScholarshipAdapter scholarshipAdapter;

    private final List<Scholarship> allScholarships = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scholarship, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnBack         = view.findViewById(R.id.btn_back);
        chipGroupFilter = view.findViewById(R.id.chip_group_filter);
        recyclerView    = view.findViewById(R.id.recycler_scholarships);

        btnBack.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        loadMockData();
        setupRecyclerView();
        setupChipFilter();
        chipGroupFilter.post(() -> styleAllChips(chipGroupFilter));
        applyFilter(R.id.chip_all);
    }

    // ─────────────────────────────────────────
    // Chip colors — không dấu tick, set cứng tránh DayNight
    // ─────────────────────────────────────────
    private void styleAllChips(ChipGroup group) {
        for (int i = 0; i < group.getChildCount(); i++) {
            styleChip((Chip) group.getChildAt(i));
        }
    }

    private void styleChip(Chip chip) {
        boolean checked = chip.isChecked();
        chip.setChipBackgroundColor(
                ColorStateList.valueOf(checked ? Color.BLACK : Color.WHITE));
        chip.setTextColor(checked ? Color.WHITE : Color.parseColor("#212121"));
        chip.setChipStrokeColor(ColorStateList.valueOf(
                checked ? Color.BLACK : Color.parseColor("#CCCCCC")));
        chip.setChipStrokeWidth(checked ? 0f : 3f);
    }

    private void setupChipFilter() {
        chipGroupFilter.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                styleAllChips(group);
                applyFilter(checkedIds.get(0));
            }
        });
    }

    private void applyFilter(int chipId) {
        List<Scholarship> filtered = new ArrayList<>();
        for (Scholarship s : allScholarships) {
            if (chipId == R.id.chip_all) {
                filtered.add(s);
            } else if (chipId == R.id.chip_not_received && !s.isReceived()) {
                filtered.add(s);
            } else if (chipId == R.id.chip_received && s.isReceived()) {
                filtered.add(s);
            }
        }
        scholarshipAdapter.updateList(filtered);
    }

    private void setupRecyclerView() {
        scholarshipAdapter = new ScholarshipAdapter(requireContext(), new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(scholarshipAdapter);
        recyclerView.setNestedScrollingEnabled(false);
    }

    private void loadMockData() {
        allScholarships.add(new Scholarship("Học bổng Khuyến khích học tập",
                "Trường ĐH Bách Khoa HN", 3_000_000L, "HK",
                Scholarship.STATUS_NOT_RECEIVED, 3.20));
        allScholarships.add(new Scholarship("Học bổng JICA",
                "Cơ quan Hợp tác Nhật Bản", 20_000_000L, "năm",
                Scholarship.STATUS_RECEIVED, 3.70));
        allScholarships.add(new Scholarship("Học bổng Vallet",
                "Quỹ Vallet Việt Nam", 15_000_000L, "năm",
                Scholarship.STATUS_NOT_RECEIVED, 3.60));
        allScholarships.add(new Scholarship("Học bổng Vingroup",
                "Tập đoàn Vingroup", 8_000_000L, "HK",
                Scholarship.STATUS_NOT_RECEIVED, 3.50));
        allScholarships.add(new Scholarship("Học bổng Chính phủ",
                "Bộ Giáo dục và Đào tạo", 5_000_000L, "HK",
                Scholarship.STATUS_RECEIVED, 3.40));
        allScholarships.add(new Scholarship("Học bổng KKHT Loại A",
                "Trường ĐH Công Thương TP.HCM", 4_500_000L, "HK",
                Scholarship.STATUS_NOT_RECEIVED, 3.60));
    }
}
