package com.utc2.appreborn.ui.dormitory.lookup.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.dormitory.lookup.model.RoomOccupant;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter hiển thị danh sách sinh viên đang ở trong phòng.
 *
 * [Chương 3 - OOP]
 *  - Bao đóng: private fields
 *  - Inner class ViewHolder
 *
 * [Chương 5 - Collection]
 *  - List<RoomOccupant>: dữ liệu hiển thị
 */
public class OccupantAdapter extends RecyclerView.Adapter<OccupantAdapter.ViewHolder> {

    private final List<RoomOccupant> items;

    public OccupantAdapter(List<RoomOccupant> items) {
        this.items = new ArrayList<>(items);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_occupant, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RoomOccupant o = items.get(position);
        holder.tvName.setText(o.getName());
        holder.tvMssv.setText("MSSV: " + o.getMssv());
        holder.tvClass.setText("Lớp: " + o.getClassId());
        // Dùng ic_user có sẵn trong drawable làm avatar mặc định
        holder.imgAvatar.setImageResource(R.drawable.ic_user);
    }

    @Override
    public int getItemCount() { return items.size(); }

    // ── Inner ViewHolder ──────────────────────────────────────────────────────
    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView imgAvatar;
        final TextView  tvName;
        final TextView  tvMssv;
        final TextView  tvClass;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            tvName    = itemView.findViewById(R.id.tvOccupantName);
            tvMssv    = itemView.findViewById(R.id.tvOccupantMssv);
            tvClass   = itemView.findViewById(R.id.tvOccupantClass);
        }
    }
}