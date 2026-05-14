package com.utc2.appreborn.ui.public_services.model;

/**
 * CardReissueService
 * ──────────────────────────────────────────────────────────────
 * Yêu cầu cấp lại thẻ sinh viên.
 *
 * Mapping: TABLE SERVICE_REQUEST
 *   service_type = BaseService.TYPE_CARD_REISSUE ("CARD_REISSUE")
 *
 * Dữ liệu sinh viên lấy từ:
 *   TABLE USER_PROFILE
 *      → studentName   (full_name)
 *      → idCardNumber  (id_card_number)
 *
 *   TABLE STUDENT_PROFILE
 *      → studentCode   (student_code)
 *      → className     (class_name)
 *
 * NOTE:
 * Nếu schema USER_PROFILE chưa có cột id_card_number
 * thì cần thêm migration DB trước khi dùng field này.
 */
public class CardReissueService extends BaseService {

    // ── Student Info ─────────────────────────────────────────

    private String studentName;   // USER_PROFILE.full_name
    private String studentCode;   // STUDENT_PROFILE.student_code
    private String className;     // STUDENT_PROFILE.class_name

    // ── CCCD / CMND ──────────────────────────────────────────

    private String idCardNumber;  // USER_PROFILE.id_card_number

    /**
     * Constructor tương thích ngược.
     */
    public CardReissueService(
            String title,
            String description,
            long submittedAt,
            String status,
            String serviceType,
            String studentName,
            String studentCode,
            String className
    ) {

        super(title, description, submittedAt, status, serviceType);

        this.studentName = studentName;
        this.studentCode = studentCode;
        this.className   = className;
    }

    /**
     * Constructor mặc định dùng TYPE_CARD_REISSUE.
     */
    public CardReissueService(
            String title,
            String description,
            long submittedAt,
            String status,
            String studentName,
            String studentCode,
            String className
    ) {

        super(
                title,
                description,
                submittedAt,
                status,
                BaseService.TYPE_CARD_REISSUE
        );

        this.studentName = studentName;
        this.studentCode = studentCode;
        this.className   = className;
    }

    /**
     * Constructor đầy đủ — bao gồm CCCD/CMND.
     */
    public CardReissueService(
            String title,
            String description,
            long submittedAt,
            String status,
            String studentName,
            String studentCode,
            String className,
            String idCardNumber
    ) {

        super(
                title,
                description,
                submittedAt,
                status,
                BaseService.TYPE_CARD_REISSUE
        );

        this.studentName  = studentName;
        this.studentCode  = studentCode;
        this.className    = className;
        this.idCardNumber = idCardNumber;
    }

    // ── Getters ──────────────────────────────────────────────

    public String getStudentName() {
        return studentName;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public String getClassName() {
        return className;
    }

    /**
     * USER_PROFILE.id_card_number
     */
    public String getIdCardNumber() {
        return idCardNumber;
    }

    // ── Setters ──────────────────────────────────────────────

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setIdCardNumber(String idCardNumber) {
        this.idCardNumber = idCardNumber;
    }
}