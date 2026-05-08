package com.utc2.appreborn.utils;

import android.graphics.Paint;
import android.text.TextPaint;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TextHelper {

    // Khởi tạo từ điển mặc định một lần duy nhất để tối ưu bộ nhớ
    private static final Map<String, String> DEFAULT_DICT;

    static {
        Map<String, String> dict = new LinkedHashMap<>();
        // ===== GIỮ NGẮN GỌN =====
        dict.put("Kỹ năng mềm", "KN mềm");
        dict.put("Những nguyên lý cơ bản của chủ nghĩa Mác- Lê Nin", "Ng.lý Mác-Lênin");
        dict.put("Tư tưởng Hồ Chí Minh", "TT HCM");
        dict.put("Đường lối cách mạng Đảng Cộng sản Việt Nam", "Đ.lối Đảng");

        dict.put("Giáo dục thể chất", "GDTC");
        dict.put("Giáo dục QP-AN", "QP-AN");

        dict.put("Tiếng Anh B1", "TA B1");
        dict.put("Tiếng Pháp B1", "Pháp B1");
        dict.put("Tiếng Nga B1", "Nga B1");

        dict.put("Ngoại ngữ chuyên ngành", "NN CN");

// ===== TOÁN =====
        dict.put("Đại số tuyến tính", "ĐS tuyến tính");
        dict.put("Giải tích 1", "GT 1");
        dict.put("Giải tích 2", "GT 2");
        dict.put("Giải tích số", "GT số");
        dict.put("Toán rời rạc", "Toán rời rạc");
        dict.put("Xác suất thống kê", "XS thống kê");

// ===== CƠ BẢN =====
        dict.put("Tin học đại cương", "Tin học ĐC");
        dict.put("Vật lý điện từ", "VL điện từ");

// ===== LẬP TRÌNH =====
        dict.put("Lập trình nâng cao", "LT nâng cao");
        dict.put("Lập trình hướng đối tượng", "LT OOP");
        dict.put("Lập trình trực quan", "LT trực quan");
        dict.put("Lập trình mạng", "LT mạng");
        dict.put("Lập trình sử dụng API", "LT API");
        dict.put("Lập trình thiết bị di động", "LT mobile");

        dict.put("Lập trình Web", "LT Web");
        dict.put("Thiết kế Web", "TK Web");

        dict.put("Công nghệ Java", "CN Java");

// ===== HỆ THỐNG =====
        dict.put("Hệ điều hành", "HĐH");
        dict.put("Hệ điều hành Unix", "HĐH Unix");

        dict.put("Kiến trúc và tổ chức máy tính", "KT & TC máy tính");

        dict.put("Mạng máy tính", "Mạng MT");
        dict.put("Quản trị mạng", "QT mạng");
        dict.put("An ninh mạng", "AN mạng");

// ===== DỮ LIỆU =====
        dict.put("Cơ sở dữ liệu", "CSDL");
        dict.put("Thiết kế cơ sở dữ liệu", "TK CSDL");

        dict.put("Khai phá dữ liệu", "Khai phá DL");

// ===== THUẬT TOÁN =====
        dict.put("Cấu trúc dữ liệu và giải thuật", "CTDL & GT");
        dict.put("Phân tích thiết kế thuật toán", "PTTK thuật toán");
        dict.put("Thuật toán và ứng dụng", "Thuật toán UD");

// ===== PHẦN MỀM =====
        dict.put("Công nghệ phần mềm", "CNPM");
        dict.put("Phân tích thiết kế hệ thống", "PTTK hệ thống");
        dict.put("Phân tích thiết kế hướng đối tượng", "PTTK HĐT");
        dict.put("Đặc tả phần mềm", "Đặc tả PM");

// ===== AI =====
        dict.put("Trí tuệ nhân tạo", "TT nhân tạo");
        dict.put("Xử lý ảnh", "XL ảnh");
        dict.put("Hệ thông tin địa lý", "HTTT địa lý");
        dict.put("Lý thuyết trò chơi và ứng dụng", "LT trò chơi");

// ===== KHÁC =====
        dict.put("Thực tập chuyên môn", "TT chuyên môn");
        dict.put("Thực tập tốt nghiệp", "TT tốt nghiệp");
        dict.put("Đồ án tốt nghiệp", "ĐA tốt nghiệp");

        dict.put("Chuyên đề công nghệ phần mềm", "CD CNPM");
        dict.put("Chuyên đề Hệ thống thông tin", "CD HTTT");
        dict.put("Chuyên đề Mạng máy tính", "CD MMT");
        dict.put("Chuyên đề Khoa học máy tính", "CD KHMT");
        dict.put("Chuyên đề Công nghệ thông tin", "CD CNTT");

        DEFAULT_DICT = Collections.unmodifiableMap(dict);
    }

    /**
     * Tự động xuống dòng dựa trên chiều rộng tối đa cho phép
     */
    public static List<String> wrapText(String text, float maxWidth, TextPaint textPaint) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) return lines;

        String[] words = text.split("\\s+"); // Split theo khoảng trắng (bao gồm cả tab, xuống dòng)
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;

            if (textPaint.measureText(testLine) <= maxWidth) {
                if (currentLine.length() > 0) currentLine.append(" ");
                currentLine.append(word);
            } else {
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                }
                currentLine = new StringBuilder(word);
            }
        }

        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }
        return lines;
    }

    /**
     * Kiểm tra xem chữ có vừa không khi xoay dọc 90 độ
     */
    public static boolean canFitRotated(String text, float cellWidth, float cellHeight, TextPaint textPaint) {
        if (text == null) return false;

        float textWidth = textPaint.measureText(text);
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        float textHeight = Math.abs(fm.bottom - fm.top);

        // Xoay 90 độ:
        // Chiều dài chữ (textWidth) phải nhỏ hơn chiều cao ô (cellHeight)
        // Độ dày chữ (textHeight) phải nhỏ hơn chiều rộng ô (cellWidth)
        return textWidth <= cellHeight && textHeight <= cellWidth;
    }

    /**
     * Viết tắt tên môn học dựa trên từ điển
     */
    public static String abbreviate(String text) {
        if (text == null || text.isEmpty()) return "";

        String result = text;
        // Duyệt qua toàn bộ từ điển để thay thế tất cả các cụm từ khớp
        for (Map.Entry<String, String> entry : DEFAULT_DICT.entrySet()) {
            String longName = entry.getKey();
            String shortName = entry.getValue();
            if (result.toLowerCase().contains(longName.toLowerCase())) {
                result = result.replaceAll("(?i)" + java.util.regex.Pattern.quote(longName), shortName);
            }
        }

        return result;
    }
}