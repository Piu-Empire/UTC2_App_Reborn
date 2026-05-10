package com.utc2.appreborn.model;

/**
 * AcademicWarning - Model cho một cảnh báo học vụ.
 *
 * Fields:
 *  id           - ID cảnh báo
 *  title        - Tiêu đề cảnh báo (VD: "Điểm thi không đạt")
 *  subTitle     - Thông tin phụ (VD: tên môn học)
 *  date         - Ngày cảnh báo (VD: "18/04/2026")
 *  type         - Loại cảnh báo: SERIOUS hoặc NORMAL
 *  iconType     - Loại icon: BOOK hoặc CLOCK
 */
public class AcademicWarning {

    public static final int TYPE_SERIOUS = 1;  // NGHIÊM TRỌNG - nền đỏ
    public static final int TYPE_NORMAL = 2;   // Cảnh báo thường - nền vàng

    public static final int ICON_BOOK = 1;
    public static final int ICON_CLOCK = 2;

    private int id;
    private String title;
    private String subTitle;
    private String date;
    private int type;
    private int iconType;

    public AcademicWarning(int id, String title, String subTitle,
                           String date, int type, int iconType) {
        this.id = id;
        this.title = title;
        this.subTitle = subTitle;
        this.date = date;
        this.type = type;
        this.iconType = iconType;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getSubTitle() { return subTitle; }
    public String getDate() { return date; }
    public int getType() { return type; }
    public int getIconType() { return iconType; }

    public boolean isSerious() {
        return type == TYPE_SERIOUS;
    }
}
