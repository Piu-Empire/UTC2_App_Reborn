package com.utc2.appreborn.ui.other;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.utc2.appreborn.R;
import com.utc2.appreborn.databinding.FragmentOtherFeaturesBinding;
import com.utc2.appreborn.ui.assessment.AssessmentFragment;
import com.utc2.appreborn.ui.dormitory.DormitoryActivity;
import com.utc2.appreborn.ui.home.QrFragment;
import com.utc2.appreborn.ui.home.model.FeatureItem;
import com.utc2.appreborn.ui.main.MainActivity;
import com.utc2.appreborn.ui.notification.NotificationFragment;
import com.utc2.appreborn.ui.profile.ProfileFragment;
import com.utc2.appreborn.ui.profile.SupportActivity;
import com.utc2.appreborn.ui.public_services.PublicServiceFragment;
import com.utc2.appreborn.ui.results.AcademicResultsFragment;
import com.utc2.appreborn.ui.schedule.ScheduleFragment;
import com.utc2.appreborn.ui.search.SearchFragment;
import com.utc2.appreborn.ui.tuition.TuitionFragment;
import com.utc2.appreborn.ui.courseregistration.CourseRegistrationActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * OtherFeaturesFragment
 * ──────────────────────────────────────────────────────────────
 * Màn hình "Danh mục khác":
 *   1. Thanh tìm kiếm → mở SearchFragment
 *   2. Mục thường dùng (favorites): 5 item + 1 nút "Thêm"
 *   3. Mục khác: các feature không có trong favorites
 */
public class OtherFeaturesFragment extends Fragment {

    public static final String TAG = "OtherFeaturesFragment";

    private FragmentOtherFeaturesBinding binding;

    // Toàn bộ danh sách feature của màn này
    private List<FeatureItem> allFeatures;

    // 5 feature mặc định trong mục thường dùng
    private List<FeatureItem> favorites = new ArrayList<>();

