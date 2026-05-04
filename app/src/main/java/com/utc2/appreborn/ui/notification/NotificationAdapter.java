package com.utc2.appreborn.ui.notification;

import android.graphics.Color;
import android.graphics.Typeface; // Thêm import này
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.utc2.appreborn.R;

public class NotificationAdapter
        extends ListAdapter<NotificationItem, NotificationAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(NotificationItem item);
    }

    private static final int[] AVATAR_COLORS = {
            0xFF1565C0, 0xFF2E7D32, 0xFF6A1B9A, 0xFFAD1457,
            0xFF00838F, 0xFFE65100, 0xFF4E342E, 0xFF37474F,
    };

    private final OnItemClickListener listener;

    public NotificationAdapter(OnItemClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<NotificationItem> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<NotificationItem>() {
                @Override
                public boolean areItemsTheSame(@NonNull NotificationItem a,
                                               @NonNull NotificationItem b) {
                    // Tốt nhất nên có ID duy nhất từ Email API
                    return a.getSubject().equals(b.getSubject()) && a.getTimeLabel().equals(b.getTimeLabel());
                }

                @Override
                public boolean areContentsTheSame(@NonNull NotificationItem a,
                                                  @NonNull NotificationItem b) {
                    return a.isRead() == b.isRead()
                            && a.getSenderName().equals(b.getSenderName())
                            && a.getSubject().equals(b.getSubject());
                }
            };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvAvatarLetter, tvSender, tvSubject, tvPreview, tvTime;
        private final View dotUnread;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAvatarLetter = itemView.findViewById(R.id.tv_avatar_letter);
            tvSender = itemView.findViewById(R.id.tv_sender);
            tvSubject = itemView.findViewById(R.id.tv_subject);
            tvPreview = itemView.findViewById(R.id.tv_preview);
            tvTime = itemView.findViewById(R.id.tv_time);
            dotUnread = itemView.findViewById(R.id.dot_unread);
        }

        void bind(NotificationItem item, OnItemClickListener listener) {
            tvAvatarLetter.setText(item.getAvatarLetter());
            tvSender.setText(item.getSenderName());
            tvSubject.setText(item.getSubject());
            tvPreview.setText(item.getPreview());
            tvTime.setText(item.getTimeLabel());

            // Đổi màu avatar (thêm mutate để không bị đổi màu dây chuyền)
            int colorIndex = Math.abs(item.getSenderName().hashCode()) % AVATAR_COLORS.length;
            if (tvAvatarLetter.getBackground() != null) {
                tvAvatarLetter.getBackground().mutate().setTint(AVATAR_COLORS[colorIndex]);
            }

            // Fix lỗi setTextStyle -> setTypeface
            tvSubject.setTypeface(null, item.isRead() ? Typeface.NORMAL : Typeface.BOLD);
            dotUnread.setVisibility(item.isRead() ? View.GONE : View.VISIBLE);

            itemView.setBackgroundColor(item.isRead() ? Color.WHITE : 0xFFF3F9FF);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    // Lưu ý: Việc setRead(true) ở đây chỉ thay đổi object tạm thời.
                    // Nên để Fragment gọi notifyItemChanged hoặc cập nhật từ Database/API.
                    item.setRead(true);
                    bind(item, listener);
                    listener.onItemClick(item);
                }
            });
        }
    }
}