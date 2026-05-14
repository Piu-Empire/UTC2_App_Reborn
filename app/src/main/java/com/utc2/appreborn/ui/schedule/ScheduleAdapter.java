package com.utc2.appreborn.ui.schedule;

import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.utc2.appreborn.R;
import com.utc2.appreborn.model.ScheduleItem;

public class ScheduleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_DATE_HEADER = 0;
    private static final int TYPE_DATE_HEADER_TODAY = 4;
    private static final int TYPE_SESSION_HEADER = 1;
    private static final int TYPE_SUBJECT_ITEM = 2;
    private static final int TYPE_EMPTY = 3;

    private List<ScheduleItem> scheduleList;
    private static final String TAG_ANIMATED = "animated";
    private boolean animationEnabled = true;

    // khởi tạo bộ chuyển đổi với danh sách dữ liệu lịch
    public ScheduleAdapter(List<ScheduleItem> scheduleList) {
        this.scheduleList = scheduleList;
    }

    // phân loại các kiểu hiển thị dựa trên mã môn học
    @Override
    public int getItemViewType(int position) {
        String code = scheduleList.get(position).getSubjectCode();
        if ("HEADER_DATE_TODAY".equals(code)) return TYPE_DATE_HEADER_TODAY;
        if ("HEADER_DATE".equals(code)) return TYPE_DATE_HEADER;
        if ("HEADER_SESSION".equals(code)) return TYPE_SESSION_HEADER;
        if ("EMPTY_NOTE".equals(code)) return TYPE_EMPTY;
        return TYPE_SUBJECT_ITEM;
    }

    // khởi tạo giao diện tương ứng cho từng loại mục hiển thị
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_DATE_HEADER_TODAY:
            case TYPE_DATE_HEADER:
                return new HeaderViewHolder(inflater.inflate(R.layout.item_date_header, parent, false));
            case TYPE_SESSION_HEADER:
                return new SessionViewHolder(inflater.inflate(R.layout.item_session_header, parent, false));
            default:
                return new ScheduleViewHolder(inflater.inflate(R.layout.item_schedule, parent, false));
        }
    }

    // gắn dữ liệu và thiết lập định dạng cho từng thành phần
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ScheduleItem schedule = scheduleList.get(position);

        if (animationEnabled && !TAG_ANIMATED.equals(holder.itemView.getTag())) {
            holder.itemView.setTag(TAG_ANIMATED);
            animateItemIn(holder.itemView, position);
        }

        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder h = (HeaderViewHolder) holder;
            h.tvHeaderDate.setText(schedule.getSubjectName());
            android.content.Context context = h.itemView.getContext();

            boolean isToday = "HEADER_DATE_TODAY".equals(schedule.getSubjectCode());
            if (isToday) {
                h.itemView.setBackgroundColor(ScheduleColors.AdapterHeader.todayBackground(context));
                h.tvHeaderDate.setTextColor(ScheduleColors.AdapterHeader.todayText(context));
                h.tvHeaderDate.setTextSize(14f);
                if (h.tvTodayBadge != null) h.tvTodayBadge.setVisibility(View.VISIBLE);
                if (h.viewTodayDot != null) h.viewTodayDot.setVisibility(View.VISIBLE);
            } else {
                h.itemView.setBackgroundColor(ScheduleColors.AdapterHeader.normalBackground(context));
                h.tvHeaderDate.setTextColor(ScheduleColors.AdapterHeader.normalText(context));
                h.tvHeaderDate.setTextSize(13f);
                if (h.tvTodayBadge != null) h.tvTodayBadge.setVisibility(View.GONE);
                if (h.viewTodayDot != null) h.viewTodayDot.setVisibility(View.GONE);
            }
        } else if (holder instanceof SessionViewHolder) {
            ((SessionViewHolder) holder).tvSessionName.setText(schedule.getSubjectName());
        } else if (holder instanceof ScheduleViewHolder) {
            ScheduleViewHolder sHolder = (ScheduleViewHolder) holder;
            android.content.Context context = sHolder.itemView.getContext();

            if ("EMPTY_NOTE".equals(schedule.getSubjectCode())) {
                sHolder.tvStartTime.setText("--:--");
                sHolder.tvEndTime.setText("--:--");
                sHolder.tvStartPeriod.setText("-");
                sHolder.tvEndPeriod.setText("-");
                sHolder.tvSubjectName.setText(ScheduleLanguage.Label.noClass(context));
                sHolder.itemView.findViewById(R.id.layoutContent).setAlpha(0.4f);
            } else {
                sHolder.itemView.findViewById(R.id.layoutContent).setAlpha(1.0f);
                sHolder.tvStartTime.setText(schedule.getStartTime() != null ? schedule.getStartTime() : "00:00");
                sHolder.tvEndTime.setText(schedule.getEndTime() != null ? schedule.getEndTime() : "00:00");
                sHolder.tvStartPeriod.setText(String.valueOf(schedule.getStartPeriod()));
                sHolder.tvEndPeriod.setText(String.valueOf(schedule.getEndPeriod()));
                sHolder.tvClassName.setText(schedule.getSubjectCode() != null ? schedule.getSubjectCode() : "CQ.65.CNTT");
                sHolder.tvSemester.setText(schedule.getSemester() != null ? schedule.getSemester() : "Học kỳ 2 (2025 - 2026)");
                sHolder.tvSubjectName.setText(schedule.getSubjectName());
                String gv = schedule.getLecturer();
                sHolder.tvLecturer.setText(ScheduleLanguage.Format.lecturer(context, gv));
                sHolder.tvLocation.setText(schedule.getRoom() != null ? schedule.getRoom() : "P104C2");
                String startD = schedule.getStartDate() != null ? schedule.getStartDate() : "13/04/2026";
                String endD = schedule.getEndDate() != null ? schedule.getEndDate() : "06/06/2026";
                sHolder.tvDateRange.setText(startD + " — " + endD);
                sHolder.tvStudentCount.setText(String.valueOf(schedule.getStudentCount() > 0 ? schedule.getStudentCount() : 89));
                sHolder.tvRemainingPeriods.setText(
                        ScheduleLanguage.Format.remainingPeriods(
                                context,
                                schedule.getRemainingPeriods(schedule.getDisplayDate())
                        )
                );
            }
        }
    }

    // tạo hiệu ứng trượt lên mờ dần khi hiện mục
    private void animateItemIn(View view, int position) {
        view.setAlpha(0f);
        view.setTranslationY(30f);
        long delay = Math.min(position % 6, 5) * 40L;
        view.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(delay)
                .setDuration(250)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    // trả về số lượng phần tử có trong danh sách lịch
    @Override
    public int getItemCount() {
        return scheduleList != null ? scheduleList.size() : 0;
    }

    // cập nhật toàn bộ dữ liệu mới và làm mới giao diện
    public void updateData(List<ScheduleItem> newList) {
        this.scheduleList = newList;
        animationEnabled = true;
        notifyDataSetChanged();
    }

    // thiết lập lại trạng thái để cho phép chạy hiệu ứng
    public void resetAnimation() {
        animationEnabled = true;
    }

    // ánh xạ các thành phần giao diện cho tiêu đề ngày
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeaderDate, tvTodayBadge;
        View viewTodayDot;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeaderDate = itemView.findViewById(R.id.tvHeaderDate);
            tvTodayBadge = itemView.findViewById(R.id.tvTodayBadge);
            viewTodayDot = itemView.findViewById(R.id.viewTodayDot);
        }
    }

    // ánh xạ giao diện cho mục tiêu đề buổi học
    public static class SessionViewHolder extends RecyclerView.ViewHolder {
        TextView tvSessionName;

        public SessionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSessionName = itemView.findViewById(R.id.tvSessionName);
        }
    }

    // ánh xạ tất cả thành phần giao diện của môn học
    public static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView tvStartTime, tvEndTime, tvStartPeriod, tvEndPeriod;
        TextView tvClassName, tvSemester, tvSubjectName, tvLecturer;
        TextView tvLocation, tvDateRange, tvStudentCount, tvRemainingPeriods;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStartTime = itemView.findViewById(R.id.tvStartTime);
            tvEndTime = itemView.findViewById(R.id.tvEndTime);
            tvStartPeriod = itemView.findViewById(R.id.tvStartPeriod);
            tvEndPeriod = itemView.findViewById(R.id.tvEndPeriod);
            tvClassName = itemView.findViewById(R.id.tvClassName);
            tvSemester = itemView.findViewById(R.id.tvSemester);
            tvSubjectName = itemView.findViewById(R.id.tvSubjectName);
            tvLecturer = itemView.findViewById(R.id.tvLecturer);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDateRange = itemView.findViewById(R.id.tvDateRange);
            tvStudentCount = itemView.findViewById(R.id.tvStudentCount);
            tvRemainingPeriods = itemView.findViewById(R.id.tvRemainingPeriods);
        }
    }
}