package com.utc2.appreborn.ui.public_services.model;

/**
 * CardReissueService
 * ──────────────────────────────────────────────────────────────
 * Yêu cầu cấp lại thẻ sinh viên.
 *
 * Mapping: TABLE SERVICE_REQUEST
 *   service_type = BaseService.TYPE_CARD_REISSUE  ("CARD_REISSUE")
 *
 * FIX: Cập nhật serviceType sang hằng tiếng Anh (BaseService.TYPE_CARD_REISSUE).
 *
 * Dữ liệu sinh viên lấy từ:
 *   TABLE USER_PROFILE    → studentName  (full_name)
 *   TABLE STUDENT_PROFILE → studentCode  (student_code), className (class_name)
 *   TABLE USER_PROFILE    → idCardNumber (id_card_number)  ← THÊM MỚI — cần cho dịch vụ hành chính
 */
public class CardReissueService extends BaseService {

    private String studentName;   // USER_PROFILE.full_name
    private String studentCode;   // STUDENT_PROFILE.student_code
    private String className;     // STUDENT_PROFILE.class_name
    private String idCardNumber;  // USER_PROFILE.id_card_number (CCCD/CMND)  ← THÊM MỚI

    /**
     * Constructor tương thích ngược.
     */

    /**
     * Constructor đầy đủ — bao gồm CCCD/CMND.
     *
     * @param idCardNumber USER_PROFILE.id_card_number — bắt buộc cho cấp lại thẻ
     */
    public CardReissueService(String title, String description,
                              long submittedAt, String status,
                              String studentName, String studentCode,
                              String className, String idCardNumber) {
        super(title, description, submittedAt, status, BaseService.TYPE_CARD_REISSUE);
        this.studentName  = studentName;
        this.studentCode  = studentCode;
        this.className    = className;
        this.idCardNumber = idCardNumber;
    }

    // ── Getters ──────────────────────────────────────────────

    public String getStudentName()  { return studentName; }
    public String getStudentCode()  { return studentCode; }
    public String getClassName()    { return className; }
    /** USER_PROFILE.id_card_number — CCCD/CMND để xác minh khi cấp thẻ. */
    public String getIdCardNumber() { return idCardNumber; }

    // ── Setters ──────────────────────────────────────────────

    public void setIdCardNumber(String idCardNumber) { this.idCardNumber = idCardNumber; }
}