package com.utc2.appreborn.ui.schedule;

import android.content.Context;

import com.utc2.appreborn.R;
import com.utc2.appreborn.utils.LocaleHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

// tập hợp các công cụ xử lý ngôn ngữ và định dạng
public final class ScheduleLanguage {

    private ScheduleLanguage() {
    }

    public static final class Lang {
        // trả về locale hiện tại của app (vi hoặc en) thay vì hardcode vi-VN
        public static Locale schedule(Context c) {
            String lang = LocaleHelper.getSavedLanguage(c);
            if ("en".equals(lang)) return Locale.ENGLISH;
            return Locale.forLanguageTag("vi-VN");
        }

        // giữ overload không có context để tương thích ngược (mặc định vi-VN)
        public static Locale schedule() {
            return Locale.forLanguageTag("vi-VN");
        }

        // chuyển đổi ngày sang chuỗi văn bản theo locale hiện tại của app
        public static String format(Context c, Calendar cal, String pattern) {
            return new SimpleDateFormat(pattern, schedule(c)).format(cal.getTime());
        }

        // overload cũ giữ lại để không break code hiện tại
        public static String format(Calendar cal, String pattern) {
            return new SimpleDateFormat(pattern, schedule()).format(cal.getTime());
        }
    }

    public static final class Label {
        // lấy nội dung hiển thị cho khung giờ buổi sáng
        public static String sessionMorning(Context c) {
            return c.getString(R.string.session_morning);
        }

        // lấy nội dung hiển thị cho khung giờ buổi chiều
        public static String sessionAfternoon(Context c) {
            return c.getString(R.string.session_afternoon);
        }

        // lấy nội dung hiển thị cho khung giờ buổi tối
        public static String sessionEvening(Context c) {
            return c.getString(R.string.session_evening);
        }

        // hiển thị thông báo khi không có lịch học diễn ra
        public static String noClass(Context c) {
            return c.getString(R.string.label_no_class);
        }

        // cung cấp nhãn viết hoa đánh dấu ngày hôm nay
        public static String todayUpper(Context c) {
            return c.getString(R.string.today_upper);
        }

        // lấy tiền tố hiển thị trước số phòng học cụ thể
        public static String roomPrefix(Context c) {
            return c.getString(R.string.label_room);
        }

        // lấy nhãn mô tả cho tổng số lượng sinh viên
        public static String studentCountPrefix(Context c) {
            return c.getString(R.string.label_student_count);
        }

        // cung cấp đơn vị tính cho các tiết học cụ thể
        public static String period(Context c) {
            return c.getString(R.string.label_period);
        }

        // xác định nhãn hiển thị cho loại tiết học lý thuyết
        public static String theory(Context c) {
            return c.getString(R.string.label_theory);
        }

        // lấy tiêu đề cho hộp thoại lựa chọn ngày tháng
        public static String pickDateTitle(Context c) {
            return c.getString(R.string.label_pick_date);
        }
    }

    public static final class Format {
        // chuẩn hóa tên giảng viên kèm theo tiền tố phù hợp
        public static String lecturer(Context c, String name) {
            if (name != null && name.startsWith("GV")) return name;
            return c.getString(R.string.prefix_lecturer, name != null ? name : "");
        }

        // hiển thị số tiết học còn lại theo dạng đầy đủ
        public static String remainingPeriods(Context c, int count) {
            return c.getString(R.string.format_remaining_periods, count);
        }

        // hiển thị số tiết học còn lại theo dạng rút gọn
        public static String remainingPeriodsShort(Context c, int count) {
            return c.getString(R.string.format_remaining_short, count);
        }

        // rút ngắn khoảng thời gian để hiển thị trên ô lịch
        public static String dateRangeShort(String startDate, String endDate) {
            String start = startDate != null ? startDate : "??/??";
            if (start.length() > 5 && start.contains("/")) {
                String[] parts = start.split("/");
                if (parts.length >= 2) start = parts[0] + "/" + parts[1];
            }
            String end = endDate != null ? endDate : "??/??";
            return start + " \u2014 " + end;
        }

        // cung cấp khoảng thời gian đầy đủ gồm ngày tháng năm
        public static String dateRangeFull(String startDate, String endDate) {
            String start = startDate != null ? startDate : "??/??/????";
            String end = endDate != null ? endDate : "??/??/????";
            return start + " \u2014 " + end;
        }

        // tạo tiêu đề ngày tháng đầy đủ cho danh sách dọc — dùng locale app
        public static String fullDateHeader(Context c, Calendar cal) {
            return Lang.format(c, cal, "EEEE, dd/MM/yyyy");
        }

        // overload cũ giữ lại để không break code hiện tại
        public static String fullDateHeader(Calendar cal) {
            return Lang.format(cal, "EEEE, dd/MM/yyyy");
        }

        // lấy tên thứ ở dạng viết tắt theo locale app
        public static String shortDayName(Context c, Calendar cal) {
            return Lang.format(c, cal, "EEE");
        }

        // overload cũ giữ lại để không break code hiện tại
        public static String shortDayName(Calendar cal) {
            return Lang.format(cal, "EEE");
        }

        // định dạng ngày tháng năm theo kiểu rút gọn cơ bản
        public static String shortDate(Calendar cal) {
            return Lang.format(cal, "dd/MM/yyyy");
        }
    }
}