package com.utc2.appreborn.ui.tuition.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.tuition.model.DormTuition;

import java.util.List;
import java.util.Locale;

public class DormAdapter extends RecyclerView.Adapter<DormAdapter.ViewHolder> {

    private final List<DormTuition> dormList;

    public DormAdapter(List<DormTuition> dormList) {
        this.dormList = dormList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dorm_tuition, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DormTuition item = dormList.get(position);

        // Tên phòng — roomCode từ DormRegistrationResponse
        if (holder.tvRoomName != null) {
            String roomDisplay = item.getName();
            if (item.getBuilding() != null && !item.getBuilding().isEmpty()) {
                roomDisplay = item.getBuilding() + " · " + roomDisplay;
            }
            holder.tvRoomName.setText(roomDisplay);
        }

        // Chi tiết: thời gian thuê
        if (holder.tvDormDetails != null) {
            String start = item.getStartDate();
            String end   = item.getEndDate();
            String detail = "";
            if (start != null && !start.isEmpty() && end != null && !end.isEmpty()) {
                detail = start + " → " + end;
            } else if (item.getDetails() != null) {
                detail = item.getDetails();
            }
            holder.tvDormDetails.setText(detail);
        }

        // ── LOGIC MỚI: chỉ hiện số tiền khi admin đã duyệt ──────────────────
        if (holder.tvDormAmount != null) {
            if (item.isApproved()) {
                // Đã duyệt → hiện số tiền cần thanh toán
                double amount = item.getRemainingAmount();
                holder.tvDormAmount.setText(
                        String.format(Locale.getDefault(), "%,.0f VND", amount));
                holder.tvDormAmount.setTextColor(Color.parseColor("#B71C1C")); // đỏ đậm
            } else if (item.isPendingReg()) {
                // Chờ duyệt → ẩn số tiền, hiện thông báo
                holder.tvDormAmount.setText("Chờ admin duyệt");
                holder.tvDormAmount.setTextColor(Color.parseColor("#E65100")); // cam
            } else {
                // Từ chối / trạng thái khác
                holder.tvDormAmount.setText("—");
                holder.tvDormAmount.setTextColor(Color.parseColor("#757575"));
            }
        }

        // Trạng thái đăng ký (regStatus) + trạng thái đóng tiền (paidStatus)
        if (holder.tvStatus != null) {
            String regStatus  = item.getRegStatus();
            String paidStatus = item.getDormPaidStatus();

            if (DormTuition.REG_PENDING.equals(regStatus)) {
                holder.tvStatus.setText("Chờ duyệt");
                holder.tvStatus.setTextColor(Color.parseColor("#E65100")); // cam
            } else if (DormTuition.REG_APPROVED.equals(regStatus)) {
                if (DormTuition.DORM_PAY_PAID.equals(paidStatus)) {
                    holder.tvStatus.setText("Đã đóng tiền");
                    holder.tvStatus.setTextColor(Color.parseColor("#2E7D32")); // xanh lá
                } else {
                    holder.tvStatus.setText("Cần thanh toán");
                    holder.tvStatus.setTextColor(Color.parseColor("#C62828")); // đỏ
                }
            } else if (DormTuition.REG_REJECTED.equals(regStatus)) {
                holder.tvStatus.setText("Đã từ chối");
                holder.tvStatus.setTextColor(Color.parseColor("#757575")); // xám
            } else {
                holder.tvStatus.setText(regStatus != null ? regStatus : "—");
                holder.tvStatus.setTextColor(Color.parseColor("#757575"));
            }
        }
    }

    @Override
    public int getItemCount() {
        return dormList != null ? dormList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoomName, tvDormDetails, tvDormAmount, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoomName    = itemView.findViewById(R.id.tvRoomName);
            tvDormDetails = itemView.findViewById(R.id.tvDormDetails);
            tvDormAmount  = itemView.findViewById(R.id.tvDormAmount);
            tvStatus      = itemView.findViewById(R.id.tvStatus);
        }
    }
}