    private OtherFeatureAdapter favoritesAdapter;
    private OtherFeatureAdapter othersAdapter;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentOtherFeaturesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buildAllFeatures();
        setupDefaultFavorites();
        setupRecyclerViews();
        setupClickListeners();
    }

    // ── Build toàn bộ feature list ────────────────────────────

    private void buildAllFeatures() {
        allFeatures = new ArrayList<>(Arrays.asList(
                new FeatureItem("hoc_phi",         R.drawable.ic_hoc_phi,        R.string.feature_hoc_phi),
                new FeatureItem("dich_vu_cong",    R.drawable.ic_dich_vu_cong,   R.string.feature_dich_vu_cong),
                new FeatureItem("danh_gia",        R.drawable.ic_danh_gia,       R.string.feature_danh_gia),
                new FeatureItem("ki_tuc_xa",       R.drawable.ic_ki_tuc_xa,      R.string.feature_ki_tuc_xa),
                new FeatureItem("ho_tro",          R.drawable.ic_ho_tro,         R.string.feature_ho_tro),
                new FeatureItem("dang_ki_hoc",     R.drawable.ic_book_copy,      R.string.feature_dang_ki_hoc),
                new FeatureItem("lich",            R.drawable.ic_calendar,       R.string.feature_lich),
                new FeatureItem("ket_qua",         R.drawable.ic_chart_column,   R.string.feature_ket_qua),
                new FeatureItem("ca_nhan",         R.drawable.ic_user,           R.string.feature_ca_nhan),
                new FeatureItem("qr",              R.drawable.ic_qr_code,        R.string.feature_qr),
                new FeatureItem("thong_bao",       R.drawable.ic_notification,   R.string.feature_thong_bao),
                new FeatureItem("tim_kiem",        R.drawable.ic_search,         R.string.feature_tim_kiem)
        ));
    }

    /** 5 mục mặc định: hoc_phi, dich_vu_cong, danh_gia, ki_tuc_xa, ho_tro */
    private void setupDefaultFavorites() {
        favorites.clear();
        for (FeatureItem item : allFeatures) {
            switch (item.getId()) {
                case "hoc_phi":
                case "dich_vu_cong":
                case "danh_gia":
                case "ki_tuc_xa":
                case "ho_tro":
                    favorites.add(item);
                    break;
            }
        }
    }

    // ── RecyclerViews ─────────────────────────────────────────

    private void setupRecyclerViews() {
        // Favorites (5 + nút Thêm = 6 ô)
        favoritesAdapter = new OtherFeatureAdapter(
                getFavoritesWithAdd(), this::handleFeatureClick);
        binding.rvFavorites.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        binding.rvFavorites.setNestedScrollingEnabled(false);
        binding.rvFavorites.setAdapter(favoritesAdapter);

        // Others
        othersAdapter = new OtherFeatureAdapter(
                getOtherFeatures(), this::handleFeatureClick);
        binding.rvOthers.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        binding.rvOthers.setNestedScrollingEnabled(false);
        binding.rvOthers.setAdapter(othersAdapter);
    }

    /** Trả về list favorites + 1 item giả "add" ở cuối */
    private List<FeatureItem> getFavoritesWithAdd() {
        List<FeatureItem> list = new ArrayList<>(favorites);
        list.add(new FeatureItem("__add__", R.drawable.ic_plus, R.string.action_add));
        return list;
    }

    /** Trả về các feature KHÔNG có trong favorites */
    private List<FeatureItem> getOtherFeatures() {
        List<String> favoriteIds = new ArrayList<>();
        for (FeatureItem f : favorites) favoriteIds.add(f.getId());
        List<FeatureItem> others = new ArrayList<>();
        for (FeatureItem item : allFeatures) {
            if (!favoriteIds.contains(item.getId())) others.add(item);
        }
        return others;
    }

    private void refreshBothSections() {
        favoritesAdapter.updateItems(getFavoritesWithAdd());
        othersAdapter.updateItems(getOtherFeatures());
    }

    // ── Click listeners ───────────────────────────────────────

    private void setupClickListeners() {
        binding.btnBack.setOnClickListener(v -> {
            if (getActivity() != null)
                getActivity().getSupportFragmentManager().popBackStack();
        });

        binding.layoutSearch.setOnClickListener(v -> navigateTo(new SearchFragment(), SearchFragment.TAG));
    }

    private void handleFeatureClick(String featureId) {
        if ("__add__".equals(featureId)) {
            openManageScreen();
            return;
        }
        MainActivity main = getMainActivity();
        if (main == null) return;

        switch (featureId) {
            case "hoc_phi":
                main.pushFragment(new TuitionFragment(), "TuitionFragment"); break;
            case "dich_vu_cong":
                main.pushFragment(new PublicServiceFragment(), "PublicServiceFragment"); break;
            case "ho_tro":
                startActivity(new Intent(requireContext(), SupportActivity.class)); break;
            case "danh_gia":
                main.pushFragment(new AssessmentFragment(), "AssessmentFragment"); break;
            case "ki_tuc_xa":
                startActivity(new Intent(requireContext(), DormitoryActivity.class)); break;
            case "dang_ki_hoc":
                startActivity(new Intent(requireContext(), CourseRegistrationActivity.class)); break;
            case "lich":
                main.pushFragment(new ScheduleFragment(), "ScheduleFragment"); break;
            case "ket_qua":
                main.pushFragment(new AcademicResultsFragment(), "AcademicResultsFragment"); break;
            case "ca_nhan":
                main.pushFragment(new ProfileFragment(), "ProfileFragment"); break;
            case "qr":
                main.pushFragment(QrFragment.newInstance("", ""), QrFragment.TAG); break;
            case "thong_bao":
                main.pushFragment(new NotificationFragment(), NotificationFragment.TAG); break;
            case "tim_kiem":
                main.pushFragment(new SearchFragment(), SearchFragment.TAG); break;
        }
    }

    private void openManageScreen() {
        if (getActivity() == null) return;
        List<FeatureItem> available = getOtherFeatures();
        ManageFeaturesFragment manageFragment = ManageFeaturesFragment.newInstance(
                favorites, available,
                newFavorites -> {
                    // callback chạy trên instance này — không bị recreate vì dùng add/hide
                    favorites = new ArrayList<>(newFavorites);
                    refreshBothSections();
                });
        // Dùng add+hide để OtherFeaturesFragment KHÔNG bị destroy
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        android.R.anim.slide_in_left,
                        android.R.anim.fade_out,
                        android.R.anim.fade_in,
                        android.R.anim.slide_out_right)
                .hide(OtherFeaturesFragment.this)
                .add(com.utc2.appreborn.R.id.fragment_container,
                        manageFragment, ManageFeaturesFragment.TAG)
                .addToBackStack(ManageFeaturesFragment.TAG)
                .commit();
    }

    private void navigateTo(Fragment fragment, String tag) {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).pushFragment(fragment, tag);
        }
    }

    @Nullable
    private MainActivity getMainActivity() {
        return (getActivity() instanceof MainActivity) ? (MainActivity) getActivity() : null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}