package com.utc2.appreborn.ui.tuition.model;

/**
 * SubjectTuition
 * ──────────────────────────────────────────────────────────────
 * Đại diện một khoản học phí học phần.
 *
 * Mapping: TABLE FEE JOIN ENROLLMENT JOIN COURSE JOIN SEMESTER
 *
 * Từ COURSE:   course_id, course_code, course_name, credits
 * Từ SEMESTER: semester_name
 * Từ FEE:      fee_id, user_id, semester_id, total_amount, paid_amount,
 *              due_date, status, payment_method, paid_at
 *
 * FIX: totalAmount / paidAmount đổi từ long → double để khớp DECIMAL(15,2).
 */
public class SubjectTuition extends Tuition {

    // ── Fields từ COURSE + SEMESTER ──────────────────────────
    private long   courseId;      // COURSE.course_id
    private String courseCode;    // COURSE.course_code
    private int    credits;       // COURSE.credits
    private String semesterName;  // SEMESTER.semester_name

    /**
     * Constructor tối giản — dùng cho mock data trong Activity.
     */
    public SubjectTuition(long feeId, String courseName, String details,
                          double totalAmount, String status) {
        super();
        this.feeId       = feeId;
        this.name        = courseName;
        this.details     = details;
        this.totalAmount = totalAmount;
        this.paidAmount  = STATUS_PAID.equals(status) ? totalAmount : 0.0;
        this.status      = status;
    }

    /**
     * Constructor đầy đủ — dùng khi map từ API / Room.
     */
    public SubjectTuition(long feeId, long userId, long semesterId,
                          double totalAmount, double paidAmount,
                          String dueDate, String status,
                          String paymentMethod, String paidAt,
                          long courseId, String courseCode,
                          String courseName, int credits,
                          String semesterName) {
        super(feeId, userId, semesterId, totalAmount, paidAmount,
                dueDate, status, paymentMethod, paidAt);
        this.name         = courseName;
        this.details      = credits + " TC — " + semesterName;
        this.courseId     = courseId;
        this.courseCode   = courseCode;
        this.credits      = credits;
        this.semesterName = semesterName;
    }

    @Override
    public String getIdentifier() { return "FEE-" + feeId; }

    // ── Getters ──────────────────────────────────────────────
    public long   getCourseId()     { return courseId; }
    public String getCourseCode()   { return courseCode; }
    public int    getCredits()      { return credits; }
    public String getSemesterName() { return semesterName; }

    // ── Setters ──────────────────────────────────────────────
    public void setCourseId(long v)       { this.courseId = v; }
    public void setCourseCode(String v)   { this.courseCode = v; }
    public void setCredits(int v)         { this.credits = v; }
    public void setSemesterName(String v) { this.semesterName = v; }
}