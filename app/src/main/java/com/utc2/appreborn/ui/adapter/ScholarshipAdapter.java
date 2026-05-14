package com.utc2.appreborn.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.utc2.appreborn.R;
import com.utc2.appreborn.model.Scholarship;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * ScholarshipAdapter - Adapter cho danh sách học bổng.
 * Status badge đổi màu theo trạng thái "Chưa nhận" / "Đã nhận".
 */
public class ScholarshipAdapter extends RecyclerView.Adapter<ScholarshipAdapter.ScholarshipViewHolder> {

    private final List<Scholarship> scholarshipList;
    private final Context context;

    public ScholarshipAdapter(Context context, List<Scholarship> scholarshipList) {
        this.context = context;
        this.scholarshipList = scholarshipList;
    }

    public void updateList(List<Scholarship> newList) {
        scholarshipList.clear();
        scholarshipList.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ScholarshipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_scholarship, parent, false);
        return new ScholarshipViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ScholarshipViewHolder holder, int position) {
        holder.bind(scholarshipList.get(position));
    }

    @Override
    public int getItemCount() {
        return scholarshipList.size();
    }

    // ─────────────────────────────────────────
    // ViewHolder
    // ─────────────────────────────────────────
    class ScholarshipViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvName;
        private final TextView tvOrganization;
        private final TextView tvValueAmount;
        private final TextView tvStatus;
        private final TextView tvMinGpa;

        ScholarshipViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName         = itemView.findViewById(R.id.tv_scholarship_name);
            tvOrganization = itemView.findViewById(R.id.tv_organization);
            tvValueAmount  = itemView.findViewById(R.id.tv_value_amount);
            tvStatus       = itemView.findViewById(R.id.tv_status);
            tvMinGpa       = itemView.findViewById(R.id.tv_min_gpa);
        }

        void bind(Scholarship scholarship) {
            tvName.setText(scholarship.getName());
            tvOrganization.setText(scholarship.getOrganization());

            // Format số tiền: 3.000.000 đ / HK
            NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
            String formattedAmount = formatter.format(scholarship.getValueAmount())
                    + " đ / " + scholarship.getValuePeriod();
            tvValueAmount.setText(formattedAmount);

            tvStatus.setText(scholarship.getStatus());
            tvMinGpa.setText(String.format(Locale.getDefault(),
                    "GPA tối thiểu: %.2f", scholarship.getMinGpa()));

            // Đổi màu badge theo trạng thái
            int bgRes = scholarship.isReceived()
                    ? R.drawable.bg_status_received
                    : R.drawable.bg_status_not_received;
            tvStatus.setBackground(ContextCompat.getDrawable(context, bgRes));

            int textColorRes = scholarship.isReceived()
                    ? R.color.status_received_text
                    : R.color.status_not_received_text;
            tvStatus.setTextColor(ContextCompat.getColor(context, textColorRes));
        }
    }
}
