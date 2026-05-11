package com.utc2.appreborn.ui.public_services.model;

/**
 * LoanSupportService
 * ──────────────────────────────────────────────────────────────
 * Yêu cầu hỗ trợ vay vốn.
 *
 * Mapping: TABLE SERVICE_REQUEST
 *   service_type = BaseService.TYPE_LOAN_SUPPORT  ("LOAN_SUPPORT")
 *
 * FIX: Cập nhật serviceType sang hằng tiếng Anh.
 * FIX: Thêm idCardNumber và permanentAddress — bắt buộc cho hồ sơ vay vốn.
 *
 * Dữ liệu bổ sung lấy từ:
 *   TABLE USER_PROFILE    → phoneNumber    (phone_number)
 *   TABLE USER_PROFILE    → idCardNumber   (id_card_number)    ← THÊM MỚI
 *   TABLE USER_PROFILE    → permanentAddress (permanent_address) ← THÊM MỚI
 *   TABLE STUDENT_PROFILE → studentCode   (student_code)
 *
 * Lưu ý: loanAmount và loanReason nên lưu trong SERVICE_REQUEST.description
 *        dưới dạng JSON khi gọi API. Ví dụ:
 *        {"loan_amount": "10000000", "loan_reason": "Khó khăn tài chính"}
 */
public class LoanSupportService extends BaseService {

    private String studentCode;       // STUDENT_PROFILE.student_code
    private String loanAmount;        // số tiền vay (lưu trong description/JSON)
    private String loanReason;        // lý do vay  (lưu trong description/JSON)
    private String phoneNumber;       // USER_PROFILE.phone_number
    private String idCardNumber;      // USER_PROFILE.id_card_number  ← THÊM MỚI
    private String permanentAddress;  // USER_PROFILE.permanent_address  ← THÊM MỚI

    /**
     * Constructor tương thích ngược.
     */
    public LoanSupportService(String title, String description,
                              long submittedAt, String status, String serviceType,
                              String loanAmount, String loanReason, String phoneNumber) {
        super(title, description, submittedAt, status, serviceType);
        this.loanAmount  = loanAmount;
        this.loanReason  = loanReason;
        this.phoneNumber = phoneNumber;
    }

    /**
     * Constructor đầy đủ — bao gồm CCCD và địa chỉ thường trú.
     */
    public LoanSupportService(String title, String description,
                              long submittedAt, String status,
                              String studentCode, String loanAmount, String loanReason,
                              String phoneNumber, String idCardNumber, String permanentAddress) {
        super(title, description, submittedAt, status, BaseService.TYPE_LOAN_SUPPORT);
        this.studentCode      = studentCode;
        this.loanAmount       = loanAmount;
        this.loanReason       = loanReason;
        this.phoneNumber      = phoneNumber;
        this.idCardNumber     = idCardNumber;
        this.permanentAddress = permanentAddress;
    }

    // ── Getters ──────────────────────────────────────────────

    public String getStudentCode()      { return studentCode; }
    public String getLoanAmount()       { return loanAmount; }
    public String getLoanReason()       { return loanReason; }
    public String getPhoneNumber()      { return phoneNumber; }
    /** USER_PROFILE.id_card_number — CCCD/CMND, bắt buộc trong hồ sơ vay vốn. */
    public String getIdCardNumber()     { return idCardNumber; }
    /** USER_PROFILE.permanent_address — địa chỉ thường trú. */
    public String getPermanentAddress() { return permanentAddress; }

    // ── Setters ──────────────────────────────────────────────

    public void setStudentCode(String studentCode)           { this.studentCode = studentCode; }
    public void setIdCardNumber(String idCardNumber)         { this.idCardNumber = idCardNumber; }
    public void setPermanentAddress(String permanentAddress) { this.permanentAddress = permanentAddress; }
}