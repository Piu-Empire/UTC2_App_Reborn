package com.utc2.appreborn.ui.tuition.model;

/**
 * Invoice
 * ──────────────────────────────────────────────────────────────
 * Đại diện hóa đơn sau khi thanh toán — mapping với TABLE FEE.
 *
 * MySQL schema (TABLE FEE):
 *   fee_id         BIGINT PK
 *   user_id        BIGINT FK → USER
 *   semester_id    BIGINT FK → SEMESTER
 *   total_amount   DECIMAL(15,2)
 *   paid_amount    DECIMAL(15,2)
 *   remaining_amount DECIMAL(15,2)
 *   due_date       DATE
 *   status         VARCHAR(50)  "chưa đóng" | "đóng một phần" | "đã đóng đủ"
 *   payment_method VARCHAR(50)  "QR Code" | "Chuyển khoản" | "Tiền mặt"
 *   paid_at        TIMESTAMP
 */
public class Invoice {

    private String invoiceCode;   // mã hiển thị cho người dùng (VD: "UTC2_2026_001")
    private long   feeId;         // FEE.fee_id — PK
    private long   semesterId;    // FEE.semester_id
    private String paidAt;        // FEE.paid_at  "dd/MM/yyyy HH:mm"
    private String paymentMethod; // FEE.payment_method
    private Tuition tuition;      // đối tượng học phí được thanh toán (đa hình)

    /**
     * Constructor đầy đủ.
     */
    public Invoice(String invoiceCode, long feeId, long semesterId,
                   String paidAt, String paymentMethod, Tuition tuition) {
        this.invoiceCode   = invoiceCode;
        this.feeId         = feeId;
        this.semesterId    = semesterId;
        this.paidAt        = paidAt;
        this.paymentMethod = paymentMethod;
        this.tuition       = tuition;
    }

    /**
     * Constructor tối giản — tương thích với code cũ dùng (invoiceID, date, tuition).
     *
     * @deprecated Dùng constructor đầy đủ để map đúng schema DB.
     */
    @Deprecated
    public Invoice(String invoiceCode, String paidAt, Tuition tuition) {
        this(invoiceCode, -1L, -1L, paidAt, "", tuition);
    }

    // ── Getters ───────────────────────────────────────────────

    /** Mã hóa đơn hiển thị (không phải FEE.fee_id) */
    public String getInvoiceCode()   { return invoiceCode; }

    /** @deprecated Dùng {@link #getInvoiceCode()} */
    @Deprecated
    public String getInvoiceID()     { return invoiceCode; }

    /** FEE.fee_id */
    public long getFeeId()           { return feeId; }

    /** FEE.semester_id */
    public long getSemesterId()      { return semesterId; }

    /** FEE.paid_at */
    public String getPaidAt()        { return paidAt; }

    /** @deprecated Dùng {@link #getPaidAt()} */
    @Deprecated
    public String getDate()          { return paidAt; }

    /** FEE.payment_method */
    public String getPaymentMethod() { return paymentMethod; }

    /** Đối tượng học phí (SubjectTuition hoặc DormTuition) */
    public Tuition getTuition()      { return tuition; }

    /** Lấy số tiền đã đóng từ Tuition */
    public long getPaidAmount()      { return tuition.getPaidAmount(); }

    /** Lấy tổng tiền từ Tuition */
    public long getTotalAmount()     { return tuition.getTotalAmount(); }
}