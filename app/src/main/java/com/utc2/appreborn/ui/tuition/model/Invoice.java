package com.utc2.appreborn.ui.tuition.model;

/**
 * Invoice
 * ──────────────────────────────────────────────────────────────
 * Đại diện hóa đơn sau khi thanh toán — mapping với TABLE FEE.
 *
 * FIX NPE: Thêm totalAmount và paidAmount trực tiếp vào Invoice
 * để InvoiceAdapter không phụ thuộc vào tuition != null.
 * getTotalAmount() / getPaidAmount() ưu tiên field riêng,
 * fallback sang tuition nếu có.
 */
public class Invoice {

    private String  invoiceCode;   // mã hiển thị (VD: "UTC2_2026_001")
    private long    feeId;         // FEE.fee_id — PK
    private long    semesterId;    // FEE.semester_id
    private String  paidAt;        // FEE.paid_at  "yyyy-MM-dd'T'HH:mm:ss" hoặc ""
    private String  paymentMethod; // FEE.payment_method
    private Tuition tuition;       // nullable — đa hình, có thể null khi map từ API

    // FIX: field riêng để adapter hiển thị khi tuition == null
    private double  totalAmount;
    private double  paidAmount;

    /**
     * Constructor đầy đủ — dùng khi map từ API (tuition có thể null).
     */
    public Invoice(String invoiceCode, long feeId, long semesterId,
                   String paidAt, String paymentMethod, Tuition tuition) {
        this.invoiceCode   = invoiceCode;
        this.feeId         = feeId;
        this.semesterId    = semesterId;
        this.paidAt        = paidAt;
        this.paymentMethod = paymentMethod;
        this.tuition       = tuition;
        // sync amount từ tuition nếu có
        if (tuition != null) {
            this.totalAmount = tuition.getTotalAmount();
            this.paidAmount  = tuition.getPaidAmount();
        }
    }

    /**
     * Constructor với amount tường minh — dùng khi map từ TuitionResponse (tuition = null).
     */
    public Invoice(String invoiceCode, long feeId, long semesterId,
                   String paidAt, String paymentMethod,
                   double totalAmount, double paidAmount) {
        this.invoiceCode   = invoiceCode;
        this.feeId         = feeId;
        this.semesterId    = semesterId;
        this.paidAt        = paidAt;
        this.paymentMethod = paymentMethod;
        this.tuition       = null;
        this.totalAmount   = totalAmount;
        this.paidAmount    = paidAmount;
    }

    /**
     * Constructor tối giản — tương thích ngược.
     * @deprecated Dùng constructor đầy đủ.
     */
    @Deprecated
    public Invoice(String invoiceCode, String paidAt, Tuition tuition) {
        this(invoiceCode, -1L, -1L, paidAt, "", tuition);
    }

    // ── Getters ───────────────────────────────────────────────

    public String getInvoiceCode()   { return invoiceCode; }

    /** @deprecated Dùng {@link #getInvoiceCode()} */
    @Deprecated
    public String getInvoiceID()     { return invoiceCode; }

    public long   getFeeId()         { return feeId; }
    public long   getSemesterId()    { return semesterId; }
    public String getPaidAt()        { return paidAt; }

    /** @deprecated Dùng {@link #getPaidAt()} */
    @Deprecated
    public String getDate()          { return paidAt; }

    public String  getPaymentMethod() { return paymentMethod; }

    /** Có thể null khi Invoice được tạo từ API response trực tiếp. */
    public Tuition getTuition()       { return tuition; }

    /**
     * FIX: null-safe — trả field riêng nếu tuition == null.
     */
    public double getTotalAmount() {
        return tuition != null ? tuition.getTotalAmount() : totalAmount;
    }

    /**
     * FIX: null-safe — trả field riêng nếu tuition == null.
     */
    public double getPaidAmount() {
        return tuition != null ? tuition.getPaidAmount() : paidAmount;
    }

    // ── Setters ───────────────────────────────────────────────

    public void setTotalAmount(double v) { this.totalAmount = v; }
    public void setPaidAmount(double v)  { this.paidAmount  = v; }
}