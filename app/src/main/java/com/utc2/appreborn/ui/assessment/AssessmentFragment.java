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

    private static final int COLOR_YELLOW       = 0xFFFFC107;
    private static final int COLOR_GRAY         = 0xFFDDDDDD;
    private static final int COLOR_BLACK        = 0xFF111111;
    private static final int LIQUID_BAR_DP      = 80;

    private static final List<String> COLUMN_LABELS = Arrays.asList(
            "SV đánh giá", "Tập thể lớp", "Khoa/BM", "Trường");

    private FragmentAssessmentBinding binding;
    private AssessmentViewModel       viewModel;
    private AssessmentAdapter         adapter;
    private int                       pendingEvidenceCriteriaId = -1;
    private boolean                   isStudentTab              = true;

    /** Học kỳ user đã chọn trong dropdown — chỉ load khi bấm nút Chọn */
    private AssessmentPeriod pendingPeriod = null;

    private final ActivityResultLauncher<String> filePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
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

    private void applyWindowInsets() {
        int liquidBarPx = dpToPx(LIQUID_BAR_DP);
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            int statusH = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            int sysNavH = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;

            ViewGroup.LayoutParams lpTop = binding.statusBarSpacer.getLayoutParams();
            lpTop.height = statusH;
            binding.statusBarSpacer.setLayoutParams(lpTop);

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
            if (!isStudentTab) { selectTab(true); viewModel.switchTab(true); adapter.setStudentTab(true); }
        });
        binding.btnTabCvht.setOnClickListener(v -> {
            if (isStudentTab) { selectTab(false); viewModel.switchTab(false); adapter.setStudentTab(false); }
        });
    }

    private void selectTab(boolean rlsv) {
        isStudentTab = rlsv;
        binding.btnTabRlsv.setBackgroundTintList(ColorStateList.valueOf(rlsv ? COLOR_YELLOW : COLOR_GRAY));
        binding.btnTabCvht.setBackgroundTintList(ColorStateList.valueOf(rlsv ? COLOR_GRAY   : COLOR_YELLOW));
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
        ArrayAdapter<String> colAdapter = new ArrayAdapter<>(
                requireContext(), R.layout.dropdown_score_item, COLUMN_LABELS);
        binding.actvColumnMode.setAdapter(colAdapter);
        binding.actvColumnMode.setText(COLUMN_LABELS.get(AssessmentAdapter.COL_SV), false);
        binding.actvColumnMode.setDropDownBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.WHITE));
        binding.actvColumnMode.setOnClickListener(v -> binding.actvColumnMode.showDropDown());
        binding.actvColumnMode.setOnFocusChangeListener((v, has) -> {
            if (has) binding.actvColumnMode.showDropDown();
        });
        binding.actvColumnMode.setOnItemClickListener((p, v, pos, id) ->
                adapter.setColumnMode(pos));
    }

    // ─── Period Dropdown ──────────────────────────────────────────────────────

    /**
     * FIX: onItemClick chỉ ghi nhớ lựa chọn vào pendingPeriod.
     * Dữ liệu chỉ thực sự được load khi bấm nút Chọn.
     */
    private void setupPeriodDropdown() {
        viewModel.getPeriods().observe(getViewLifecycleOwner(), periods -> {
            if (periods == null || periods.isEmpty()) return;

            List<String> labels = new ArrayList<>();
            for (AssessmentPeriod p : periods) labels.add(p.getLabel());

            ArrayAdapter<String> pa = new ArrayAdapter<>(
                    requireContext(), R.layout.dropdown_score_item, labels);
            binding.actvPeriod.setAdapter(pa);
            binding.actvPeriod.setDropDownBackgroundDrawable(
                    new ColorDrawable(android.graphics.Color.WHITE));

            // Hiện học kỳ đầu tiên mặc định, ghi vào pendingPeriod
            binding.actvPeriod.setText(labels.get(0), false);
            pendingPeriod = periods.get(0);

            binding.actvPeriod.setOnClickListener(v -> binding.actvPeriod.showDropDown());
            binding.actvPeriod.setOnFocusChangeListener((v, has) -> {
                if (has) binding.actvPeriod.showDropDown();
            });

            // Chọn xong chỉ lưu vào pendingPeriod, KHÔNG load ngay
            binding.actvPeriod.setOnItemClickListener((p, v, pos, id) ->
                    pendingPeriod = periods.get(pos));

            // Nút Chọn mới thực sự load dữ liệu
            binding.btnChoose.setOnClickListener(v -> {
                if (pendingPeriod != null) {
                    animateButtonPress(binding.btnChoose);
                    binding.progressBar.setVisibility(View.VISIBLE);
                    viewModel.setSelectedPeriod(pendingPeriod);
                    viewModel.switchTab(isStudentTab);
                }
            });
        });
    }

    // ─── Action Buttons ───────────────────────────────────────────────────────

    private void setupActionButtons() {
        binding.btnSave.setOnClickListener(v -> {
            animateButtonPress(binding.btnSave);
            setButtonLoading(binding.btnSave, true, "Đang lưu...");
            viewModel.saveAssessment(adapter.getItems(), success -> {
                setButtonLoading(binding.btnSave, false,
                        getString(R.string.assessment_btn_save));
                if (success) showToast("✓ Lưu đánh giá thành công!", false);
                else         showToast("✗ Lỗi khi lưu, vui lòng thử lại", true);
            });
        });

        binding.btnPrint.setOnClickListener(v -> {
            animateButtonPress(binding.btnPrint);
            showToast("Chức năng in đang phát triển", false);
        });

        binding.btnSubmitCvht.setOnClickListener(v -> {
            animateButtonPress(binding.btnSubmitCvht);
            setButtonLoading(binding.btnSubmitCvht, true, "Đang nộp...");
            viewModel.submitCvht(adapter.getItems(), "", success -> {
                setButtonLoading(binding.btnSubmitCvht, false,
                        getString(R.string.assessment_btn_submit_cvht));
                if (success) showToast("✓ Nộp đánh giá CVHT thành công!", false);
                else         showToast("✗ Lỗi khi nộp, vui lòng thử lại", true);
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

        viewModel.getStudentName().observe(getViewLifecycleOwner(), name -> {
            binding.tvStudentName.setText(name);
            binding.tvCvhtStudentName.setText(name);
        });
        viewModel.getStudentCode().observe(getViewLifecycleOwner(), code ->
                binding.tvStudentCode.setText(code));
        viewModel.getStudentClass().observe(getViewLifecycleOwner(), cls ->
                binding.tvStudentClass.setText(cls));
        viewModel.getAdvisorName().observe(getViewLifecycleOwner(), name ->
                binding.tvAdvisorName.setText(name));
    }

    // ─── UI Helpers ───────────────────────────────────────────────────────────

    private void animateButtonPress(View btn) {
        btn.animate().scaleX(0.93f).scaleY(0.93f).setDuration(70)
                .withEndAction(() -> btn.animate()
                        .scaleX(1f).scaleY(1f).setDuration(70).start())
                .start();
    }

    private void setButtonLoading(
            com.google.android.material.button.MaterialButton btn,
            boolean loading, String text) {
        btn.setEnabled(!loading);
        btn.setAlpha(loading ? 0.65f : 1f);
        btn.setText(loading ? "..." : text);
    }

    private void showToast(String msg, boolean isError) {
        Toast.makeText(requireContext(), msg,
                isError ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

    private String fmt(float v) {
        return (v == (int) v) ? String.valueOf((int) v) : String.valueOf(v);
    }
}