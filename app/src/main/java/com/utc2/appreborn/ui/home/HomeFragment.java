package com.utc2.appreborn.ui.home;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.appbar.AppBarLayout;
import com.utc2.appreborn.R;
import com.utc2.appreborn.data.local.StudentProfile;
import com.utc2.appreborn.databinding.FragmentHomeBinding;
import com.utc2.appreborn.ui.profile.InfoFragment;
import com.utc2.appreborn.ui.home.adapter.FeatureAdapter;
import com.utc2.appreborn.ui.home.adapter.NewsAdapter;
import com.utc2.appreborn.ui.home.model.NewsItem;
import com.utc2.appreborn.ui.main.MainActivity;
import com.utc2.appreborn.ui.news.NewsDetailActivity;
import com.utc2.appreborn.ui.dormitory.DormitoryActivity;
import com.utc2.appreborn.ui.profile.SupportActivity;
import com.utc2.appreborn.ui.public_services.PublicServiceFragment;
import com.utc2.appreborn.ui.tuition.TuitionFragment;
import com.utc2.appreborn.utils.MockHelper;
import com.utc2.appreborn.ui.assessment.AssessmentFragment;
import com.utc2.appreborn.ui.other.OtherFeaturesFragment;
/**
 * HomeFragment — FINAL FIXES
 * ──────────────────────────────────────────────────────────────
 * Fix 1 — Status bar overlap (Android 15 edge-to-edge):
 * Dùng WindowInsetsCompat để đọc chiều cao status bar thực,
 * rồi set lên View statusBarSpacer trong layout. Cách này hoạt
 * động đúng trên mọi thiết bị, mọi API level, kể cả SDK 36.
 * <p>
 * Fix 2 — Profile bar bị che khi scroll:
 * Toolbar đã có android:elevation="8dp" trong layout.
 * Thêm bUpdateToolbarIconTint() để đổi tint đúng lúc.
 * <p>
 * Fix 3 — Bỏ iv_add_btn:
 * Đã xóa khỏi layout và toàn bộ listener liên quan.
 * <p>
 * Package: com.utc2.appreborn.ui.home
 */
