package com.utc2.appreborn.utils;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TimeUtils {

    public static final String TIME_FORMAT_HH_MM = "HH:mm";
    public static final String TIME_FORMAT_FULL = "HH:mm:ss";

    // format giờ
    public static String format(Calendar calendar, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    // lấy giờ hiện tại dạng string
    public static String getCurrentTime() {
        return format(Calendar.getInstance(), TIME_FORMAT_HH_MM);
    }

    // lấy giờ bắt đầu (00:00) - reuse DateUtils
    public static Calendar getStartOfDay(Calendar cal) {
        return DateUtils.getStartOfDay(cal);
    }

    // lấy giờ kết thúc (23:59)
    public static Calendar getEndOfDay(Calendar cal) {
        return DateUtils.getEndOfDay(cal);
    }

    // cộng thêm giờ
    public static Calendar addHours(Calendar cal, int hours) {
        Calendar clone = (Calendar) cal.clone();
        clone.add(Calendar.HOUR_OF_DAY, hours);
        return clone;
    }

    // cộng thêm phút
    public static Calendar addMinutes(Calendar cal, int minutes) {
        Calendar clone = (Calendar) cal.clone();
        clone.add(Calendar.MINUTE, minutes);
        return clone;
    }

    // lấy giờ từ calendar
    public static int getHour(Calendar cal) {
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    // lấy phút
    public static int getMinute(Calendar cal) {
        return cal.get(Calendar.MINUTE);
    }

    // set giờ
    public static Calendar setTime(Calendar cal, int hour, int minute) {
        Calendar clone = (Calendar) cal.clone();
        clone.set(Calendar.HOUR_OF_DAY, hour);
        clone.set(Calendar.MINUTE, minute);
        clone.set(Calendar.SECOND, 0);
        clone.set(Calendar.MILLISECOND, 0);
        return clone;
    }

    // so sánh giờ (ignore ngày)
    public static int compareTime(Calendar c1, Calendar c2) {
        int hDiff = getHour(c1) - getHour(c2);
        if (hDiff != 0) return hDiff;

        return getMinute(c1) - getMinute(c2);
    }
}