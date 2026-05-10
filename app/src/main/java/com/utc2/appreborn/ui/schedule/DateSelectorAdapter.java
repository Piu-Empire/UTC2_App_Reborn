package com.utc2.appreborn.ui.schedule;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.utc2.appreborn.R;
import com.utc2.appreborn.utils.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

// quản lý việc hiển thị danh sách chọn ngày ngang
public class DateSelectorAdapter extends RecyclerView.Adapter<DateSelectorAdapter.ViewHolder> {
    private final List<Calendar> days = new ArrayList<>();
    private Calendar selectedDate;
    private final OnDateClickListener listener;

    // định nghĩa sự kiện khi người dùng nhấn chọn ngày
    public interface OnDateClickListener {
        void onDateClick(Calendar date);
    }

    // thiết lập dữ liệu ban đầu cho bộ điều hợp
    public DateSelectorAdapter(Calendar initialDate, OnDateClickListener listener) {
        this.selectedDate = initialDate;
        this.listener = listener;
    }

    // truy xuất đối tượng ngày tại vị trí xác định
    public Calendar getDateAtPosition(int position) {
        if (position >= 0 && position < days.size()) {
            return days.get(position);
        }
        return null;
    }

    // tạo danh sách ngày xung quanh một mốc cụ thể
    public void initAroundDate(Calendar pivotDate) {
        days.clear();
        for (int i = 7; i >= 1; i--) {
            days.add(DateUtils.addDays(pivotDate, -i));
        }
        days.add(DateUtils.clone(pivotDate));
        for (int i = 1; i <= 7; i++) {
            days.add(DateUtils.addDays(pivotDate, i));
        }
        notifyDataSetChanged();
    }

    // thêm các ngày của tuần tiếp theo vào danh sách
    public void addFutureWeek() {
        Calendar lastDate = days.get(days.size() - 1);
        for (int i = 1; i <= 7; i++) {
            days.add(DateUtils.addDays(lastDate, i));
        }
        notifyItemRangeInserted(days.size() - 7, 7);
    }

    // chèn thêm các ngày của tuần trước vào đầu
    public void addPastWeek() {
        Calendar firstDate = days.get(0);
        for (int i = 1; i <= 7; i++) {
            days.add(0, DateUtils.addDays(firstDate, -i));
        }
        notifyItemRangeInserted(0, 7);
    }

    // tạo và liên kết cấu trúc giao diện cho mục
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_date_selector, parent, false);
        return new ViewHolder(view);
    }

    // xử lý dữ liệu hiển thị và trạng thái của ngày
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Calendar day = days.get(position);
        boolean isSelected = DateUtils.isSameDay(day, selectedDate);
        boolean isToday = DateUtils.isToday(day);
        Context context = holder.itemView.getContext();

        holder.tvDayNumber.setText(String.valueOf(day.get(Calendar.DAY_OF_MONTH)));
        holder.tvDayName.setText(ScheduleLanguage.Format.shortDayName(day));
        holder.itemView.setSelected(isSelected);

        if (isSelected) {
            holder.tvDayNumber.setTextColor(ScheduleColors.DateSelector.selectedText(context));
            holder.tvDayName.setTextColor(ScheduleColors.DateSelector.selectedDayName(context));
            holder.itemView.setBackgroundResource(R.drawable.bg_date_selected);
            holder.itemView.setElevation(3f);
            if (holder.viewTodayDot != null) holder.viewTodayDot.setVisibility(View.INVISIBLE);
        } else if (isToday) {
            holder.tvDayNumber.setTextColor(ScheduleColors.DateSelector.todayText(context));
            holder.tvDayName.setTextColor(ScheduleColors.DateSelector.todayDayName(context));
            holder.itemView.setBackground(null);
            if (holder.viewTodayDot != null) holder.viewTodayDot.setVisibility(View.VISIBLE);
        } else {
            holder.tvDayNumber.setTextColor(ScheduleColors.DateSelector.normalText(context));
            holder.tvDayName.setTextColor(ScheduleColors.DateSelector.normalDayName(context));
            holder.itemView.setBackground(null);
            if (holder.viewTodayDot != null) holder.viewTodayDot.setVisibility(View.INVISIBLE);
        }

        holder.itemView.setOnClickListener(v -> {
            int oldPos = getPositionForDate(selectedDate);
            selectedDate = day;
            int newPos = getPositionForDate(day);
            if (oldPos != -1) notifyItemChanged(oldPos);
            if (newPos != -1) notifyItemChanged(newPos);
            playSelectAnimation(v);
            listener.onDateClick(day);
        });
    }

    // tạo hiệu ứng thu phóng khi nhấn vào mục
    private void playSelectAnimation(View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.88f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.88f, 1f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(scaleX, scaleY);
        set.setDuration(200);
        set.start();
    }

    // cung cấp tổng số lượng ngày hiện có
    @Override
    public int getItemCount() {
        return days.size();
    }

    // tìm kiếm vị trí của ngày cụ thể trong mảng
    public int getPositionForDate(Calendar date) {
        for (int i = 0; i < days.size(); i++) {
            if (DateUtils.isSameDay(days.get(i), date)) return i;
        }
        return -1;
    }

    // cập nhật trạng thái ngày được chọn từ bên ngoài
    public void updateSelectedDate(Calendar newDate) {
        this.selectedDate = newDate;
        notifyDataSetChanged();
    }

    // lưu trữ và quản lý các thành phần giao diện mục
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayName, tvDayNumber;
        View viewTodayDot;

        ViewHolder(View itemView) {
            super(itemView);
            tvDayName = itemView.findViewById(R.id.tvDayName);
            tvDayNumber = itemView.findViewById(R.id.tvDayNumber);
            viewTodayDot = itemView.findViewById(R.id.viewTodayDot);
        }
    }
}