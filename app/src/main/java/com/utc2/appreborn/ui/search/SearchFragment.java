package com.utc2.appreborn.ui.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.utc2.appreborn.R;
import com.utc2.appreborn.databinding.FragmentSearchBinding;
import com.utc2.appreborn.ui.main.MainActivity;
import com.utc2.appreborn.ui.news.NewsDetailActivity;
import com.utc2.appreborn.ui.home.model.NewsItem;
import com.utc2.appreborn.ui.home.adapter.NewsAdapter;

import android.content.Intent;

/**
 * SearchFragment
 * ──────────────────────────────────────────────────────────────
 * Màn hình tìm kiếm toàn cục của ứng dụng.
 *
 * Chức năng:
 *   • Tìm kiếm theo tên tính năng → điều hướng trực tiếp đến màn hình đó
 *   • Tìm kiếm theo tiêu đề/nội dung tin tức → mở NewsDetailActivity
 *   • Gợi ý tìm kiếm nhanh (chip) cho các tính năng phổ biến
 *   • Hiển thị trạng thái: loading / empty / kết quả
 *
 * Pattern: MVVM — SearchViewModel xử lý logic, Fragment chỉ observe & render.
 *
 * Package: com.utc2.appreborn.ui.search
 */
public class SearchFragment extends Fragment {

    public static final String TAG = "SearchFragment";

    private FragmentSearchBinding binding;
    private SearchViewModel viewModel;
    private SearchResultAdapter resultAdapter;

    public SearchFragment() {
        super(R.layout.fragment_search);
    }

    // ═══════════════════════════════════════════════════════════
    //  Lifecycle
    // ═══════════════════════════════════════════════════════════

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        setupToolbar();
        setupSearchInput();
        setupResultList();
        setupQuickSuggestions();
        observeViewModel();

        // Tự động mở bàn phím khi vào màn hình tìm kiếm
        focusSearchInput();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ═══════════════════════════════════════════════════════════
    //  Setup UI
    // ═══════════════════════════════════════════════════════════

    private void setupToolbar() {
        binding.btnBack.setOnClickListener(v -> {
            hideKeyboard();
            requireActivity().onBackPressed();
        });
    }

