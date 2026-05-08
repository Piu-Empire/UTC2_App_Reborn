package com.utc2.appreborn.ui.schedule;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.utc2.appreborn.R;

public final class ScheduleColors {

    // ngăn chặn việc khởi tạo thực thể lớp này
    private ScheduleColors() {
    }

    public static final class DateSelector {
        // lấy màu văn bản khi người dùng chọn ngày
        public static int selectedText(Context c) {
            return ContextCompat.getColor(c, R.color.text_on_colored);
        }

        // cung cấp màu tên thứ khi đang được chọn
        public static int selectedDayName(Context c) {
            return ContextCompat.getColor(c, R.color.text_on_colored);
        }

        // lấy màu nhấn dành riêng cho ngày hiện tại
        public static int todayText(Context c) {
            return ContextCompat.getColor(c, R.color.today_accent);
        }

        // định dạng màu tên thứ cho ngày hôm nay
        public static int todayDayName(Context c) {
            return ContextCompat.getColor(c, R.color.today_accent);
        }

        // xác định màu chữ cho các ngày bình thường
        public static int normalText(Context c) {
            return ContextCompat.getColor(c, R.color.text_primary);
        }

        // lấy màu sắc tên thứ cho ngày không chọn
        public static int normalDayName(Context c) {
            return ContextCompat.getColor(c, R.color.text_secondary);
        }
    }

    public static final class AdapterHeader {
        // lấy màu nền nổi bật cho tiêu đề hôm nay
        public static int todayBackground(Context c) {
            return ContextCompat.getColor(c, R.color.today_background);
        }

        // cung cấp màu chữ tiêu đề cho ngày hiện tại
        public static int todayText(Context c) {
            return ContextCompat.getColor(c, R.color.today_accent);
        }

        // thiết lập màu nền trong suốt cho tiêu đề thường
        public static int normalBackground(Context c) {
            return ContextCompat.getColor(c, R.color.transparent);
        }

        // xác định màu chữ tiêu đề cho ngày bình thường
        public static int normalText(Context c) {
            return ContextCompat.getColor(c, R.color.text_secondary);
        }
    }

    public static final class Canvas {
        // lấy màu vẽ các đường kẻ lưới lịch tuần
        public static int gridLine(Context c) {
            return ContextCompat.getColor(c, R.color.canvas_grid_line);
        }

        // cung cấp màu cho vạch chỉ giờ hiện tại
        public static int nowLine(Context c) {
            return ContextCompat.getColor(c, R.color.canvas_now_line);
        }
    }

    public static final class ItemDrawer {
        // lấy màu chữ tiêu đề hiển thị trên nền màu
        public static int titleText(Context c) {
            return ContextCompat.getColor(c, R.color.text_on_colored);
        }

        // cung cấp màu chữ phụ hiển thị trên thẻ màu
        public static int subText(Context c) {
            return ContextCompat.getColor(c, R.color.text_on_colored);
        }

        // lấy màu nền đặc trưng cho tiết học lý thuyết
        public static int theoryBg(Context c) {
            return ContextCompat.getColor(c, R.color.schedule_theory);
        }

        // lấy màu nền đặc trưng cho tiết học thực hành
        public static int practiceBg(Context c) {
            return ContextCompat.getColor(c, R.color.schedule_practice);
        }
    }

    public static final class WeekHeader {
        // lấy màu nền tiêu đề tuần cho ngày hiện tại
        public static int todayBackground(Context c) {
            return ContextCompat.getColor(c, R.color.today_background);
        }

        // cung cấp màu viền bao quanh ngày hôm nay
        public static int todayStroke(Context c) {
            return ContextCompat.getColor(c, R.color.today_accent);
        }

        // xác định màu số ngày cho ngày hôm nay
        public static int todayDayNumber(Context c) {
            return ContextCompat.getColor(c, R.color.today_accent);
        }

        // lấy màu tên thứ cho ngày hôm nay
        public static int todayDayName(Context c) {
            return ContextCompat.getColor(c, R.color.today_accent);
        }

        // cung cấp màu số ngày cho các ngày khác
        public static int normalDayNumber(Context c) {
            return ContextCompat.getColor(c, R.color.text_primary);
        }

        // lấy màu tên thứ cho các ngày bình thường
        public static int normalDayName(Context c) {
            return ContextCompat.getColor(c, R.color.text_muted);
        }
    }

    public static final class DayManager {
        // lấy màu hiển thị văn bản ngày tháng đầy đủ
        public static int fullDateText(Context c) {
            return ContextCompat.getColor(c, R.color.text_primary);
        }
    }
}