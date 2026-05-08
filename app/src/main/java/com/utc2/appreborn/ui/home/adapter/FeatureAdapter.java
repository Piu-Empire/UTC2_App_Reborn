package com.utc2.appreborn.ui.home.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.utc2.appreborn.databinding.ItemFeatureCardBinding;
import com.utc2.appreborn.ui.home.model.FeatureItem;

import java.util.List;

/**
 * FeatureAdapter
 * ──────────────────────────────────────────────────────────────
 * RecyclerView adapter for the 3×2 feature grid on the
 * Home screen.
 *
 * Uses View Binding (ItemFeatureCardBinding).
 *
 * Package: com.utc2.appreborn.ui.home.adapter
 */
public class FeatureAdapter extends RecyclerView.Adapter<FeatureAdapter.ViewHolder> {

    // ── Listener interface ────────────────────────────────────
    public interface OnFeatureClickListener {
        void onFeatureClick(String featureId);
    }

    // ── State ─────────────────────────────────────────────────
    private final List<FeatureItem>      items;
    private final OnFeatureClickListener listener;

    // ── Constructor ───────────────────────────────────────────
    public FeatureAdapter(@NonNull List<FeatureItem> items,
                          @NonNull OnFeatureClickListener listener) {
        this.items    = items;
        this.listener = listener;
    }

    // ═══════════════════════════════════════════════════════════
    //  RecyclerView.Adapter overrides
    // ═══════════════════════════════════════════════════════════

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFeatureCardBinding itemBinding = ItemFeatureCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() { return items.size(); }

    // ═══════════════════════════════════════════════════════════
    //  ViewHolder
    // ═══════════════════════════════════════════════════════════

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemFeatureCardBinding b;

        ViewHolder(@NonNull ItemFeatureCardBinding binding) {
            super(binding.getRoot());
            this.b = binding;
        }

        void bind(@NonNull FeatureItem item) {
            b.ivFeatureIcon.setImageResource(item.getIconRes());
            b.tvFeatureTitle.setText(item.getTitle());
            b.getRoot().setOnClickListener(v -> listener.onFeatureClick(item.getId()));
        }
    }
}