    private void setupSearchInput() {
        // Lắng nghe thay đổi văn bản → tìm kiếm realtime
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                viewModel.search(query);

                // Hiển thị/ẩn nút xóa
                binding.btnClear.setVisibility(
                        query.isEmpty() ? View.GONE : View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Nhấn "Search" trên bàn phím → ẩn bàn phím
        binding.etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard();
                return true;
            }
            return false;
        });

        // Nút X xóa nội dung
        binding.btnClear.setOnClickListener(v -> {
            binding.etSearch.setText("");
            binding.etSearch.requestFocus();
        });
    }

    private void setupResultList() {
        resultAdapter = new SearchResultAdapter(item -> {
            hideKeyboard();
            handleResultClick(item);
        });

        binding.rvResults.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvResults.setNestedScrollingEnabled(false);
        binding.rvResults.setAdapter(resultAdapter);
    }

    private void setupQuickSuggestions() {
        // Chip gợi ý nhanh — nhấn vào điền vào ô tìm kiếm
        binding.chipHocPhi.setOnClickListener(v ->
                binding.etSearch.setText(getString(R.string.feature_hoc_phi)));

        binding.chipLich.setOnClickListener(v ->
                binding.etSearch.setText("Lịch học"));

        binding.chipDichVu.setOnClickListener(v ->
                binding.etSearch.setText(getString(R.string.feature_dich_vu_cong)));

        binding.chipDanhGia.setOnClickListener(v ->
                binding.etSearch.setText(getString(R.string.feature_danh_gia)));

        binding.chipKtx.setOnClickListener(v ->
                binding.etSearch.setText(getString(R.string.feature_ki_tuc_xa)));
    }

    // ═══════════════════════════════════════════════════════════
    //  Observers
    // ═══════════════════════════════════════════════════════════

    private void observeViewModel() {
        viewModel.getSearchResults().observe(getViewLifecycleOwner(), results -> {
            if (results == null || results.isEmpty()) {
                boolean hasQuery = !binding.etSearch.getText().toString().trim().isEmpty();
                showEmptyState(hasQuery);
            } else {
                showResults(results);
            }
        });
    }

    // ═══════════════════════════════════════════════════════════
    //  UI State helpers
    // ═══════════════════════════════════════════════════════════

    /**
     * Hiển thị danh sách kết quả.
     */
    private void showResults(java.util.List<SearchResult> results) {
        binding.layoutEmpty.setVisibility(View.GONE);
        binding.layoutSuggestions.setVisibility(View.GONE);
        binding.rvResults.setVisibility(View.VISIBLE);
        resultAdapter.submitList(results);
    }

    /**
     * Hiển thị trạng thái rỗng.
     * @param hasQuery true → "Không tìm thấy kết quả", false → màn hình gợi ý ban đầu
     */
    private void showEmptyState(boolean hasQuery) {
        binding.rvResults.setVisibility(View.GONE);

        if (hasQuery) {
            // Đã nhập nhưng không có kết quả
            binding.layoutEmpty.setVisibility(View.VISIBLE);
            binding.layoutSuggestions.setVisibility(View.GONE);
            binding.tvEmptyQuery.setText(
                    String.format("\"%s\"", binding.etSearch.getText().toString().trim()));
        } else {
            // Chưa nhập → hiện gợi ý nhanh
            binding.layoutEmpty.setVisibility(View.GONE);
            binding.layoutSuggestions.setVisibility(View.VISIBLE);
        }
    }

    // ═══════════════════════════════════════════════════════════
    //  Click handler
    // ═══════════════════════════════════════════════════════════

    private void handleResultClick(SearchResult item) {
        MainActivity main = (MainActivity) requireActivity();

        switch (item.getType()) {
            case FEATURE:
                // Điều hướng đến tính năng tương ứng
                navigateToFeature(item.getFeatureId(), main);
                break;

            case NEWS:
                // Mở màn hình chi tiết tin tức
                Intent intent = new Intent(requireContext(), NewsDetailActivity.class);
                intent.putExtra(NewsDetailActivity.EXTRA_TITLE,   item.getTitle());
                intent.putExtra(NewsDetailActivity.EXTRA_DATE,    item.getSubtitle());
                intent.putExtra(NewsDetailActivity.EXTRA_CONTENT, item.getContent());
                startActivity(intent);
                break;
        }
    }

    private void navigateToFeature(String featureId, MainActivity main) {
        switch (featureId) {
            case "hoc_phi":
                main.pushFragment(
                        new com.utc2.appreborn.ui.tuition.TuitionFragment(),
                        "TuitionFragment");
                break;
            case "dich_vu_cong":
                main.pushFragment(
                        new com.utc2.appreborn.ui.public_services.PublicServiceFragment(),
                        "PublicServiceFragment");
                break;
            case "danh_gia":
                main.pushFragment(
                        new com.utc2.appreborn.ui.assessment.AssessmentFragment(),
                        "AssessmentFragment");
                break;
            case "ki_tuc_xa":
                startActivity(new Intent(requireContext(),
                        com.utc2.appreborn.ui.dormitory.DormitoryActivity.class));
                break;
            case "ho_tro":
                startActivity(new Intent(requireContext(),
                        com.utc2.appreborn.ui.profile.SupportActivity.class));
                break;
            case "lich_hoc":
                // Điều hướng về tab Lịch học trên bottom bar
                // Gọi thẳng switchTab nếu cần, hoặc dùng bottom bar selection
                main.pushFragment(
                        new com.utc2.appreborn.ui.schedule.ScheduleFragment(),
                        "ScheduleFragment");
                break;
            case "ket_qua":
                main.pushFragment(
                        new com.utc2.appreborn.ui.results.AcademicResultsFragment(),
                        "AcademicResultsFragment");
                break;
            default:
                break;
        }
    }

    // ═══════════════════════════════════════════════════════════
    //  Keyboard helpers
    // ═══════════════════════════════════════════════════════════

    private void focusSearchInput() {
        binding.etSearch.requestFocus();
        InputMethodManager imm = (InputMethodManager)
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            binding.etSearch.postDelayed(() ->
                    imm.showSoftInput(binding.etSearch, InputMethodManager.SHOW_IMPLICIT), 100);
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && binding != null) {
            imm.hideSoftInputFromWindow(binding.etSearch.getWindowToken(), 0);
        }
    }
}