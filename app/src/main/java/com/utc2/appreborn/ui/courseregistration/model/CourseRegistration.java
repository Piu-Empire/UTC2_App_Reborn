package com.utc2.appreborn.ui.courseregistration.model;

/**
 * Lớp đại diện cho một lượt đăng ký học phần.
 *
 * [Chương 3 - OOP]
 *  - Kế thừa: extends CourseItem
 *  - Bao đóng: private fields
 *  - Override: getDisplayInfo() từ CourseItem
 */
public class CourseRegistration extends CourseItem {

    private final Course course;
    private       String status; // "PENDING" / "CONFIRMED" / "CANCELLED"

    public CourseRegistration(String id, Course course) {
        super(id, "Đăng ký: " + course.getName());
        this.course = course;
        this.status = "PENDING";
    }

    public Course getCourse()  { return course; }
    public String getStatus()  { return status; }
    public void   setStatus(String status) { this.status = status; }

    @Override
    public String getDisplayInfo() {
        return course.getName()
                + " | " + course.getCredits() + " tín chỉ"
                + " | " + status;
    }
}
