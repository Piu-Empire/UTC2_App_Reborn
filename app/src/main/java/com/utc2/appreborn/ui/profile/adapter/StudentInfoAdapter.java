package com.utc2.appreborn.ui.profile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.profile.model.StudentInfoItem;

import java.util.List;

public class StudentInfoAdapter extends RecyclerView.Adapter<StudentInfoAdapter.ViewHolder> {
    private List<StudentInfoItem> infoList;

    public StudentInfoAdapter(List<StudentInfoItem> infoList) {
        this.infoList = infoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail_info_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudentInfoItem item = infoList.get(position);
        holder.tvLabel.setText(item.getLabel());
        holder.tvValue.setText(item.getValue());
    }

    @Override
    public int getItemCount() { return infoList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLabel, tvValue;
        public ViewHolder(View itemView) {
            super(itemView);
            tvLabel = itemView.findViewById(R.id.tvLabel);
            tvValue = itemView.findViewById(R.id.tvValue);
        }
    }
}