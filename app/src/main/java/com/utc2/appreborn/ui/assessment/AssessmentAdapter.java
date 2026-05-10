package com.utc2.appreborn.ui.assessment;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.utc2.appreborn.R;
import com.utc2.appreborn.databinding.ItemAssessmentCriteriaBinding;
import com.utc2.appreborn.databinding.ItemAssessmentDeductionBinding;
import com.utc2.appreborn.databinding.ItemAssessmentFooterCvhtBinding;
import com.utc2.appreborn.databinding.ItemAssessmentFooterRlsvBinding;
import com.utc2.appreborn.databinding.ItemAssessmentHeaderBinding;
import com.utc2.appreborn.model.AssessmentCriteria;

import java.util.ArrayList;
import java.util.List;

public class AssessmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int COL_SV     = 0;
    public static final int COL_TAPTHE = 1;
    public static final int COL_KHOA   = 2;
    public static final int COL_TRUONG = 3;

    public interface OnScoreChangedListener {
        void onScoreChanged(List<AssessmentCriteria> updatedList);
    }

    public interface OnEvidenceClickListener {
        void onAddEvidence(int criteriaId);
        void onRemoveEvidence(int criteriaId, int fileIndex);
    }

    private List<AssessmentCriteria> items        = new ArrayList<>();
    private final OnScoreChangedListener  scoreListener;
    private final OnEvidenceClickListener evidenceListener;
    private boolean isStudentTab = true;
    private int     columnMode   = COL_SV;

    public AssessmentAdapter(OnScoreChangedListener scoreListener,
                             OnEvidenceClickListener evidenceListener) {
        this.scoreListener    = scoreListener;
        this.evidenceListener = evidenceListener;
    }

    // ─── Data ─────────────────────────────────────────────────────────────────

    public void submitList(List<AssessmentCriteria> newList) {
        this.items = (newList != null) ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    public List<AssessmentCriteria> getItems() { return items; }

    public void setStudentTab(boolean isStudent) {
        this.isStudentTab = isStudent;
        if (!isStudent) this.columnMode = COL_SV;
        notifyDataSetChanged();
    }

    public void setColumnMode(int mode) {
        this.columnMode = mode;
        notifyDataSetChanged();
    }

    public void notifyEvidenceUpdated(int criteriaId) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId() == criteriaId) { notifyItemChanged(i); return; }
        }
    }

    // ─── RecyclerView ─────────────────────────────────────────────────────────

    @Override public int getItemViewType(int pos) { return items.get(pos).getViewType(); }
    @Override public int getItemCount()           { return items.size(); }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case AssessmentCriteria.TYPE_SECTION_HEADER:
                return new HeaderVH(ItemAssessmentHeaderBinding.inflate(inf, parent, false));
            case AssessmentCriteria.TYPE_DEDUCTION:
                return new DeductionVH(ItemAssessmentDeductionBinding.inflate(inf, parent, false));
            case AssessmentCriteria.TYPE_FOOTER_RLSV:
                return new FooterRlsvVH(ItemAssessmentFooterRlsvBinding.inflate(inf, parent, false));
            case AssessmentCriteria.TYPE_FOOTER_CVHT:
                return new FooterCvhtVH(ItemAssessmentFooterCvhtBinding.inflate(inf, parent, false));
            default:
                return new CriteriaVH(ItemAssessmentCriteriaBinding.inflate(inf, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AssessmentCriteria item = items.get(position);
        switch (item.getViewType()) {
            case AssessmentCriteria.TYPE_SECTION_HEADER:
                ((HeaderVH) holder).bind(item);                         break;
            case AssessmentCriteria.TYPE_DEDUCTION:
                ((DeductionVH) holder).bind(item, columnMode, isStudentTab); break;
            case AssessmentCriteria.TYPE_FOOTER_RLSV:
            case AssessmentCriteria.TYPE_FOOTER_CVHT:
                break;
            default:
                ((CriteriaVH) holder).bind(item, columnMode, isStudentTab);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  ViewHolder: Header
    // ══════════════════════════════════════════════════════════════════════════

    class HeaderVH extends RecyclerView.ViewHolder {
        private final ItemAssessmentHeaderBinding b;
        HeaderVH(ItemAssessmentHeaderBinding binding) { super(binding.getRoot()); b = binding; }

        void bind(AssessmentCriteria item) {
            b.tvSectionTitle.setText(item.getTitle());
            boolean isDeduct = item.getTitle() != null && item.getTitle().contains("TRỪ ĐIỂM");
            int color = isDeduct ? 0xFFCC0000 : 0xFF0057A8;
            b.tvSectionTitle.setTextColor(color);
            b.viewAccent.setBackgroundColor(color);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  ViewHolder: Criteria (tiêu chí thường)
    // ══════════════════════════════════════════════════════════════════════════

    class CriteriaVH extends RecyclerView.ViewHolder {
        private final ItemAssessmentCriteriaBinding b;
        private TextWatcher activeWatcher;

        CriteriaVH(ItemAssessmentCriteriaBinding binding) {
            super(binding.getRoot());
            b = binding;
        }

        void bind(AssessmentCriteria item, int colMode, boolean isStudent) {
            b.tvCriteriaTitle.setText(item.getTitle());

            if (item.getDescription() != null && !item.getDescription().isEmpty()) {
                b.tvDescription.setVisibility(View.VISIBLE);
                b.tvDescription.setText(item.getDescription());
            } else {
                b.tvDescription.setVisibility(View.GONE);
            }

            boolean editable = !isStudent || colMode == COL_SV;
            if (editable) {
                b.actvScore.setVisibility(View.VISIBLE);
                b.tvReadonlyScore.setVisibility(View.GONE);
                setupDropdown(b.actvScore, item);
            } else {
                b.actvScore.setVisibility(View.GONE);
                b.tvReadonlyScore.setVisibility(View.VISIBLE);
                b.tvReadonlyScore.setText(fmt(getReadonlyScore(item, colMode)));
            }

            if (item.isRequiresEvidence()) {
                b.layoutEvidenceSection.setVisibility(View.VISIBLE);
                refreshEvidence(item);
            } else {
                b.layoutEvidenceSection.setVisibility(View.GONE);
            }
        }

        private float getReadonlyScore(AssessmentCriteria item, int mode) {
            switch (mode) {
                case COL_TAPTHE: return item.getTapTheScore();
                case COL_KHOA:   return item.getKhoaScore();
                case COL_TRUONG: return item.getTruongScore();
                default:         return item.getCurrentScore();
            }
        }

        private void setupDropdown(AutoCompleteTextView actv, AssessmentCriteria item) {
            List<String> opts = buildOpts(item.getScoreOptions());
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    actv.getContext(), R.layout.dropdown_score_item, opts);
            actv.setAdapter(adapter);
            applyDropdownStyle(actv);

            if (activeWatcher != null) actv.removeTextChangedListener(activeWatcher);
            actv.setText(fmt(item.getCurrentScore()), false);

            activeWatcher = makeWatcher(item);
            actv.addTextChangedListener(activeWatcher);
        }

        private TextWatcher makeWatcher(AssessmentCriteria item) {
            return new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int a, int b2, int c) {}
                @Override public void onTextChanged(CharSequence s, int a, int b2, int c)     {}
                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        item.setCurrentScore(Float.parseFloat(s.toString().trim()));
                        if (scoreListener != null) scoreListener.onScoreChanged(items);
                    } catch (NumberFormatException ignored) {}
                }
            };
        }

        private void refreshEvidence(AssessmentCriteria item) {
            List<String> uris = item.getEvidenceUris();
            if (uris.isEmpty()) {
                b.btnEvidence.setVisibility(View.VISIBLE);
                b.layoutEvidenceList.setVisibility(View.GONE);
                b.btnAddMoreEvidence.setVisibility(View.GONE);
                b.btnEvidence.setOnClickListener(v -> {
                    if (evidenceListener != null) evidenceListener.onAddEvidence(item.getId());
                });
            } else {
                b.btnEvidence.setVisibility(View.GONE);
                b.layoutEvidenceList.setVisibility(View.VISIBLE);
                b.btnAddMoreEvidence.setVisibility(View.VISIBLE);
                b.layoutEvidenceList.removeAllViews();
                LayoutInflater inf = LayoutInflater.from(b.getRoot().getContext());
                for (int i = 0; i < uris.size(); i++) {
                    final int idx = i;
                    View row = inf.inflate(R.layout.item_evidence_file,
                            b.layoutEvidenceList, false);
                    ((TextView) row.findViewById(R.id.tvEvidenceFileName))
                            .setText(extractFileName(uris.get(i)));
                    row.findViewById(R.id.btnDeleteEvidence).setOnClickListener(v -> {
                        if (evidenceListener != null)
                            evidenceListener.onRemoveEvidence(item.getId(), idx);
                    });
                    b.layoutEvidenceList.addView(row);
                }
                b.btnAddMoreEvidence.setOnClickListener(v -> {
                    if (evidenceListener != null) evidenceListener.onAddEvidence(item.getId());
                });
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  ViewHolder: Deduction (điểm trừ)
    //
    //  FIX: Deduction cũng hỗ trợ readonly khi columnMode != COL_SV.
    //  Phần 6 (SINH VIÊN BỊ TRỪ ĐIỂM) chỉ được edit ở cột "SV đánh giá",
    //  còn Tập thể lớp / Khoa/BM / Trường hiện readonly giống criteria thường.
    // ══════════════════════════════════════════════════════════════════════════

    class DeductionVH extends RecyclerView.ViewHolder {
        private final ItemAssessmentDeductionBinding b;
        private TextWatcher activeWatcher;

        DeductionVH(ItemAssessmentDeductionBinding binding) {
            super(binding.getRoot());
            b = binding;
        }

        void bind(AssessmentCriteria item, int colMode, boolean isStudent) {
            b.tvDeductionTitle.setText(item.getTitle());

            // FIX: logic giống CriteriaVH — chỉ edit khi COL_SV
            boolean editable = !isStudent || colMode == COL_SV;
            if (editable) {
                b.actvDeductionScore.setVisibility(View.VISIBLE);
                b.tvReadonlyDeduction.setVisibility(View.GONE);
                setupDropdown(item);
            } else {
                b.actvDeductionScore.setVisibility(View.GONE);
                b.tvReadonlyDeduction.setVisibility(View.VISIBLE);
                // Readonly điểm trừ: lấy theo cột (mock = 0 cho các cột khác)
                b.tvReadonlyDeduction.setText(fmt(getReadonlyScore(item, colMode)));
            }
        }

        private float getReadonlyScore(AssessmentCriteria item, int mode) {
            // Điểm trừ ở Tập thể / Khoa / Trường thường là 0 (không áp dụng)
            switch (mode) {
                case COL_TAPTHE: return 0f;
                case COL_KHOA:   return 0f;
                case COL_TRUONG: return 0f;
                default:         return item.getCurrentScore();
            }
        }

        private void setupDropdown(AssessmentCriteria item) {
            List<String> opts = buildOpts(item.getScoreOptions());
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    b.actvDeductionScore.getContext(), R.layout.dropdown_score_item, opts);
            b.actvDeductionScore.setAdapter(adapter);
            applyDropdownStyle(b.actvDeductionScore);

            if (activeWatcher != null)
                b.actvDeductionScore.removeTextChangedListener(activeWatcher);
            b.actvDeductionScore.setText(fmt(item.getCurrentScore()), false);

            activeWatcher = new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int a, int b2, int c) {}
                @Override public void onTextChanged(CharSequence s, int a, int b2, int c)     {}
                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        item.setCurrentScore(Float.parseFloat(s.toString().trim()));
                        if (scoreListener != null) scoreListener.onScoreChanged(items);
                    } catch (NumberFormatException ignored) {}
                }
            };
            b.actvDeductionScore.addTextChangedListener(activeWatcher);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  ViewHolder: Footer
    // ══════════════════════════════════════════════════════════════════════════

    class FooterRlsvVH extends RecyclerView.ViewHolder {
        FooterRlsvVH(ItemAssessmentFooterRlsvBinding b) { super(b.getRoot()); }
    }

    class FooterCvhtVH extends RecyclerView.ViewHolder {
        FooterCvhtVH(ItemAssessmentFooterCvhtBinding b) { super(b.getRoot()); }
    }

    // ─── Shared helpers ───────────────────────────────────────────────────────

    /** Áp dụng popup trắng + click → showDropDown */
    private void applyDropdownStyle(AutoCompleteTextView actv) {
        actv.setDropDownBackgroundDrawable(new ColorDrawable(android.graphics.Color.WHITE));
        actv.setOnClickListener(v -> actv.showDropDown());
        actv.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) actv.showDropDown();
        });
    }

    private List<String> buildOpts(List<Float> scoreOptions) {
        List<String> opts = new ArrayList<>();
        if (scoreOptions != null)
            for (Float f : scoreOptions) opts.add(fmt(f));
        return opts;
    }

    private String extractFileName(String uri) {
        if (uri == null) return "file";
        int slash = uri.lastIndexOf('/');
        if (slash >= 0 && slash < uri.length() - 1) {
            String name = uri.substring(slash + 1);
            try { name = java.net.URLDecoder.decode(name, "UTF-8"); }
            catch (Exception ignored) {}
            return name;
        }
        return uri;
    }

    private String fmt(float v) {
        return (v == (int) v) ? String.valueOf((int) v) : String.valueOf(v);
    }
}