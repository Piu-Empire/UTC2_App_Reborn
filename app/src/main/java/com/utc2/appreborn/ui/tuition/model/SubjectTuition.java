package com.utc2.appreborn.ui.tuition.model;

/**
 * SubjectTuition
 * ──────────────────────────────────────────────────────────────
 * Đại diện một khoản học phí học phần.
 *
 * Mapping: TABLE FEE JOIN TABLE ENROLLMENT JOIN TABLE COURSE JOIN TABLE SEMESTER
 *
 * feeId        ↔ FEE.fee_id          PK
 * courseId     ↔ COURSE.course_id    FK  ← THÊM MỚI
 * courseCode   ↔ COURSE.course_code       ← THÊM MỚI
 * name         ↔ COURSE.course_name
 * credits      ↔ COURSE.credits           ← THÊM MỚI (int, không phải String)
 * courseType   ↔ COURSE.course_type  "THEORY"|"PRACTICE"|"MIXED"  ← THÊM MỚI
 * semesterName ↔ SEMESTER.semester_name   ← THÊM MỚI
 * details      ↔ credits + semesterName  (ghép để hiển thị)
 * totalAmount  ↔ FEE.total_amount
 * status       ↔ FEE.status
 */
public class SubjectTuition extends Tuition {

    // ── Hằng course_type ─────────────────────────────────────
    public static final String COURSE_THEORY   = "THEORY";
    public static final String COURSE_PRACTICE = "PRACTICE";
    public static final String COURSE_MIXED    = "MIXED";

    // ── Fields ────────────────────────────────────────────────
    private long   courseId;     // COURSE.course_id  ← THÊM MỚI
    private String courseCode;   // COURSE.course_code  ← THÊM MỚI
    private int    credits;      // COURSE.credits  ← THÊM MỚI
    private String courseType;   // COURSE.course_type  ← THÊM MỚI
    private String semesterName; // SEMESTER.semester_name  ← THÊM MỚI

    /**
     * Constructor tối giản — tương thích ngược với code cũ.
     *
     * @param feeId       FEE.fee_id
     * @param name        COURSE.course_name
     * @param details     số tín chỉ + học kỳ (hiển thị)
     * @param totalAmount FEE.total_amount
     * @param status      FEE.status — dùng hằng Tuition.STATUS_*
     */
    public SubjectTuition(long feeId, String name, String details,
                          long totalAmount, String status) {
        super(name, details, totalAmount, status);
        this.feeId     = feeId;
        this.feeType   = Tuition.TYPE_TUITION;
        this.courseType = COURSE_THEORY;
    }

    /**
     * Constructor đầy đủ — dùng khi map từ API / Room.
     */
    public SubjectTuition(long feeId, long userId,
                          long courseId, String courseCode, String courseName,
                          int credits, String courseType, String semesterName,
                          long totalAmount, long paidAmount,
                          String status, String dueDate,
                          String paymentMethod, String paidAt,
                          String receiptNumber, long semesterId) {
        super(feeId, userId, Tuition.TYPE_TUITION,
                courseName,
                credits + " TC — " + semesterName,   // details hiển thị
                totalAmount, paidAmount,
                status, dueDate, paymentMethod, paidAt,
                receiptNumber, semesterId);
        this.courseId    = courseId;
        this.courseCode  = courseCode;
        this.credits     = credits;
        this.courseType  = courseType;
        this.semesterName = semesterName;
    }

    @Override
    public String getIdentifier() {
        return "FEE-" + feeId;
    }

    // ── Getters ──────────────────────────────────────────────

    /** FEE.fee_id (PK). */
    public long   getFeeId()        { return feeId; }
    /** COURSE.course_id. */
    public long   getCourseId()     { return courseId; }
    /** COURSE.course_code (VD: "MATH101"). */
    public String getCourseCode()   { return courseCode; }
    /** COURSE.credits. */
    public int    getCredits()      { return credits; }
    /** COURSE.course_type — "THEORY" | "PRACTICE" | "MIXED". */
    public String getCourseType()   { return courseType; }
    /** SEMESTER.semester_name (VD: "Học kỳ 1 - 2023-2024"). */
    public String getSemesterName() { return semesterName; }

    // ── Setters ──────────────────────────────────────────────

    public void setCourseId(long courseId)         { this.courseId = courseId; }
    public void setCourseCode(String courseCode)   { this.courseCode = courseCode; }
    public void setCredits(int credits)            { this.credits = credits; }
    public void setCourseType(String courseType)   { this.courseType = courseType; }
    public void setSemesterName(String name)       { this.semesterName = name; }
}