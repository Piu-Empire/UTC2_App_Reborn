package com.utc2.appreborn.ui.courseregistration.model;

/**
 * Lớp trừu tượng cơ sở cho các mục trong đăng ký học phần.
 *
 * [Chương 3 - OOP]
 *  - Bao đóng: private fields, chỉ truy cập qua getters
 *  - Kế thừa: Course extends lớp này
 *  - Override: getDisplayInfo() là abstract → bắt buộc lớp con override
 */
public abstract class CourseItem {

    private final String id;
    private final String name;

    protected CourseItem(String id, String name) {
        this.id   = id;
        this.name = name;
    }

    public String getId()   { return id; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{ id='" + id + "', name='" + name + "' }";
    }

    /** Abstract: mỗi lớp con tự mô tả thông tin hiển thị. */
    public abstract String getDisplayInfo();
}
