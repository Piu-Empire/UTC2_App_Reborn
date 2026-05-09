package com.utc2.appreborn.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DateUtils {
    public static Calendar HK1_START;
    public static Calendar HK1_END;
    public static Calendar HK2_START;
    public static Calendar HK2_END;
    public static final String DATE_FORMAT_RANGE = "dd/MM/yyyy";
    public static final String DATE_FORMAT_DAY_MONTH = "dd/MM";
    public static final String DATE_FORMAT_DAY_NAME = "EEE";

    // chuyển đổi đối tượng thời gian sang chuỗi văn bản
    public static String format(Calendar calendar, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    // tạo đối tượng thời gian từ chuỗi định dạng sẵn
    public static Calendar parseDate(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_RANGE, Locale.getDefault());
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(dateStr));
            return getStartOfDay(cal);
        } catch (Exception e) {
            return null;
        }
    }

    // tìm ngày thứ hai đầu tiên của tuần hiện tại
    public static Calendar getFirstDayOfWeek(Calendar current) {
        Calendar cal = (Calendar) current.clone();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        // tính khoảng cách ngày so với thứ hai đầu tuần
        int diff = cal.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
        // xử lý trường hợp ngày hiện tại là chủ nhật
        if (diff < 0) diff += 7;
        cal.add(Calendar.DAY_OF_MONTH, -diff);
        return cal;
    }

    // xác định ngày chủ nhật cuối cùng của tuần
    public static Calendar getLastDayOfWeek(Calendar current) {
        Calendar first = getFirstDayOfWeek(current);
        Calendar last = (Calendar) first.clone();
        last.add(Calendar.DAY_OF_YEAR, 6);
        return last;
    }

    // lấy chuỗi văn bản mô tả khoảng thời gian tuần
    public static String getWeekRange(Calendar current) {
        Calendar first = getFirstDayOfWeek(current);
        Calendar last = getLastDayOfWeek(current);
        return format(first, DATE_FORMAT_RANGE) + " - " + format(last, DATE_FORMAT_RANGE);
    }

    // kiểm tra hai mốc thời gian có cùng ngày không
    public static boolean isSameDay(Calendar c1, Calendar c2) {
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }

    // xác định ngày được chọn có phải hôm nay không
    public static boolean isToday(Calendar cal) {
        return isSameDay(cal, Calendar.getInstance());
    }

    // cộng thêm số ngày cụ thể vào mốc thời gian
    public static Calendar addDays(Calendar cal, int days) {
        Calendar clone = (Calendar) cal.clone();
        clone.add(Calendar.DAY_OF_MONTH, days);
        return clone;
    }

    // tăng thêm số tuần cho đối tượng thời gian hiện tại
    public static Calendar addWeeks(Calendar cal, int weeks) {
        Calendar clone = (Calendar) cal.clone();
        clone.add(Calendar.WEEK_OF_YEAR, weeks);
        return clone;
    }

    // tạo bản sao độc lập để tránh thay đổi dữ liệu gốc
    public static Calendar clone(Calendar cal) {
        return (Calendar) cal.clone();
    }

    // thiết lập thời gian về thời điểm bắt đầu ngày mới
    public static Calendar getStartOfDay(Calendar cal) {
        Calendar c = (Calendar) cal.clone();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c;
    }

    // đưa thời gian về khoảnh khắc cuối cùng của ngày
    public static Calendar getEndOfDay(Calendar cal) {
        Calendar c = (Calendar) cal.clone();
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c;
    }

    // tạo danh sách bảy ngày trong một tuần cụ thể
    public static List<Calendar> getDaysOfWeek(Calendar current) {
        List<Calendar> days = new ArrayList<>();
        Calendar start = getFirstDayOfWeek(current);
        for (int i = 0; i < 7; i++) {
            Calendar day = (Calendar) start.clone();
            day.add(Calendar.DAY_OF_MONTH, i);
            days.add(day);
        }
        return days;
    }

    // so sánh thứ tự thời gian giữa hai đối tượng
    public static int compare(Calendar c1, Calendar c2) {
        return c1.compareTo(c2);
    }

    // kiểm tra thời điểm này có trước thời điểm kia không
    public static boolean isBefore(Calendar c1, Calendar c2) {
        return c1.before(c2);
    }

    // xác định thời điểm này có sau thời điểm kia không
    public static boolean isAfter(Calendar c1, Calendar c2) {
        return c1.after(c2);
    }

    // thiết lập mốc thời gian bắt đầu học kỳ một
    public static void setSemesterHK1(int startMonth, int startDay, int endMonth, int endDay) {
        HK1_START = Calendar.getInstance();
        HK1_START.set(Calendar.MONTH, startMonth - 1);
        HK1_START.set(Calendar.DAY_OF_MONTH, startDay);
        HK1_END = Calendar.getInstance();
        HK1_END.set(Calendar.MONTH, endMonth - 1);
        HK1_END.set(Calendar.DAY_OF_MONTH, endDay);
    }

    // cấu hình thời gian diễn ra cho học kỳ hai
    public static void setSemesterHK2(int startMonth, int startDay, int endMonth, int endDay) {
        HK2_START = Calendar.getInstance();
        HK2_START.set(Calendar.MONTH, startMonth - 1);
        HK2_START.set(Calendar.DAY_OF_MONTH, startDay);
        HK2_END = Calendar.getInstance();
        HK2_END.set(Calendar.MONTH, endMonth - 1);
        HK2_END.set(Calendar.DAY_OF_MONTH, endDay);
    }

    // hiển thị tháng hiện tại theo ngôn ngữ hệ thống
    public static String getCurrentMonthDisplay() {
        return "Tháng " + format(Calendar.getInstance(), "MM");
    }

    // tính toán tuần học hiện tại dựa trên học kỳ
    public static String getCurrentCalendarWeek() {
        Calendar now = Calendar.getInstance();
        Calendar start = getSemesterStart();
        // tính chênh lệch thời gian theo đơn vị mili giây
        long diffMillis = now.getTimeInMillis() - start.getTimeInMillis();
        if (diffMillis < 0) {
            return "Tuần 0";
        }
        int days = (int) (diffMillis / (1000 * 60 * 60 * 24));
        int week = (days / 7) + 1;
        return "Tuần " + week;
    }

    // xác định học kỳ hiện tại của sinh viên
    public static String getCurrentSemester() {
        Calendar now = Calendar.getInstance();
        if (HK2_START != null && HK2_END != null &&
                !now.before(HK2_START) && !now.after(HK2_END)) {
            return "HK II";
        }
        return "HK I";
    }

    // tìm kiếm học kỳ tương ứng với ngày bất kỳ
    public static String getSemesterByDate(Calendar cal) {
        if (HK2_START != null && HK2_END != null &&
                !cal.before(HK2_START) && !cal.after(HK2_END)) {
            return "HK II";
        }
        return "HK I";
    }

    // lấy ngày bắt đầu của học kỳ chứa mốc thời gian
    public static Calendar getSemesterStart(Calendar cal) {
        return getSemesterByDate(cal).equals("HK II")
                ? (Calendar) HK2_START.clone()
                : (Calendar) HK1_START.clone();
    }

    // tính số thứ tự tuần học dưới dạng chuỗi
    public static String getWeekOfSemester(Calendar cal) {
        Calendar start = getSemesterStart(cal);
        Calendar s = getStartOfDay(start);
        Calendar c = getStartOfDay(cal);
        long diffMillis = c.getTimeInMillis() - s.getTimeInMillis();
        if (diffMillis < 0) return "Tuần 0";
        int days = (int) (diffMillis / (1000 * 60 * 60 * 24));
        // chia số ngày để tìm ra số thứ tự tuần
        int week = (days / 7) + 1;
        return "Tuần " + week;
    }

    // lấy số tuần học kỳ dưới dạng giá trị nguyên
    public static int getIntWeekOfSemester(Calendar cal) {
        Calendar start = getSemesterStart(cal);
        Calendar s = getStartOfDay(start);
        Calendar c = getStartOfDay(cal);
        long diffMillis = c.getTimeInMillis() - s.getTimeInMillis();
        if (diffMillis < 0) return 0;
        int days = (int) (diffMillis / (1000 * 60 * 60 * 24));
        int week = (days / 7) + 1;
        return week;
    }

    // hiển thị tên tháng của một ngày cụ thể
    public static String getMonthDisplay(Calendar cal) {
        return "Tháng " + format(cal, "MM");
    }

    // truy xuất ngày khởi đầu của học kỳ hiện tại
    public static Calendar getSemesterStart() {
        return getCurrentSemester().equals("HK II")
                ? (Calendar) HK2_START.clone()
                : (Calendar) HK1_START.clone();
    }

    // lấy ngày kết thúc của học kỳ hiện tại
    public static Calendar getSemesterEnd() {
        return getCurrentSemester().equals("HK II")
                ? (Calendar) HK2_END.clone()
                : (Calendar) HK1_END.clone();
    }
}