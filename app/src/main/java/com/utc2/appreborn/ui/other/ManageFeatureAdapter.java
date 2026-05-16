package com.utc2.appreborn.ui.other;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.utc2.appreborn.databinding.ItemManageFeatureBinding;
import com.utc2.appreborn.ui.home.model.FeatureItem;

import java.util.List;

public class ManageFeatureAdapter extends RecyclerView.Adapter<ManageFeatureAdapter.VH> {

    public interface OnItemClickListener { void onClick(FeatureItem item); }

    public enum Mode { FAVORITES, AVAILABLE }

    private final List<FeatureItem>   items;
    private final Mode                mode;
    private final OnItemClickListener onBadgeClick;

    public ManageFeatureAdapter(List<FeatureItem> items,
                                Mode mode,
                                @Nullable OnItemClickListener onBadgeClick) {
        this.items        = items;
        this.mode         = mode;
        this.onBadgeClick = onBadgeClick;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(ItemManageFeatureBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        FeatureItem item = items.get(position);
        holder.b.ivIcon.setImageResource(item.getIconRes());
        holder.b.tvTitle.setText(item.getTitleRes());

        if (mode == Mode.FAVORITES) {
            // Hiện X, ẩn +, click X → callback
            holder.b.ivRemove.setVisibility(View.VISIBLE);
            holder.b.ivAdd.setVisibility(View.GONE);
            holder.b.cardFeature.setOnClickListener(null);
            holder.b.ivRemove.setOnClickListener(v -> {
                if (onBadgeClick != null) onBadgeClick.onClick(item);
            });
        } else {
            // Hiện +, ẩn X, click card hoặc + → callback
            holder.b.ivAdd.setVisibility(View.VISIBLE);
            holder.b.ivRemove.setVisibility(View.GONE);
            View.OnClickListener addClick = v -> {
                if (onBadgeClick != null) onBadgeClick.onClick(item);
            };
            holder.b.cardFeature.setOnClickListener(addClick);
            holder.b.ivAdd.setOnClickListener(addClick);
        }
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        final ItemManageFeatureBinding b;
        VH(ItemManageFeatureBinding b) { super(b.getRoot()); this.b = b; }
    }
}