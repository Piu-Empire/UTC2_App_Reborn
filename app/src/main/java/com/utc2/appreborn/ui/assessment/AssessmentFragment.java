package com.utc2.appreborn.ui.assessment;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.utc2.appreborn.R;
import com.utc2.appreborn.databinding.FragmentAssessmentBinding;
import com.utc2.appreborn.model.AssessmentPeriod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AssessmentFragment extends Fragment {

    // ─── Colors ───────────────────────────────────────────────────────────────
    private static final int COLOR_YELLOW = 0xFFFFC107;
    private static final int COLOR_GRAY   = 0xFFDDDDDD;
    private static final int COLOR_BLACK  = 0xFF111111;

    /**
     * FIX CHÍNH: LiquidBar (bottom_bar_compose) cao cố định 80dp và đè lên
     * fragment. WindowInsets.navigationBars chỉ trả về system gesture/button bar,
     * KHÔNG tính LiquidBar. Cần cộng thêm 80dp vào navBarSpacer.
     */
    private static final int LIQUID_BAR_HEIGHT_DP = 80;

    // ─── Column labels ────────────────────────────────────────────────────────
    private static final List<String> COLUMN_LABELS = Arrays.asList(
            "SV đánh giá", "Tập thể lớp", "Khoa/BM", "Trường");

    // ─── Fields ───────────────────────────────────────────────────────────────
    private FragmentAssessmentBinding binding;
    private AssessmentViewModel       viewModel;
    private AssessmentAdapter         adapter;
    private int                       pendingEvidenceCriteriaId = -1;
    private boolean                   isStudentTab              = true;

    private final ActivityResultLauncher<String> filePickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {
                        if (uri == null || pendingEvidenceCriteriaId == -1) return;
                        try {
                            requireContext().getContentResolver()
                                    .takePersistableUriPermission(
                                            uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        } catch (SecurityException ignored) {}
                        viewModel.addEvidenceUri(pendingEvidenceCriteriaId, uri.toString());
                        adapter.notifyEvidenceUpdated(pendingEvidenceCriteriaId);
                        Toast.makeText(requireContext(),
                                R.string.assessment_toast_evidence_attached,
                                Toast.LENGTH_SHORT).show();
                        pendingEvidenceCriteriaId = -1;
                    });

    // ─── Lifecycle ────────────────────────────────────────────────────────────

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAssessmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AssessmentViewModel.class);

        applyWindowInsets();
        setupBackButton();
        setupAdapter();
        setupTabSwitcher();
        setupColumnDropdown();
        setupPeriodDropdown();
        setupActionButtons();
        observeViewModel();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ─── Window Insets ────────────────────────────────────────────────────────

    /**
     * FIX: navBarSpacer = LiquidBar (80dp) + system navigation bar inset.
     *
     * LiquidBar là ComposeView có height="80dp" được đặt chồng lên fragment
     * container trong activity_main.xml. Nó không phải system inset nên
     * WindowInsetsCompat không biết đến nó. Phải cộng thủ công.
     */
    private void applyWindowInsets() {
        int liquidBarPx = dpToPx(LIQUID_BAR_HEIGHT_DP);

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            int statusH = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            int sysNavH = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;

            // Status bar spacer
            ViewGroup.LayoutParams lpTop = binding.statusBarSpacer.getLayoutParams();
            lpTop.height = statusH;
            binding.statusBarSpacer.setLayoutParams(lpTop);

            // Nav bar spacer = LiquidBar height + system nav bar height
            ViewGroup.LayoutParams lpBot = binding.navBarSpacer.getLayoutParams();
            lpBot.height = liquidBarPx + sysNavH;
            binding.navBarSpacer.setLayoutParams(lpBot);

            return insets;
        });
    }

    private int dpToPx(int dp) {
        return Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp,
                requireContext().getResources().getDisplayMetrics()));
    }

    // ─── Back ─────────────────────────────────────────────────────────────────

    private void setupBackButton() {
        binding.btnBack.setOnClickListener(v -> {
            if (requireActivity().getSupportFragmentManager().getBackStackEntryCount() > 0)
                requireActivity().getSupportFragmentManager().popBackStack();
        });
    }

    // ─── Adapter ──────────────────────────────────────────────────────────────

    private void setupAdapter() {
        adapter = new AssessmentAdapter(
                updatedList -> viewModel.onScoreChanged(updatedList),
                new AssessmentAdapter.OnEvidenceClickListener() {
                    @Override
                    public void onAddEvidence(int criteriaId) {
                        pendingEvidenceCriteriaId = criteriaId;
                        filePickerLauncher.launch("*/*");
                    }

                    @Override
                    public void onRemoveEvidence(int criteriaId, int fileIndex) {
                        viewModel.removeEvidenceUri(criteriaId, fileIndex);
                        adapter.notifyEvidenceUpdated(criteriaId);
                        Toast.makeText(requireContext(),
                                R.string.assessment_toast_evidence_removed,
                                Toast.LENGTH_SHORT).show();
                    }
                });

        binding.rvCriteria.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvCriteria.setAdapter(adapter);
        binding.rvCriteria.setItemAnimator(null);
    }

    // ─── Tab Switcher ─────────────────────────────────────────────────────────

    private void setupTabSwitcher() {
        selectTab(true);

        binding.btnTabRlsv.setOnClickListener(v -> {
            if (!isStudentTab) {
                selectTab(true);
                viewModel.switchTab(true);
                adapter.setStudentTab(true);
            }
        });

        binding.btnTabCvht.setOnClickListener(v -> {
            if (isStudentTab) {
                selectTab(false);
                viewModel.switchTab(false);
                adapter.setStudentTab(false);
            }
        });

        binding.btnChoose.setOnClickListener(v ->
                viewModel.switchTab(isStudentTab));
    }

    private void selectTab(boolean rlsv) {
        isStudentTab = rlsv;

        binding.btnTabRlsv.setBackgroundTintList(
                ColorStateList.valueOf(rlsv ? COLOR_YELLOW : COLOR_GRAY));
        binding.btnTabCvht.setBackgroundTintList(
                ColorStateList.valueOf(rlsv ? COLOR_GRAY   : COLOR_YELLOW));
        binding.btnTabRlsv.setTextColor(COLOR_BLACK);
        binding.btnTabCvht.setTextColor(COLOR_BLACK);

        binding.layoutHeaderRlsv.setVisibility(rlsv ? View.VISIBLE : View.GONE);
        binding.layoutHeaderCvht.setVisibility(rlsv ? View.GONE   : View.VISIBLE);

        binding.actvColumnMode.setVisibility(rlsv ? View.VISIBLE : View.GONE);
        binding.tvColHeaderFixed.setVisibility(rlsv ? View.GONE   : View.VISIBLE);

        binding.layoutBottomRlsv.setVisibility(rlsv ? View.VISIBLE : View.GONE);
        binding.layoutBottomCvht.setVisibility(rlsv ? View.GONE   : View.VISIBLE);
    }

    // ─── Column Dropdown ──────────────────────────────────────────────────────

    private void setupColumnDropdown() {
        // FIX: dùng dropdown_score_item.xml → nền trắng chữ đen, đẹp hơn
        ArrayAdapter<String> colAdapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.dropdown_score_item,
                COLUMN_LABELS);
        binding.actvColumnMode.setAdapter(colAdapter);
        binding.actvColumnMode.setText(COLUMN_LABELS.get(AssessmentAdapter.COL_SV), false);
        applyDropdownStyle(binding.actvColumnMode);

        binding.actvColumnMode.setOnItemClickListener((p, v, pos, id) ->
                adapter.setColumnMode(pos));
    }

    // ─── Period Dropdown ──────────────────────────────────────────────────────

    private void setupPeriodDropdown() {
        viewModel.getPeriods().observe(getViewLifecycleOwner(), periods -> {
            if (periods == null || periods.isEmpty()) return;

            List<String> labels = new ArrayList<>();
            for (AssessmentPeriod p : periods) labels.add(p.getLabel());

            ArrayAdapter<String> pa = new ArrayAdapter<>(
                    requireContext(),
                    R.layout.dropdown_score_item,
                    labels);
            binding.actvPeriod.setAdapter(pa);
            binding.actvPeriod.setText(labels.get(0), false);
            viewModel.setSelectedPeriod(periods.get(0));
            applyDropdownStyle(binding.actvPeriod);

            binding.actvPeriod.setOnItemClickListener((p, v, pos, id) ->
                    viewModel.setSelectedPeriod(periods.get(pos)));
        });
    }

    // ─── Action Buttons ───────────────────────────────────────────────────────

    private void setupActionButtons() {

        // RLSV: Lưu đánh giá
        binding.btnSave.setOnClickListener(v -> {
            animateButtonPress(binding.btnSave);
            setButtonLoading(binding.btnSave, true, "Đang lưu...");
            viewModel.saveAssessment(adapter.getItems(), success -> {
                setButtonLoading(binding.btnSave, false, getString(R.string.assessment_btn_save));
                if (success) {
                    showSuccessToast("✓ Lưu đánh giá thành công!");
                } else {
                    showErrorToast("✗ Lỗi khi lưu, vui lòng thử lại");
                }
            });
        });

        // RLSV: In đánh giá
        binding.btnPrint.setOnClickListener(v -> {
            animateButtonPress(binding.btnPrint);
            Toast.makeText(requireContext(),
                    "Chức năng in đang phát triển", Toast.LENGTH_SHORT).show();
        });

        // CVHT: Nộp đánh giá
        binding.btnSubmitCvht.setOnClickListener(v -> {
            animateButtonPress(binding.btnSubmitCvht);
            setButtonLoading(binding.btnSubmitCvht, true, "Đang nộp...");
            viewModel.submitCvht(adapter.getItems(), "", success -> {
                setButtonLoading(binding.btnSubmitCvht, false,
                        getString(R.string.assessment_btn_submit_cvht));
                if (success) {
                    showSuccessToast("✓ Nộp đánh giá CVHT thành công!");
                } else {
                    showErrorToast("✗ Lỗi khi nộp, vui lòng thử lại");
                }
            });
        });
    }

    // ─── Observers ────────────────────────────────────────────────────────────

    private void observeViewModel() {
        viewModel.getCriteria().observe(getViewLifecycleOwner(), list -> {
            binding.progressBar.setVisibility(View.GONE);
            adapter.submitList(list);
        });

        viewModel.getTotalScore().observe(getViewLifecycleOwner(), score ->
                binding.tvTotalScore.setText(fmt(score)));

        viewModel.getClassification().observe(getViewLifecycleOwner(), cls ->
                binding.tvClassification.setText(cls));

        viewModel.getStudentName().observe(getViewLifecycleOwner(), name ->
                binding.tvStudentName.setText(name));
        viewModel.getStudentCode().observe(getViewLifecycleOwner(), code ->
                binding.tvStudentCode.setText(code));
        viewModel.getStudentClass().observe(getViewLifecycleOwner(), cls ->
                binding.tvStudentClass.setText(cls));

        viewModel.getStudentName().observe(getViewLifecycleOwner(), name ->
                binding.tvCvhtStudentName.setText(name));
        viewModel.getAdvisorName().observe(getViewLifecycleOwner(), name ->
                binding.tvAdvisorName.setText(name));
    }

    // ─── UI Helpers ───────────────────────────────────────────────────────────

    /**
     * FIX: Áp dụng popup background trắng cho AutoCompleteTextView.
     * Mặc định Android dùng dark popup trên nhiều ROM → xấu.
     * Dùng setDropDownBackgroundDrawable() để override thành trắng.
     */
    private void applyDropdownStyle(android.widget.AutoCompleteTextView actv) {
        actv.setDropDownBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.WHITE));

        // Mở dropdown khi click
        actv.setOnClickListener(v -> actv.showDropDown());
        actv.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) actv.showDropDown();
        });
    }

    /**
     * Cũng áp dụng cho score dropdowns trong Adapter — gọi từ Adapter qua callback.
     * Adapter sẽ gọi applyDropdownStylePublic() khi tạo ViewHolder.
     */
    public static void applyScoreDropdownStyle(android.widget.AutoCompleteTextView actv) {
        actv.setDropDownBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.WHITE));
        actv.setOnClickListener(v -> actv.showDropDown());
        actv.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) actv.showDropDown();
        });
    }

    /** Scale nhỏ lại rồi trở về — hiệu ứng "press" khi bấm nút */
    private void animateButtonPress(View btn) {
        btn.animate()
                .scaleX(0.94f).scaleY(0.94f)
                .setDuration(80)
                .withEndAction(() -> btn.animate()
                        .scaleX(1f).scaleY(1f)
                        .setDuration(80)
                        .start())
                .start();
    }

    /** Disable nút + đổi text tạm khi đang xử lý */
    private void setButtonLoading(
            com.google.android.material.button.MaterialButton btn,
            boolean loading,
            String originalText) {
        btn.setEnabled(!loading);
        btn.setAlpha(loading ? 0.65f : 1f);
        btn.setText(loading ? "..." : originalText);
    }

    private void showSuccessToast(String msg) {
        Toast toast = Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void showErrorToast(String msg) {
        Toast toast = Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG);
        toast.show();
    }

    private String fmt(float v) {
        return (v == (int) v) ? String.valueOf((int) v) : String.valueOf(v);
    }
}