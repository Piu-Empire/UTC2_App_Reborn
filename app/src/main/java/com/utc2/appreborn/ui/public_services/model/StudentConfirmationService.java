package com.utc2.appreborn.ui.public_services.model;

/**
 * StudentConfirmationService
 * ──────────────────────────────────────────────────────────────
 * Yêu cầu cấp giấy xác nhận sinh viên.
 *
 * Mapping: TABLE SERVICE_REQUEST
 *   service_type = BaseService.TYPE_CONFIRMATION  ("CONFIRMATION_LETTER")
 *
 * FIX: Cập nhật serviceType sang hằng tiếng Anh.
 * FIX: Thêm purpose (mục đích xin giấy) và quantity (số lượng bản).
 *
 * Dữ liệu sinh viên lấy từ:
 *   TABLE USER_PROFILE    → studentName  (full_name)
 *   TABLE STUDENT_PROFILE → studentCode  (student_code), className, faculty, major
 */
public class StudentConfirmationService extends BaseService {

    private String studentName;  // USER_PROFILE.full_name
    private String studentCode;  // STUDENT_PROFILE.student_code
    private String className;    // STUDENT_PROFILE.class_name
    private String faculty;      // STUDENT_PROFILE.faculty  ← THÊM MỚI
    private String purpose;      // mục đích xin giấy (VD: "xin học bổng", "vay vốn")  ← THÊM MỚI
    private int    quantity;     // số lượng bản cần cấp  ← THÊM MỚI

    /**
     * Constructor tương thích ngược.
     */
    public StudentConfirmationService(String title, String description,
                                      long submittedAt, String status, String serviceType,
                                      String studentName, String studentCode, String className) {
        super(title, description, submittedAt, status, serviceType);
        this.studentCode = studentCode;
        this.studentName = studentName;
        this.className   = className;
        this.quantity    = 1;
    }

    /**
     * Constructor đầy đủ.
     */
    public StudentConfirmationService(String title, String description,
                                      long submittedAt, String status,
                                      String studentName, String studentCode,
                                      String className, String faculty,
                                      String purpose, int quantity) {
        super(title, description, submittedAt, status, BaseService.TYPE_CONFIRMATION);
        this.studentName = studentName;
        this.studentCode = studentCode;
        this.className   = className;
        this.faculty     = faculty;
        this.purpose     = purpose;
        this.quantity    = quantity;
    }

    // ── Getters ──────────────────────────────────────────────

    public String getStudentName() { return studentName; }
    public String getStudentCode() { return studentCode; }
    public String getClassName()   { return className; }
    /** STUDENT_PROFILE.faculty. */
    public String getFaculty()     { return faculty; }
    /** Mục đích xin giấy xác nhận. */
    public String getPurpose()     { return purpose; }
    /** Số lượng bản cần cấp (mặc định 1). */
    public int    getQuantity()    { return quantity; }

    // ── Setters ──────────────────────────────────────────────

    public void setFaculty(String faculty)   { this.faculty = faculty; }
    public void setPurpose(String purpose)   { this.purpose = purpose; }
    public void setQuantity(int quantity)    { this.quantity = quantity; }
}