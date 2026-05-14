package com.utc2.appreborn.ui.schedule;

import com.utc2.appreborn.utils.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ScheduleNavigator {
    private Calendar currentCalendar;

    // thiết lập thời gian mặc định khi khởi tạo lớp
    public ScheduleNavigator() {
        currentCalendar = Calendar.getInstance();
    }

    // dịch chuyển mốc thời gian thêm một tuần kế tiếp
    public void nextWeek() {
        currentCalendar = DateUtils.addWeeks(currentCalendar, 1);
    }

    // lùi mốc thời gian lại một tuần phía trước
    public void previousWeek() {
        currentCalendar = DateUtils.addWeeks(currentCalendar, -1);
    }

    // đưa mốc thời gian trở về đúng ngày hôm nay
    public void resetToToday() {
        currentCalendar = Calendar.getInstance();
    }

    // cung cấp chuỗi ký tự mô tả khoảng thời gian tuần
    public String getWeekRangeString() {
        return DateUtils.getWeekRange(currentCalendar);
    }

    // tạo danh sách ngày trong tuần kèm trạng thái hôm nay
    public List<DateModel> getDaysInWeek() {
        List<DateModel> result = new ArrayList<>();
        List<Calendar> days = DateUtils.getDaysOfWeek(currentCalendar);
        Calendar now = Calendar.getInstance();

        for (Calendar day : days) {
            result.add(new DateModel(
                    ScheduleLanguage.Format.shortDayName(day),
                    ScheduleLanguage.Lang.format(day, "dd"),
                    DateUtils.isSameDay(day, now)
            ));
        }
        return result;
    }

    // trích xuất bản sao của đối tượng thời gian hiện tại
    public Calendar getCurrentCalendar() {
        return DateUtils.clone(currentCalendar);
    }

    // cập nhật mốc thời gian mới bằng một bản sao
    public void setCurrentCalendar(Calendar calendar) {
        this.currentCalendar = DateUtils.clone(calendar);
    }

    // cấu trúc lưu trữ thông tin ngày hiển thị đơn giản
    public static class DateModel {
        public String dayName;
        public String dayMonth;
        public boolean isToday;

        public DateModel(String dayName, String dayMonth, boolean isToday) {
            this.dayName = dayName;
            this.dayMonth = dayMonth;
            this.isToday = isToday;
        }
    }
}