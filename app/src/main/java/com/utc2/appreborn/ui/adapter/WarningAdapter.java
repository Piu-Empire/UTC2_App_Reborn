package com.utc2.appreborn.ui.adapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.utc2.appreborn.R;
import com.utc2.appreborn.model.AcademicWarning;

import java.util.List;

/**
 * WarningAdapter
 *
 * - ACTIVE   (Chưa giải quyết): nền đỏ nhạt + stroke đỏ nhạt
 * - RESOLVED (Đã giải quyết)  : nền xanh nhạt + stroke xanh nhạt
 */
public class WarningAdapter extends RecyclerView.Adapter<WarningAdapter.WarningViewHolder> {

    private final List<AcademicWarning> warningList;

    public WarningAdapter(List<AcademicWarning> warningList) {
        this.warningList = warningList;
    }

    public void updateList(List<AcademicWarning> newList) {
        warningList.clear();
        warningList.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WarningViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_warning, parent, false);
        return new WarningViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WarningViewHolder holder, int position) {
        holder.bind(warningList.get(position));
    }

    @Override
    public int getItemCount() {
        return warningList.size();
    }

    static class WarningViewHolder extends RecyclerView.ViewHolder {

        private final MaterialCardView cardRoot;
        private final TextView   tvTitle;
        private final TextView   tvSubTitle;
        private final TextView   tvDate;
        private final ImageView  ivSeriousBadge;

        WarningViewHolder(@NonNull View itemView) {
            super(itemView);
            cardRoot       = itemView.findViewById(R.id.card_warning_item);
            tvTitle        = itemView.findViewById(R.id.tv_warning_title);
            tvSubTitle     = itemView.findViewById(R.id.tv_warning_subtitle);
            tvDate         = itemView.findViewById(R.id.tv_warning_date);
            ivSeriousBadge = itemView.findViewById(R.id.iv_serious_badge);
        }

        void bind(AcademicWarning warning) {
            tvTitle.setText(warning.getTitle());
            tvDate.setText(warning.getDate());

            if (warning.getSubTitle() != null && !warning.getSubTitle().isEmpty()) {
                tvSubTitle.setText(warning.getSubTitle());
                tvSubTitle.setVisibility(View.VISIBLE);
            } else {
                tvSubTitle.setVisibility(View.GONE);
            }

            if (warning.isActive()) {
                // Chưa giải quyết: nền đỏ nhạt
                cardRoot.setCardBackgroundColor(Color.parseColor("#FFEBEE"));
                cardRoot.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#FFCDD2")));
                cardRoot.setStrokeWidth(dpToPx(1.5f));
            } else {
                // Đã giải quyết: nền xanh nhạt
                cardRoot.setCardBackgroundColor(Color.parseColor("#E8F5E9"));
                cardRoot.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#C8E6C9")));
                cardRoot.setStrokeWidth(dpToPx(1f));
            }

            // Badge ẩn đi — không còn dùng
            if (ivSeriousBadge != null) ivSeriousBadge.setVisibility(View.GONE);
        }

        private int dpToPx(float dp) {
            float density = itemView.getContext()
                    .getResources().getDisplayMetrics().density;
            return Math.round(dp * density);
        }
    }
}