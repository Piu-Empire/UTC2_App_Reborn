// PATH: app/src/main/java/com/utc2/appreborn/model/AssessmentPeriod.java

package com.utc2.appreborn.model;

/**
 * Đại diện cho một đợt/học kỳ đánh giá.
 * Dùng trong Spinner chọn học kỳ trên màn hình Đánh giá.
 */
public class AssessmentPeriod {

    private final String id;    // Mã định danh, ví dụ: "HK1_2024_2025"
    private final String label; // Chuỗi hiển thị: "Học kỳ 1 – 2024-2025"

    public AssessmentPeriod(String id, String label) {
        this.id    = id;
        this.label = label;
    }

    public String getId()    { return id; }
    public String getLabel() { return label; }

    /** Trả về label khi được dùng trong ArrayAdapter hoặc toString() */
    @Override
    public String toString() { return label; }
}