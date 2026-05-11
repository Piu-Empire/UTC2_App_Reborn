package com.utc2.appreborn.ui.profile.model;

/**
 * StudentProfile
 * ──────────────────────────────────────────────────────────────
 * Model mapping TABLE STUDENT_PROFILE.
 * Chỉ tồn tại khi USER.role = "STUDENT".
 *
 * THÊM MỚI — file này chưa tồn tại trong project.
 * InfoFragment hiện đang lấy dữ liệu từ MockHelper. Model này cung cấp
 * POJO đúng schema để thay thế khi có Room / API.
 *
 * TABLE STUDENT_PROFILE:
 *   user_id               BIGINT PK/FK → USER
 *   student_code          VARCHAR(50)   MSSV — encode vào QR code
 *   faculty               VARCHAR(100)
 *   major                 VARCHAR(100)
 *   academic_year         VARCHAR(20)   VD: "K65", "2024"
 *   class_name            VARCHAR(50)   VD: "KTPM65A"
 *   status                VARCHAR(30)   "ACTIVE" | "GRADUATED" | "SUSPENDED" | "RESERVED"
 *   advisor_id            BIGINT FK → ADVISOR
 *   enrollment_year       YEAR
 *   expected_graduation   DATE
 *   cumulative_gpa        DECIMAL(4,2)
 *   total_credits_passed  INT
 *
 * LƯU Ý: full_name KHÔNG có trong bảng này (đã ở USER_PROFILE.full_name).
 *        Schema docx đã ghi rõ đây là trường dư thừa cần xoá.
 */
public class StudentProfile {

    // ── Hằng status ──────────────────────────────────────────
    public static final String STATUS_ACTIVE    = "ACTIVE";
    public static final String STATUS_GRADUATED = "GRADUATED";
    public static final String STATUS_SUSPENDED = "SUSPENDED";
    public static final String STATUS_RESERVED  = "RESERVED";

    // ── Fields ────────────────────────────────────────────────
    private long   userId;               // STUDENT_PROFILE.user_id (PK/FK)
    private String studentCode;          // STUDENT_PROFILE.student_code (MSSV)
    private String faculty;              // STUDENT_PROFILE.faculty
    private String major;                // STUDENT_PROFILE.major
    private String academicYear;         // STUDENT_PROFILE.academic_year
    private String className;            // STUDENT_PROFILE.class_name
    private String status;               // STUDENT_PROFILE.status
    private long   advisorId;            // STUDENT_PROFILE.advisor_id (FK → ADVISOR)
    private int    enrollmentYear;       // STUDENT_PROFILE.enrollment_year
    private String expectedGraduation;   // STUDENT_PROFILE.expected_graduation  "yyyy-MM-dd"
    private double cumulativeGpa;        // STUDENT_PROFILE.cumulative_gpa
    private int    totalCreditsPassed;   // STUDENT_PROFILE.total_credits_passed

    public StudentProfile() {}

    public StudentProfile(long userId, String studentCode, String faculty, String major,
                          String academicYear, String className, String status, long advisorId,
                          int enrollmentYear, String expectedGraduation,
                          double cumulativeGpa, int totalCreditsPassed) {
        this.userId              = userId;
        this.studentCode         = studentCode;
        this.faculty             = faculty;
        this.major               = major;
        this.academicYear        = academicYear;
        this.className           = className;
        this.status              = status;
        this.advisorId           = advisorId;
        this.enrollmentYear      = enrollmentYear;
        this.expectedGraduation  = expectedGraduation;
        this.cumulativeGpa       = cumulativeGpa;
        this.totalCreditsPassed  = totalCreditsPassed;
    }

    // ── Getters ──────────────────────────────────────────────

    public long   getUserId()              { return userId; }
    /** MSSV — encode vào QR code. */
    public String getStudentCode()         { return studentCode; }
    public String getFaculty()             { return faculty; }
    public String getMajor()               { return major; }
    /** Khoá học, VD: "K65", "2024". */
    public String getAcademicYear()        { return academicYear; }
    /** Lớp hành chính, VD: "KTPM65A". */
    public String getClassName()           { return className; }
    public String getStatus()              { return status; }
    public long   getAdvisorId()           { return advisorId; }
    public int    getEnrollmentYear()      { return enrollmentYear; }
    /** Ngày dự kiến tốt nghiệp "yyyy-MM-dd". */
    public String getExpectedGraduation()  { return expectedGraduation; }
    /** GPA tích luỹ toàn khoá học (thang 4.0). */
    public double getCumulativeGpa()       { return cumulativeGpa; }
    /** Tổng tín chỉ đã tích luỹ (qua môn). */
    public int    getTotalCreditsPassed()  { return totalCreditsPassed; }

    // ── Setters ──────────────────────────────────────────────

    public void setUserId(long userId)                         { this.userId = userId; }
    public void setStudentCode(String studentCode)             { this.studentCode = studentCode; }
    public void setFaculty(String faculty)                     { this.faculty = faculty; }
    public void setMajor(String major)                         { this.major = major; }
    public void setAcademicYear(String academicYear)           { this.academicYear = academicYear; }
    public void setClassName(String className)                 { this.className = className; }
    public void setStatus(String status)                       { this.status = status; }
    public void setAdvisorId(long advisorId)                   { this.advisorId = advisorId; }
    public void setEnrollmentYear(int enrollmentYear)          { this.enrollmentYear = enrollmentYear; }
    public void setExpectedGraduation(String expectedGrad)     { this.expectedGraduation = expectedGrad; }
    public void setCumulativeGpa(double cumulativeGpa)         { this.cumulativeGpa = cumulativeGpa; }
    public void setTotalCreditsPassed(int totalCreditsPassed)  { this.totalCreditsPassed = totalCreditsPassed; }

    /** Tiện ích: true nếu đang theo học. */
    public boolean isActive()     { return STATUS_ACTIVE.equals(status); }
    /** Tiện ích: true nếu đã tốt nghiệp. */
    public boolean isGraduated()  { return STATUS_GRADUATED.equals(status); }
}