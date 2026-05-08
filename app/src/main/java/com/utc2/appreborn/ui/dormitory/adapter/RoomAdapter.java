package com.utc2.appreborn.ui.dormitory.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.dormitory.model.Room;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter cho RecyclerView hiển thị danh sách phòng KTX.
 *
 * [Chương 3 - OOP]
 *  - Bao đóng: private fields + interface callback
 *  - Overload: 2 constructor (có/không có listener)
 *  - Inner class ViewHolder (giống pattern bên HomeFragment)
 *
 * Dùng getDisplayInfo() – method được override từ DormitoryItem.
 */
public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {

    // ── Interface callback (giống FeatureAdapter bên Home) ────────────────────
    public interface OnRoomClickListener {
        void onRegisterClick(Room room);
    }

    // ── Bao đóng ──────────────────────────────────────────────────────────────
    private final List<Room>          items;
    private final OnRoomClickListener listener;

    // ── Overload constructor 1: có listener ──────────────────────────────────
    public RoomAdapter(List<Room> items, OnRoomClickListener listener) {
        this.items    = new ArrayList<>(items);
        this.listener = listener;
    }

    // ── Overload constructor 2: không có listener (chỉ xem) ──────────────────
    public RoomAdapter(List<Room> items) {
        this(items, null);
    }

    /** Cập nhật danh sách khi lọc hoặc tìm kiếm. */
    public void updateData(List<Room> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    // ── RecyclerView ──────────────────────────────────────────────────────────

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_room, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Room room = items.get(position);

        holder.txtTitle.setText(room.getName());

        // Gọi getDisplayInfo() – override từ DormitoryItem
        holder.txtInfo.setText(room.getDisplayInfo());

        if (room.isAvailable()) {
            holder.badgeLayout.setBackgroundResource(R.drawable.bg_badge_available);
            holder.imgStatusIcon.setImageResource(R.drawable.ic_status_check);
            holder.tvStatusText.setText("Còn chỗ");
            holder.tvStatusText.setTextColor(android.graphics.Color.parseColor("#00C853"));

            // Nút Đăng ký: đen, chữ trắng, clickable
            holder.btnRegister.setBackgroundResource(R.drawable.bg_button_black);
            holder.btnRegister.setTextColor(android.graphics.Color.WHITE);
            holder.btnRegister.setAlpha(1.0f);
            holder.btnRegister.setClickable(true);
            holder.btnRegister.setFocusable(true);
        } else {
            holder.badgeLayout.setBackgroundResource(R.drawable.bg_badge_unavailable);
            holder.imgStatusIcon.setImageResource(R.drawable.ic_status_x);
            holder.tvStatusText.setText("Hết chỗ");
            holder.tvStatusText.setTextColor(android.graphics.Color.parseColor("#FF3B30"));

            // Nút Đăng ký: disabled – xám mờ, không click được
            holder.btnRegister.setBackgroundResource(R.drawable.bg_button_disabled);
            holder.btnRegister.setAlpha(0.5f);
            holder.btnRegister.setClickable(false);
            holder.btnRegister.setFocusable(false);
        }

        holder.btnRegister.setOnClickListener(v -> {
            if (listener != null) listener.onRegisterClick(room);
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    // ── Inner ViewHolder ──────────────────────────────────────────────────────
    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView     txtTitle;
        final TextView     txtInfo;
        final LinearLayout badgeLayout;
        final ImageView    imgStatusIcon;
        final TextView     tvStatusText;
        final TextView     btnRegister;   // TextView thay Button – tránh Material override màu

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle      = itemView.findViewById(R.id.txtTitle);
            txtInfo       = itemView.findViewById(R.id.txtInfo);
            badgeLayout   = itemView.findViewById(R.id.txtStatus);
            imgStatusIcon = itemView.findViewById(R.id.imgStatusIcon);
            tvStatusText  = itemView.findViewById(R.id.tvStatusText);
            btnRegister   = itemView.findViewById(R.id.btnRegister);  // TextView
        }
    }
}