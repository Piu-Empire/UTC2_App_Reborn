package com.utc2.appreborn.ui.tuition.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.tuition.model.SubjectTuition;
import com.utc2.appreborn.ui.tuition.model.Tuition;

import java.util.List;
import java.util.Locale;

public class SubjectTuitionAdapter extends RecyclerView.Adapter<SubjectTuitionAdapter.ViewHolder> {

    private final List<SubjectTuition> subjectList;

    public SubjectTuitionAdapter(List<SubjectTuition> subjectList) {
        this.subjectList = subjectList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_subject_tuition, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SubjectTuition item = subjectList.get(position);

        holder.tvName.setText(item.getName());
        holder.tvDetails.setText(item.getDetails());

        boolean unpaid = Tuition.STATUS_UNPAID.equals(item.getStatus())
                || Tuition.STATUS_PARTIAL.equals(item.getStatus());

        if (unpaid) {
            // Còn nợ → hiện số tiền CẦN ĐÓNG (remaining)
            holder.tvAmount.setText(String.format(Locale.getDefault(),
                    "%,.0f VND", item.getRemainingAmount()));
            holder.tvAmount.setTextColor(Color.RED);
            holder.tvStatus.setText(
                    holder.itemView.getContext().getString(R.string.status_unpaid));
            holder.tvStatus.setTextColor(Color.RED);
        } else {
            // Đã đóng đủ → hiện TỔNG HỌC PHÍ (total) để user biết đã đóng bao nhiêu
            holder.tvAmount.setText(String.format(Locale.getDefault(),
                    "%,.0f VND", item.getTotalAmount()));
            holder.tvAmount.setTextColor(Color.parseColor("#4CAF50")); // green
            holder.tvStatus.setText(
                    holder.itemView.getContext().getString(R.string.status_paid));
            holder.tvStatus.setTextColor(Color.parseColor("#4CAF50"));
        }
    }

    @Override
    public int getItemCount() {
        return subjectList != null ? subjectList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDetails, tvAmount, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName    = itemView.findViewById(R.id.tvSubjectName);
            tvDetails = itemView.findViewById(R.id.tvSubjectDetails);
            tvAmount  = itemView.findViewById(R.id.tvAmount);
            tvStatus  = itemView.findViewById(R.id.tvStatus);
        }
    }
}