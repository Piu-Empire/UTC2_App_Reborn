package com.utc2.appreborn.ui.public_services.model;

/**
 * TranscriptService
 * ──────────────────────────────────────────────────────────────
 * Yêu cầu cấp bảng điểm.
 *
 * Mapping: TABLE SERVICE_REQUEST
 *   service_type = BaseService.TYPE_TRANSCRIPT  ("TRANSCRIPT")
 *
 * FIX: Cập nhật serviceType sang hằng tiếng Anh.
 * FIX: Thêm semesterId để FK liên kết đúng SEMESTER.semester_id
 *      thay vì chỉ lưu string semesterName.
 *
 * Thông tin thêm lấy từ:
 *   TABLE STUDENT_PROFILE → studentCode, className, faculty, major
 *   TABLE USER_PROFILE    → studentName
 *   TABLE SEMESTER        → academicYear, semesterName, semesterId  ← semesterId THÊM MỚI
 *   quantity → lưu vào SERVICE_REQUEST.description (JSON) hoặc field riêng
 */
public class TranscriptService extends BaseService {

    private String studentName;   // USER_PROFILE.full_name
    private String studentCode;   // STUDENT_PROFILE.student_code
    private String className;     // STUDENT_PROFILE.class_name
    private String faculty;       // STUDENT_PROFILE.faculty  ← THÊM MỚI
    private String major;         // STUDENT_PROFILE.major  ← THÊM MỚI
    private String academicYear;  // SEMESTER.academic_year
    private String semester;      // SEMESTER.semester_name
    private long   semesterId;    // SEMESTER.semester_id  ← THÊM MỚI
    private int    quantity;      // số lượng bản in (String → int)  ← FIX kiểu dữ liệu

    /**
     * Constructor tương thích ngược.
     */
    public TranscriptService(String title, String description,
                             long submittedAt, String status, String serviceType,
                             String studentName, String studentCode, String className,
                             String academicYear, String semester, String quantity) {
        super(title, description, submittedAt, status, serviceType);
        this.studentCode  = studentCode;
        this.studentName  = studentName;
        this.className    = className;
        this.academicYear = academicYear;
        this.semester     = semester;
        this.quantity     = parseQuantity(quantity);
    }

    /**
     * Constructor đầy đủ.
     */
    public TranscriptService(String title, String description,
                             long submittedAt, String status,
                             String studentName, String studentCode,
                             String className, String faculty, String major,
                             String academicYear, String semester, long semesterId,
                             int quantity) {
        super(title, description, submittedAt, status, BaseService.TYPE_TRANSCRIPT);
        this.studentName  = studentName;
        this.studentCode  = studentCode;
        this.className    = className;
        this.faculty      = faculty;
        this.major        = major;
        this.academicYear = academicYear;
        this.semester     = semester;
        this.semesterId   = semesterId;
        this.quantity     = quantity;
    }

    // ── Getters ──────────────────────────────────────────────

    public String getStudentName()  { return studentName; }
    public String getStudentCode()  { return studentCode; }
    public String getClassName()    { return className; }
    /** STUDENT_PROFILE.faculty. */
    public String getFaculty()      { return faculty; }
    /** STUDENT_PROFILE.major. */
    public String getMajor()        { return major; }
    public String getAcademicYear() { return academicYear; }
    public String getSemester()     { return semester; }
    /** SEMESTER.semester_id (FK). */
    public long   getSemesterId()   { return semesterId; }
    /** Số lượng bản in (int). */
    public int    getQuantity()     { return quantity; }

    /** @deprecated Dùng getQuantity() (int). */
    @Deprecated
    public String getQuantityStr()  { return String.valueOf(quantity); }

    // ── Setters ──────────────────────────────────────────────

    public void setFaculty(String faculty)       { this.faculty = faculty; }
    public void setMajor(String major)           { this.major = major; }
    public void setSemesterId(long semesterId)   { this.semesterId = semesterId; }
    public void setQuantity(int quantity)        { this.quantity = quantity; }

    // ── Helper ───────────────────────────────────────────────

    private static int parseQuantity(String qty) {
        if (qty == null || qty.isEmpty()) return 1;
        try { return Integer.parseInt(qty.trim()); }
        catch (NumberFormatException e) { return 1; }
    }
}