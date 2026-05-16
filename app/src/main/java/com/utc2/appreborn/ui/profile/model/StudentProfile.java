package com.utc2.appreborn.ui.profile.model;

/**
 * StudentProfile
 * Cập nhật: Thêm trường studentCardUrl để hiển thị ảnh thẻ từ Backend.
 */
public class StudentProfile {

    // ── Hằng status ──────────────────────────────────────────
    public static final String STATUS_ACTIVE    = "ACTIVE";
    public static final String STATUS_GRADUATED = "GRADUATED";
    public static final String STATUS_SUSPENDED = "SUSPENDED";
    public static final String STATUS_RESERVED  = "RESERVED";

    // ── Fields ────────────────────────────────────────────────
    private long   userId;        // user_id (PK/FK)
    private String studentCode;   // student_code — MSSV
    private String faculty;       // faculty
    private Long   advisorId;     // advisor_id FK → ADVISOR (nullable)
    private String major;         // major
    private String academicYear;  // academic_year
    private String className;     // class_name
    private String status;        // status
    private String studentCardUrl; // <-- TRƯỜNG MỚI THÊM

    public StudentProfile() {}

    public StudentProfile(long userId, String studentCode, String faculty,
                          Long advisorId, String major, String academicYear,
                          String className, String status, String studentCardUrl) {
        this.userId       = userId;
        this.studentCode  = studentCode;
        this.faculty      = faculty;
        this.advisorId    = advisorId;
        this.major        = major;
        this.academicYear = academicYear;
        this.className    = className;
        this.status       = status;
        this.studentCardUrl = studentCardUrl;
    }

    // ── Getters ───────────────────────────────────────────────
    public long   getUserId()      { return userId; }
    public String getStudentCode() { return studentCode; }
    public String getFaculty()     { return faculty; }
    public Long   getAdvisorId()   { return advisorId; }
    public String getMajor()       { return major; }
    public String getAcademicYear(){ return academicYear; }
    public String getClassName()   { return className; }
    public String getStatus()      { return status; }
    public String getStudentCardUrl() { return studentCardUrl; }

    // ── Setters ───────────────────────────────────────────────
    public void setUserId(long v)        { this.userId = v; }
    public void setStudentCode(String v) { this.studentCode = v; }
    public void setFaculty(String v)     { this.faculty = v; }
    public void setAdvisorId(Long v)     { this.advisorId = v; }
    public void setMajor(String v)       { this.major = v; }
    public void setAcademicYear(String v){ this.academicYear = v; }
    public void setClassName(String v)   { this.className = v; }
    public void setStatus(String v)      { this.status = v; }
    public void setStudentCardUrl(String v) { this.studentCardUrl = v; }

    /** Tiện ích: true nếu đang theo học. */
    public boolean isActive()    { return STATUS_ACTIVE.equals(status); }
    /** Tiện ích: true nếu đã tốt nghiệp. */
    public boolean isGraduated() { return STATUS_GRADUATED.equals(status); }
}