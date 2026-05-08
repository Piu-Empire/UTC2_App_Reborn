package com.utc2.appreborn.ui.profile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.profile.model.Subject;

import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_SUBJECT = 1;
    private List<Subject> subjectList;

    public SubjectAdapter(List<Subject> subjectList) {
        this.subjectList = subjectList;
    }

    public void updateList(List<Subject> newList) {
        this.subjectList = newList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return subjectList.get(position).isHeader() ? TYPE_HEADER : TYPE_SUBJECT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_semester_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subject, parent, false);
            return new SubjectViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Subject subject = subjectList.get(position);
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).tvHeader.setText(subject.getName());
        } else {
            SubjectViewHolder sHolder = (SubjectViewHolder) holder;
            sHolder.tvName.setText(subject.getName());
            sHolder.tvCode.setText(subject.getCode());
            sHolder.tvCredit.setText(subject.getCredit() + " tín chỉ");
            sHolder.tvScore.setText("Điểm: " + subject.getScore());
        }
    }

    @Override
    public int getItemCount() {
        return subjectList != null ? subjectList.size() : 0;
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeader;
        HeaderViewHolder(View itemView) {
            super(itemView);
            tvHeader = itemView.findViewById(R.id.txtHeaderTitle);
        }
    }

    static class SubjectViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCode, tvCredit, tvScore;
        SubjectViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvCode = itemView.findViewById(R.id.tvCode);
            tvCredit = itemView.findViewById(R.id.tvCredit);
            tvScore = itemView.findViewById(R.id.tvScore);
        }
    }
}