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
import com.utc2.appreborn.model.CourseGrade;

import java.util.List;
import java.util.Locale;

public class GradeAdapter extends RecyclerView.Adapter<GradeAdapter.GradeViewHolder> {

    private final List<CourseGrade> gradeList;
    private final Context context;

    public GradeAdapter(Context context, List<CourseGrade> gradeList) {
        this.context = context;
        this.gradeList = gradeList;
    }

    public void updateList(List<CourseGrade> newList) {
        gradeList.clear();
        gradeList.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GradeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_grade, parent, false);
        return new GradeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GradeViewHolder holder, int position) {
        holder.bind(gradeList.get(position));
    }

    @Override
    public int getItemCount() {
        return gradeList.size();
    }

    class GradeViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvCourseCode;
        private final TextView tvCourseName;
        private final TextView tvCredits;
        private final TextView tvMidterm;
        private final TextView tvFinal;
        private final TextView tvGpa;
        private final TextView tvLetterGrade;

        GradeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourseCode  = itemView.findViewById(R.id.tv_course_code);
            tvCourseName  = itemView.findViewById(R.id.tv_course_name);
            tvCredits     = itemView.findViewById(R.id.tv_credits);
            tvMidterm     = itemView.findViewById(R.id.tv_midterm);
            tvFinal       = itemView.findViewById(R.id.tv_final);
            tvGpa         = itemView.findViewById(R.id.tv_gpa);
            tvLetterGrade = itemView.findViewById(R.id.tv_letter_grade);
        }

        void bind(CourseGrade grade) {
            tvCourseCode.setText(grade.getCourseCode());
            tvCourseName.setText(grade.getCourseName());
            tvCredits.setText(grade.getCredits() + " TC");
            tvMidterm.setText(String.format(Locale.getDefault(), "%.1f", grade.getMidtermScore()));
            tvFinal.setText(String.format(Locale.getDefault(), "%.1f", grade.getFinalScore()));
            tvGpa.setText(String.format(Locale.getDefault(), "%.2f", grade.getGpaScore()));
            tvLetterGrade.setText(grade.getLetterGrade());

            int bgRes = grade.isPassed() ? R.drawable.bg_grade_pass : R.drawable.bg_grade_fail;
            tvLetterGrade.setBackground(ContextCompat.getDrawable(context, bgRes));
        }
    }
}
