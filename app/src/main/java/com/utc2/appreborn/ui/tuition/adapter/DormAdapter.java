package com.utc2.appreborn.ui.tuition.adapter;

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

/**
 * Adapter hiển thị danh sách tiền KTX cho sinh viên UTC2.
 * Kế thừa dữ liệu từ lớp cha Tuition để đảm bảo tính nhất quán.
 */
public class DormAdapter extends RecyclerView.Adapter<DormAdapter.ViewHolder> {
    private final List<DormTuition> dormList;

    public DormAdapter(List<DormTuition> dormList) {
        this.dormList = dormList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout item_dorm_tuition mà bạn đã thiết kế
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dorm_tuition, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DormTuition item = dormList.get(position);

        // Hiển thị Tên phòng (Lấy từ trường 'name' của lớp cha Tuition)
        if (holder.tvRoomName != null) {
            holder.tvRoomName.setText(item.getName());
        }

        // Hiển thị chi tiết (Lấy từ trường 'details' của lớp cha Tuition - VD: "Tiền điện tháng 10")
        if (holder.tvDormDetails != null) {
            holder.tvDormDetails.setText(item.getDetails());
        }

        // Hiển thị số tiền (Định dạng kiểu 1,000,000 VND)
        if (holder.tvDormAmount != null) {
            String formattedAmount = String.format(Locale.getDefault(), "%,d VND", item.getAmount());
            holder.tvDormAmount.setText(formattedAmount);
        }
    }

    @Override
    public int getItemCount() {
        return dormList != null ? dormList.size() : 0;
    }

    /**
     * ViewHolder tối ưu hóa việc tìm View (findViewById)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoomName, tvDormDetails, tvDormAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Đảm bảo ID trong file item_dorm_tuition.xml trùng khớp với các ID dưới đây:
            tvRoomName = itemView.findViewById(R.id.tvRoomName);
            tvDormDetails = itemView.findViewById(R.id.tvDormDetails);
            tvDormAmount = itemView.findViewById(R.id.tvDormAmount);
        }
    }
}