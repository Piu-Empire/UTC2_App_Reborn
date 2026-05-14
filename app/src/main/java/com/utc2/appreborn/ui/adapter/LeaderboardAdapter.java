package com.utc2.appreborn.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.utc2.appreborn.R;
import com.utc2.appreborn.model.LeaderboardEntry;   // ← đổi từ results.model

import java.util.List;
import java.util.Locale;

/**
 * LeaderboardAdapter - Adapter cho bảng xếp hạng sinh viên.
 * Row của sinh viên hiện tại được highlight (bold + đường kẻ dưới).
 */
public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder> {

    private final List<LeaderboardEntry> entryList;
    private final Context context;

    public LeaderboardAdapter(Context context, List<LeaderboardEntry> entryList) {
        this.context = context;
        this.entryList = entryList;
    }

    public void updateList(List<LeaderboardEntry> newList) {
        entryList.clear();
        entryList.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_leaderboard, parent, false);
        return new LeaderboardViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        holder.bind(entryList.get(position));
    }

    @Override
    public int getItemCount() {
        return entryList.size();
    }

    // ─────────────────────────────────────────
    // ViewHolder
    // ─────────────────────────────────────────
    class LeaderboardViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvRank;
        private final TextView tvAvatarInitials;
        private final TextView tvStudentName;
        private final TextView tvCredits;
        private final TextView tvGpa;
        private final View     dividerHighlight;

        LeaderboardViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank           = itemView.findViewById(R.id.tv_rank);
            tvAvatarInitials = itemView.findViewById(R.id.tv_avatar_initials);
            tvStudentName    = itemView.findViewById(R.id.tv_student_name);
            tvCredits        = itemView.findViewById(R.id.tv_credits);
            tvGpa            = itemView.findViewById(R.id.tv_gpa);
            dividerHighlight = itemView.findViewById(R.id.divider_highlight);
        }

        void bind(LeaderboardEntry entry) {
            tvRank.setText("# " + entry.getRank());
            tvAvatarInitials.setText(entry.getAvatarInitials());
            tvGpa.setText(String.format(Locale.getDefault(), "%.2f", entry.getGpa()));
            tvCredits.setText(entry.getTotalCredits() + " tín chỉ");

            // Highlight row của sinh viên hiện tại
            if (entry.isCurrentUser()) {
                tvStudentName.setText(entry.getStudentName() + " (Bạn)");
                tvStudentName.setTypeface(null, Typeface.BOLD);
                tvRank.setTypeface(null, Typeface.BOLD);
                dividerHighlight.setVisibility(View.VISIBLE);
            } else {
                tvStudentName.setText(entry.getStudentName());
                tvStudentName.setTypeface(null, Typeface.NORMAL);
                tvRank.setTypeface(null, Typeface.NORMAL);
                dividerHighlight.setVisibility(View.GONE);
            }
        }
    }
}
