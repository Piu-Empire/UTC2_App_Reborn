package com.utc2.appreborn.ui.results.warning;

import android.content.res.ColorStateList;
import android.graphics.Color;
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

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.adapter.WarningAdapter;
import com.utc2.appreborn.model.AcademicWarning;

import java.util.ArrayList;
import java.util.List;

public class WarningsFragment extends Fragment {

    private View     btnBack;
    private TextView tvCountSerious;
    private TextView tvCountTotal;
    private ChipGroup chipGroupFilter;
    private RecyclerView recyclerView;
    private WarningAdapter warningAdapter;

    private final List<AcademicWarning> allWarnings = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_warnings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnBack         = view.findViewById(R.id.btn_back);
        tvCountSerious  = view.findViewById(R.id.tv_count_serious);
        tvCountTotal    = view.findViewById(R.id.tv_count_total);
        chipGroupFilter = view.findViewById(R.id.chip_group_filter);
        recyclerView    = view.findViewById(R.id.recycler_warnings);

        btnBack.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        loadMockData();
        setupRecyclerView();
        updateStatCards();
        setupChipFilter();
        chipGroupFilter.post(() -> styleAllChips(chipGroupFilter));
        applyFilter(R.id.chip_all);
    }

    // ─────────────────────────────────────────
    // Chip colors — set cứng, không bị DayNight ảnh hưởng, không có dấu tick
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
        List<AcademicWarning> filtered = new ArrayList<>();
        for (AcademicWarning w : allWarnings) {
            if (chipId == R.id.chip_all || w.isSerious()) {
                filtered.add(w);
            }
        }
        warningAdapter.updateList(filtered);
    }

    private void updateStatCards() {
        int serious = 0;
        for (AcademicWarning w : allWarnings) {
            if (w.isSerious()) serious++;
        }
        tvCountSerious.setText(String.valueOf(serious));
        tvCountTotal.setText(String.valueOf(allWarnings.size()));
    }

    private void setupRecyclerView() {
        warningAdapter = new WarningAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(warningAdapter);
        recyclerView.setNestedScrollingEnabled(false);
    }

    private void loadMockData() {
        allWarnings.add(new AcademicWarning(1,
                "Điểm thi không đạt", "Triết học Mác-Lênin",
                "18/04/2026", AcademicWarning.TYPE_SERIOUS, AcademicWarning.ICON_BOOK));
        allWarnings.add(new AcademicWarning(2,
                "Điểm thi không đạt (HK1 2024-2025)", "Hệ điều hành",
                "15/01/2026", AcademicWarning.TYPE_SERIOUS, AcademicWarning.ICON_BOOK));
        allWarnings.add(new AcademicWarning(3,
                "Cảnh báo học vụ mức 1", null,
                "01/04/2026", AcademicWarning.TYPE_NORMAL, AcademicWarning.ICON_CLOCK));
        allWarnings.add(new AcademicWarning(4,
                "Học phí còn nợ", null,
                "01/03/2026", AcademicWarning.TYPE_SERIOUS, AcademicWarning.ICON_BOOK));
        allWarnings.add(new AcademicWarning(5,
                "Vắng mặt quá mức cho phép", "Lập trình Web",
                "10/04/2026", AcademicWarning.TYPE_NORMAL, AcademicWarning.ICON_CLOCK));
    }
}
