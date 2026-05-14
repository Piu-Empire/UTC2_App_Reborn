package com.utc2.appreborn.ui.public_services.model;

/**
 * LoanSupportService
 * ──────────────────────────────────────────────────────────────
 * Yêu cầu hỗ trợ vay vốn.
 *
 * Mapping: TABLE SERVICE_REQUEST
 *   service_type = BaseService.TYPE_LOAN_SUPPORT  ("LOAN_SUPPORT")
 *
 * Dữ liệu bổ sung lấy từ:
 *   TABLE USER_PROFILE    → phoneNumber  (phone_number)
 *   TABLE STUDENT_PROFILE → studentCode  (student_code)
 *
 * NOTE: idCardNumber và permanentAddress đã bị xóa — cột id_card_number
 *       và permanent_address không tồn tại trong schema TABLE USER_PROFILE.
 *
 * Lưu ý: loanAmount và loanReason nên lưu trong SERVICE_REQUEST.description
 *        dưới dạng JSON. Ví dụ:
 *        {"loan_amount": "10000000", "loan_reason": "Khó khăn tài chính"}
 */
public class LoanSupportService extends BaseService {

    private String studentCode;  // STUDENT_PROFILE.student_code
    private String loanAmount;   // lưu trong description JSON
    private String loanReason;   // lưu trong description JSON
    private String phoneNumber;  // USER_PROFILE.phone_number

    /**
     * Constructor duy nhất — 8 tham số.
     * Tham số thứ 5 là studentCode (thay thế serviceType ở code cũ).
     * serviceType luôn được gán = TYPE_LOAN_SUPPORT.
     */
    public LoanSupportService(String title, String description,
                              long submittedAt, String status,
                              String studentCode, String loanAmount,
                              String loanReason, String phoneNumber) {
        super(title, description, submittedAt, status, BaseService.TYPE_LOAN_SUPPORT);
        this.studentCode = studentCode;
        this.loanAmount  = loanAmount;
        this.loanReason  = loanReason;
        this.phoneNumber = phoneNumber;
    }

    // ── Getters ──────────────────────────────────────────────

    public String getStudentCode() { return studentCode; }
    public String getLoanAmount()  { return loanAmount; }
    public String getLoanReason()  { return loanReason; }
    public String getPhoneNumber() { return phoneNumber; }

    // ── Setters ──────────────────────────────────────────────

    public void setStudentCode(String studentCode) { this.studentCode = studentCode; }
    public void setLoanAmount(String loanAmount)   { this.loanAmount = loanAmount; }
    public void setLoanReason(String loanReason)   { this.loanReason = loanReason; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}