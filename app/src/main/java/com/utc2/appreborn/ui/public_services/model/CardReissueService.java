package com.utc2.appreborn.ui.public_services.model;

/**
 * CardReissueService
 * ──────────────────────────────────────────────────────────────
 * Yêu cầu cấp lại thẻ sinh viên.
 *
 * Mapping: TABLE SERVICE_REQUEST
 *   service_type = BaseService.TYPE_CARD_REISSUE  ("CARD_REISSUE")
 *
 * Dữ liệu sinh viên lấy từ:
 *   TABLE USER_PROFILE    → studentName  (full_name)
 *   TABLE STUDENT_PROFILE → studentCode  (student_code), className (class_name)
 *
 * NOTE: idCardNumber đã bị xóa — cột id_card_number không tồn tại
 *       trong schema TABLE USER_PROFILE.
 *       Nếu cần CCCD/CMND, hãy thêm cột vào USER_PROFILE và cập nhật lại.
 */
public class CardReissueService extends BaseService {

    private String studentName;  // USER_PROFILE.full_name
    private String studentCode;  // STUDENT_PROFILE.student_code
    private String className;    // STUDENT_PROFILE.class_name

    /**
     * Constructor tương thích ngược.
     */
    public CardReissueService(String title, String description,
                              long submittedAt, String status, String serviceType,
                              String studentName, String studentCode, String className) {
        super(title, description, submittedAt, status, serviceType);
        this.studentName = studentName;
        this.studentCode = studentCode;
        this.className   = className;
    }

    /**
     * Constructor đầy đủ.
     */
    public CardReissueService(String title, String description,
                              long submittedAt, String status,
                              String studentName, String studentCode,
                              String className) {
        super(title, description, submittedAt, status, BaseService.TYPE_CARD_REISSUE);
        this.studentName = studentName;
        this.studentCode = studentCode;
        this.className   = className;
    }

    // ── Getters ──────────────────────────────────────────────

    public String getStudentName() { return studentName; }
    public String getStudentCode() { return studentCode; }
    public String getClassName()   { return className; }

    // ── Setters ──────────────────────────────────────────────

    public void setStudentName(String studentName) { this.studentName = studentName; }
    public void setStudentCode(String studentCode) { this.studentCode = studentCode; }
    public void setClassName(String className)     { this.className = className; }
}