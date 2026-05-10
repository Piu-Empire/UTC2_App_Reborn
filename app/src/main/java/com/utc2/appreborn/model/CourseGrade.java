package com.utc2.appreborn.model;

/**
 * CourseGrade - Model cho một môn học trong bảng điểm.
 *
 * Fields:
 *  courseCode    - Mã môn học (VD: "IT3040")
 *  courseName    - Tên môn học (VD: "Lập trình Java")
 *  credits       - Số tín chỉ
 *  midtermScore  - Điểm giữa kỳ (thang 10)
 *  finalScore    - Điểm cuối kỳ (thang 10)
 *  gpaScore      - Điểm tổng kết (thang 4)
 *  letterGrade   - Điểm chữ (A, B+, B, C+, C, D+, D, F)
 *  isPassed      - true nếu qua môn (D trở lên)
 *  semester      - Kỳ học (VD: "HK1 2024-2025")
 */
public class CourseGrade {

    private String courseCode;
    private String courseName;
    private int credits;
    private double midtermScore;
    private double finalScore;
    private double gpaScore;
    private String letterGrade;
    private boolean isPassed;
    private String semester;

    public CourseGrade(String courseCode, String courseName, int credits,
                       double midtermScore, double finalScore,
                       double gpaScore, String letterGrade,
                       boolean isPassed, String semester) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credits = credits;
        this.midtermScore = midtermScore;
        this.finalScore = finalScore;
        this.gpaScore = gpaScore;
        this.letterGrade = letterGrade;
        this.isPassed = isPassed;
        this.semester = semester;
    }

    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public int getCredits() { return credits; }
    public double getMidtermScore() { return midtermScore; }
    public double getFinalScore() { return finalScore; }
    public double getGpaScore() { return gpaScore; }
    public String getLetterGrade() { return letterGrade; }
    public boolean isPassed() { return isPassed; }
    public String getSemester() { return semester; }
}
