package com.utc2.appreborn.ui.other;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.utc2.appreborn.databinding.ItemFeatureCardBinding;
import com.utc2.appreborn.ui.home.model.FeatureItem;

import java.util.List;

/**
 * OtherFeatureAdapter
 * Tái sử dụng item_feature_card.xml (giống FeatureAdapter ở Home).
 * Dùng cho cả rvFavorites và rvOthers trong OtherFeaturesFragment.
 */
public class OtherFeatureAdapter extends RecyclerView.Adapter<OtherFeatureAdapter.VH> {

    public interface OnClickListener { void onClick(String featureId); }

    private List<FeatureItem> items;
    private final OnClickListener listener;

    public OtherFeatureAdapter(List<FeatureItem> items, OnClickListener listener) {
        this.items    = items;
        this.listener = listener;
    }

    public void updateItems(List<FeatureItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(ItemFeatureCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        FeatureItem item = items.get(position);
        holder.b.ivFeatureIcon.setImageResource(item.getIconRes());
        holder.b.tvFeatureTitle.setText(item.getTitleRes());
        holder.b.getRoot().setOnClickListener(v -> listener.onClick(item.getId()));
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        final ItemFeatureCardBinding b;
        VH(ItemFeatureCardBinding b) { super(b.getRoot()); this.b = b; }
    }
}