public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private static final String URL_ALL_NEWS = "https://utc2.edu.vn/sinh-vien/thong-bao";

    // Ngưỡng collapsed (0.0 = fully expanded, 1.0 = fully collapsed)
    private static final float COLLAPSE_THRESHOLD = 0.85f;

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    private NewsAdapter newsAdapter;
    private FeatureAdapter featureAdapter;
    private boolean isToolbarCollapsed = false;

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    // ═══════════════════════════════════════════════════════════
    //  Lifecycle
    // ═══════════════════════════════════════════════════════════

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Thứ tự quan trọng: insets trước, rồi mới setup các phần còn lại
        applyStatusBarInset();
        setupFeatureGrid();
        setupNewsFeed();
        observeViewModel();
        setupClickListeners();
        setupToolbarScrollBehavior();

        viewModel.loadNews();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ═══════════════════════════════════════════════════════════
    //  FIX 1 — Status bar inset
    // ═══════════════════════════════════════════════════════════

    /**
     * Đọc chiều cao status bar thực từ WindowInsets và set lên
     * View statusBarSpacer (height ban đầu = 0dp trong XML).
     * <p>
     * Tại sao không dùng fitsSystemWindows="true":
     * Android 15 (API 35+) bắt buộc edge-to-edge cho app targetSdk ≥ 35.
     * Trong Fragment, fitsSystemWindows không được đảm bảo dispatch
     * đúng qua toàn bộ view hierarchy. Cách dùng WindowInsetsCompat
     * trực tiếp hoạt động đáng tin cậy trên mọi API level.
     * <p>
     * Kết quả:
     * statusBarSpacer = 24dp (hoặc bất kỳ giá trị thực của device)
     * CoordinatorLayout bắt đầu ngay dưới status bar ✓
     * Ảnh hero KHÔNG đè lên status bar ✓
     */
    private void applyStatusBarInset() {
        ViewCompat.setOnApplyWindowInsetsListener(
                binding.getRoot(),
                (v, windowInsets) -> {
                    Insets statusBars = windowInsets.getInsets(
                            WindowInsetsCompat.Type.statusBars());

                    // Set chiều cao statusBarSpacer = chiều cao status bar thực
                    ViewGroup.LayoutParams lp = binding.statusBarSpacer.getLayoutParams();
                    lp.height = statusBars.top;
                    binding.statusBarSpacer.setLayoutParams(lp);

                    // Trả về CONSUMED để các view con không xử lý lại insets này
                    return WindowInsetsCompat.CONSUMED;
                }
        );
    }

    // ═══════════════════════════════════════════════════════════
    //  FIX 2 — Toolbar scroll behavior (icon tint)
    // ═══════════════════════════════════════════════════════════

    /**
     * Theo dõi AppBarLayout offset để biết khi nào Toolbar collapsed.
     * <p>
     * Khi collapsed (nền #1E1E1E - màu tối):
     * Icon 3 nút đổi sang vàng #FFC107 — tương phản cao trên nền tối.
     * Khi expanded (nền là ảnh):
     * Icon giữ nguyên trắng — dễ đọc trên ảnh + gradient overlay.
     */
    private void setupToolbarScrollBehavior() {
        binding.appBarLayout.addOnOffsetChangedListener(
                (appBarLayout, verticalOffset) -> {
                    int total = appBarLayout.getTotalScrollRange();
                    if (total == 0) return;

                    float collapseRatio = Math.abs(verticalOffset) / (float) total;
                    boolean collapsed = collapseRatio >= COLLAPSE_THRESHOLD;

                    if (collapsed != isToolbarCollapsed) {
                        isToolbarCollapsed = collapsed;
                        applyToolbarIconTint(collapsed);
                    }
                });
    }

    private void applyToolbarIconTint(boolean collapsed) {
        // Expanded → trắng | Collapsed → vàng
        int color = collapsed
                ? requireContext().getColor(R.color.accent_yellow)
                : Color.WHITE;
        ColorStateList tintList = ColorStateList.valueOf(color);

        binding.btnQr.setImageTintList(tintList);
        binding.btnSearch.setImageTintList(tintList);
        binding.btnNotification.setImageTintList(tintList);
        binding.tvUsername.setTextColor(color);
    }

    // ═══════════════════════════════════════════════════════════
    //  Setup RecyclerViews
    // ═══════════════════════════════════════════════════════════

    private void setupFeatureGrid() {
        featureAdapter = new FeatureAdapter(
                viewModel.getFeatureList(), this::handleFeatureClick);
        binding.rvFeatures.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        binding.rvFeatures.setNestedScrollingEnabled(false);
        binding.rvFeatures.setHasFixedSize(true);
        binding.rvFeatures.setAdapter(featureAdapter);
    }

    private void setupNewsFeed() {
        newsAdapter = new NewsAdapter(this::handleNewsClick);
        binding.rvNews.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvNews.setNestedScrollingEnabled(false);
        binding.rvNews.setAdapter(newsAdapter);
    }

    // ═══════════════════════════════════════════════════════════
    //  Observers
    // ═══════════════════════════════════════════════════════════

    private void observeViewModel() {
        viewModel.getStudentProfileLiveData()
                .observe(getViewLifecycleOwner(), profile -> {
                    if (profile != null) binding.tvUsername.setText(profile.getFullName());
                });

        viewModel.getNewsLiveData().observe(getViewLifecycleOwner(), list -> {
            if (list != null) newsAdapter.submitList(list);
        });

        viewModel.getIsLoadingLiveData().observe(getViewLifecycleOwner(),
                loading -> Log.d(TAG, "Loading: " + loading));
    }

    // ═══════════════════════════════════════════════════════════
    //  Click listeners
    // ═══════════════════════════════════════════════════════════

    private void setupClickListeners() {
        // Avatar → trang cá nhân
        binding.ivAvatar.setOnClickListener(v -> {
            // Ép kiểu context về MainActivity để gọi hàm chuyển Fragment
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).pushFragment(new InfoFragment(), "InfoFragment");
            }
        });

        // FIX 3: KHÔNG còn binding.ivAddBtn — đã xóa khỏi layout

        // 3 nút icon toolbar
        binding.btnQr.setOnClickListener(v -> openQrFragment());
        binding.btnSearch.setOnClickListener(v -> handleSearchClick());
        binding.btnNotification.setOnClickListener(v -> handleNotificationClick());

        // Xem thêm trên web
        binding.btnViewAllNews.setOnClickListener(v -> openAllNewsInBrowser());
    }

    // ─── Feature clicks ───────────────────────────────────────

    private void handleFeatureClick(String featureId) {
        // Lấy instance của MainActivity để gọi hàm pushFragment
        MainActivity mainActivity = (MainActivity) requireActivity();

        switch (featureId) {
            case "hoc_phi":
                mainActivity.pushFragment(new TuitionFragment(), "TuitionFragment");
                break;

            case "dich_vu_cong":
                mainActivity.pushFragment(new PublicServiceFragment(), "PublicServiceFragment");
                break;

            case "ho_tro":
                // SupportActivity là ACTIVITY (tên class kết thúc bằng Activity)
                // Nên chỗ này dùng Intent là ĐÚNG, không được sửa thành pushFragment
                Intent intent = new Intent(requireContext(), SupportActivity.class);
                startActivity(intent);
                break;

            case "danh_gia":
                mainActivity.pushFragment(
                        new AssessmentFragment(),
                        "AssessmentFragment"
                );
                break;
            case "ki_tuc_xa":
                startActivity(new Intent(requireContext(), DormitoryActivity.class));
                break;
            case "danh_muc_khac":
                mainActivity.pushFragment(new OtherFeaturesFragment(), OtherFeaturesFragment.TAG);
                break;
            default:
                Log.w(TAG, "Unknown feature: " + featureId);
        }
    }

    // ─── News click ───────────────────────────────────────────

    private void handleNewsClick(NewsItem item) {
        Intent intent = new Intent(requireContext(), NewsDetailActivity.class);
        intent.putExtra(NewsDetailActivity.EXTRA_TITLE, item.getTitle());
        intent.putExtra(NewsDetailActivity.EXTRA_DATE, item.getDate());
        intent.putExtra(NewsDetailActivity.EXTRA_CONTENT, item.getContent());
        startActivity(intent);
    }

    // ─── 3 toolbar button handlers ────────────────────────────

    private void handleSearchClick() {
        ((MainActivity) requireActivity())
                .pushFragment(new com.utc2.appreborn.ui.search.SearchFragment(),
                        com.utc2.appreborn.ui.search.SearchFragment.TAG);
    }

    private void handleNotificationClick() {
        ((MainActivity) requireActivity())
                .pushFragment(new com.utc2.appreborn.ui.notification.NotificationFragment(),
                        com.utc2.appreborn.ui.notification.NotificationFragment.TAG);
    }

    // ═══════════════════════════════════════════════════════════
    //  Navigation
    // ═══════════════════════════════════════════════════════════

    private void openQrFragment() {
        StudentProfile p = viewModel.getStudentProfileLiveData().getValue();
        String name = (p != null) ? p.getFullName() : MockHelper.getMockFullName();
        String code = (p != null) ? p.getStudentCode() : MockHelper.getMockStudentCode();
        ((MainActivity) requireActivity())
                .pushFragment(QrFragment.newInstance(name, code), QrFragment.TAG);
    }

    private void openAllNewsInBrowser() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL_ALL_NEWS));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(requireContext(),
                    "Không tìm thấy trình duyệt", Toast.LENGTH_SHORT).show();
        }
    }
}