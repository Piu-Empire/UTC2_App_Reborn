package com.utc2.appreborn.ui.profile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.profile.model.Subject;

import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER  = 0;
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
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            View view = inflater.inflate(R.layout.item_semester_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_subject, parent, false);
            return new SubjectViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Subject subject = subjectList.get(position);

        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).tvHeader.setText(subject.getName());
            return;
        }

        SubjectViewHolder h = (SubjectViewHolder) holder;
        Context ctx = h.itemView.getContext();

        h.tvName.setText(subject.getName());
        h.tvCode.setText(subject.getCode());
        h.tvCredit.setText(subject.getCredit() + " tín chỉ");

        String score       = subject.getScore();        // "N/A" nếu null
        String letterGrade = subject.getLetterGrade();
        boolean hasScore   = !score.equals("N/A") && letterGrade != null && !letterGrade.isEmpty();

        // Hàng 1: điểm số + xếp loại chữ
        if (hasScore) {
            h.tvScore.setText(score + "  |  " + letterGrade);
        } else {
            h.tvScore.setText("N/A");
        }

        // Hàng 2: GPA + trạng thái — chỉ hiện khi đã có điểm
        if (hasScore) {
            h.layoutGradeDetail.setVisibility(View.VISIBLE);
            h.tvGradePoint.setText(String.format("GPA: %.1f", subject.getGradePoint()));

            if (subject.isPassed()) {
                h.tvStatus.setText("Đạt");
                h.tvStatus.setTextColor(ContextCompat.getColor(ctx, R.color.green_success));
            } else {
                h.tvStatus.setText("Không đạt");
                h.tvStatus.setTextColor(ContextCompat.getColor(ctx, android.R.color.holo_red_dark));
            }
        } else {
            h.layoutGradeDetail.setVisibility(View.GONE);
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
        TextView     tvName, tvCode, tvCredit, tvScore, tvGradePoint, tvStatus;
        LinearLayout layoutGradeDetail;

        SubjectViewHolder(View itemView) {
            super(itemView);
            tvName            = itemView.findViewById(R.id.tvName);
            tvCode            = itemView.findViewById(R.id.tvCode);
            tvCredit          = itemView.findViewById(R.id.tvCredit);
            tvScore           = itemView.findViewById(R.id.tvScore);
            tvGradePoint      = itemView.findViewById(R.id.tvGradePoint);
            tvStatus          = itemView.findViewById(R.id.tvStatus);
            layoutGradeDetail = itemView.findViewById(R.id.layoutGradeDetail);
        }
    }
}