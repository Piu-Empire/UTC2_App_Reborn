package com.utc2.appreborn.ui.other;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.utc2.appreborn.R;
import com.utc2.appreborn.databinding.FragmentManageFeaturesBinding;
import com.utc2.appreborn.ui.home.model.FeatureItem;
import com.utc2.appreborn.ui.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class ManageFeaturesFragment extends Fragment {

    public static final String TAG = "ManageFeaturesFragment";

    private FragmentManageFeaturesBinding binding;
    private List<FeatureItem> activeFavorites;
    private List<FeatureItem> availableFeatures;
    private ManageFeatureAdapter activeAdapter;
    private ManageFeatureAdapter availableAdapter;

    public interface OnFavoritesChangedListener {
        void onFavoritesChanged(List<FeatureItem> newFavorites);
    }

    // Static để không bị mất khi Fragment recreate
    private static OnFavoritesChangedListener pendingListener;

    public static ManageFeaturesFragment newInstance(
            List<FeatureItem> active,
            List<FeatureItem> available,
            OnFavoritesChangedListener listener) {
        ManageFeaturesFragment f = new ManageFeaturesFragment();
        f.activeFavorites   = new ArrayList<>(active);
        f.availableFeatures = new ArrayList<>(available);
        pendingListener     = listener;  // lưu static để không bị mất
        return f;
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentManageFeaturesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (activeFavorites == null)   activeFavorites   = new ArrayList<>();
        if (availableFeatures == null) availableFeatures = new ArrayList<>();

        setupAdapters();

        // X ở header → đóng KHÔNG lưu
        binding.btnClose.setOnClickListener(v -> popBack());

        // Nút Lưu → callback + đóng
        binding.btnSave.setOnClickListener(v -> {
            if (pendingListener != null) {
                pendingListener.onFavoritesChanged(new ArrayList<>(activeFavorites));
                pendingListener = null;
            }
            Toast.makeText(requireContext(), "Đã lưu mục thường dùng", Toast.LENGTH_SHORT).show();
            popBack();
        });
    }

    private void setupAdapters() {
        activeAdapter = new ManageFeatureAdapter(
                activeFavorites,
                ManageFeatureAdapter.Mode.FAVORITES,
                item -> {
                    activeFavorites.remove(item);
                    availableFeatures.add(item);
                    activeAdapter.notifyDataSetChanged();
                    availableAdapter.notifyDataSetChanged();
                });
        binding.rvActiveFavorites.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        binding.rvActiveFavorites.setAdapter(activeAdapter);
        binding.rvActiveFavorites.setNestedScrollingEnabled(false);

        availableAdapter = new ManageFeatureAdapter(
                availableFeatures,
                ManageFeatureAdapter.Mode.AVAILABLE,
                item -> {
                    availableFeatures.remove(item);
                    activeFavorites.add(item);
                    activeAdapter.notifyDataSetChanged();
                    availableAdapter.notifyDataSetChanged();
                });
        binding.rvAvailableFeatures.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        binding.rvAvailableFeatures.setAdapter(availableAdapter);
        binding.rvAvailableFeatures.setNestedScrollingEnabled(false);
    }

    private void popBack() {
        if (getActivity() == null) return;
        androidx.fragment.app.FragmentManager fm = getActivity().getSupportFragmentManager();
        // Show OtherFeaturesFragment lại (nó bị hide khi mở màn này)
        androidx.fragment.app.Fragment other = fm.findFragmentByTag(
                com.utc2.appreborn.ui.other.OtherFeaturesFragment.TAG);
        androidx.fragment.app.FragmentTransaction tx = fm.beginTransaction();
        if (other != null && other.isHidden()) tx.show(other);
        tx.commit();
        fm.popBackStack();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}