package com.utc2.appreborn.ui.profile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.profile.model.FeedbackItem;

public class FeedbackHistoryAdapter
        extends ListAdapter<FeedbackItem, FeedbackHistoryAdapter.ViewHolder> {

    private static final int[] AVATAR_COLORS = {
            0xFF1565C0,  // "Lỗi"  → xanh dương
            0xFF2E7D32,  // "Góp ý" → xanh lá
            0xFF6A1B9A,
            0xFFAD1457,
    };

    public interface OnItemClickListener {
        void onItemClick(FeedbackItem item);
    }

    private final OnItemClickListener listener;

    public FeedbackHistoryAdapter(OnItemClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<FeedbackItem> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<FeedbackItem>() {
                @Override
                public boolean areItemsTheSame(@NonNull FeedbackItem a, @NonNull FeedbackItem b) {
                    return a.getId() == b.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull FeedbackItem a, @NonNull FeedbackItem b) {
                    return a.getStatus().equals(b.getStatus())
                            && a.getContent().equals(b.getContent());
                }
            };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_feedback_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvAvatarLetter;
        private final TextView tvType;
        private final TextView tvContent;
        private final TextView tvTime;
        private final TextView tvStatus;
        private final TextView tvAdminReply;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAvatarLetter = itemView.findViewById(R.id.tv_avatar_letter);
            tvType         = itemView.findViewById(R.id.tv_loai);
            tvContent      = itemView.findViewById(R.id.tv_content);
            tvTime         = itemView.findViewById(R.id.tv_time);
            tvStatus       = itemView.findViewById(R.id.tv_status);
            tvAdminReply   = itemView.findViewById(R.id.tv_admin_reply);
        }

        void bind(FeedbackItem item, OnItemClickListener listener) {
            tvAvatarLetter.setText(item.getAvatarLetter());
            tvType.setText(item.getType());
            tvContent.setText(item.getContent());
            tvTime.setText(item.getTimeLabel());

            // Badge trạng thái
            if (tvStatus != null) {
                tvStatus.setText(item.getStatus());
                tvStatus.setVisibility(View.VISIBLE);
            }

            // Phản hồi admin
            if (tvAdminReply != null) {
                if (item.hasAdminReply()) {
                    tvAdminReply.setText("Phản hồi: " + item.getAdminReply());
                    tvAdminReply.setVisibility(View.VISIBLE);
                } else {
                    tvAdminReply.setVisibility(View.GONE);
                }
            }

            // Màu avatar
            int colorIndex = Math.abs(item.getType().hashCode()) % AVATAR_COLORS.length;
            if (tvAvatarLetter.getBackground() != null) {
                tvAvatarLetter.getBackground().mutate().setTint(AVATAR_COLORS[colorIndex]);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(item);
            });
        }
    }
}