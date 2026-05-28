package com.utc2.appreborn.ui.tuition.model;

/**
 * Tuition (abstract)
 * ──────────────────────────────────────────────────────────────
 * Lớp cha cho các loại học phí/lệ phí.
 *
 * Mapping TABLE FEE:
 *   fee_id           BIGINT PK AUTO_INCREMENT
 *   user_id          BIGINT FK → USER
 *   semester_id      BIGINT FK → SEMESTER
 *   total_amount     DECIMAL(15,2)
 *   paid_amount      DECIMAL(15,2) DEFAULT 0
 *   remaining_amount DECIMAL(15,2)  — computed: total - paid
 *   due_date         DATE
 *   status           VARCHAR(50)
 *   payment_method   VARCHAR(50)
 *   paid_at          TIMESTAMP
 *
 * FIX: totalAmount / paidAmount đổi từ long → double để khớp DECIMAL(15,2).
 */
public abstract class Tuition {

    // ── Hằng status — khớp với giá trị backend trả về ───────
    public static final String STATUS_UNPAID  = "chưa đóng";
    public static final String STATUS_PARTIAL = "đóng một phần";
    public static final String STATUS_PAID    = "đã đóng đủ";
    public static final String STATUS_OVERDUE = "quá hạn";

    // ── Fields ────────────────────────────────────────────────
    protected long   feeId;           // fee_id
    protected long   userId;          // user_id
    protected long   semesterId;      // semester_id
    protected double totalAmount;     // total_amount  DECIMAL(15,2)
    protected double paidAmount;      // paid_amount   DECIMAL(15,2)
    protected String dueDate;         // due_date  "yyyy-MM-dd"
    protected String status;          // status
    protected String paymentMethod;   // payment_method
    protected String paidAt;          // paid_at

    // ── Trường UI bổ sung (không có trong DB) ────────────────
    protected String name;            // tên hiển thị
    protected String details;         // mô tả / học kỳ

    public Tuition() {}

    public Tuition(long feeId, long userId, long semesterId,
                   double totalAmount, double paidAmount,
                   String dueDate, String status,
                   String paymentMethod, String paidAt) {
        this.feeId         = feeId;
        this.userId        = userId;
        this.semesterId    = semesterId;
        this.totalAmount   = totalAmount;
        this.paidAmount    = paidAmount;
        this.dueDate       = dueDate;
        this.status        = status;
        this.paymentMethod = paymentMethod;
        this.paidAt        = paidAt;
    }

    /** Mỗi lớp con cung cấp định danh riêng. */
    public abstract String getIdentifier();

    // ── Getters ───────────────────────────────────────────────
    public long   getFeeId()           { return feeId; }
    public long   getUserId()          { return userId; }
    public long   getSemesterId()      { return semesterId; }
    public double getTotalAmount()     { return totalAmount; }
    /** Alias của getTotalAmount() — dùng cho adapter hiển thị. */
    public double getAmount()          { return totalAmount; }
    public double getPaidAmount()      { return paidAmount; }
    /** remaining_amount = total_amount - paid_amount (computed). */
    public double getRemainingAmount() { return totalAmount - paidAmount; }
    public String getDueDate()         { return dueDate; }
    public String getStatus()          { return status; }
    public String getPaymentMethod()   { return paymentMethod; }
    public String getPaidAt()          { return paidAt; }
    public String getName()            { return name; }
    public String getDetails()         { return details; }

    // ── Setters ───────────────────────────────────────────────
    public void setFeeId(long v)           { this.feeId = v; }
    public void setUserId(long v)          { this.userId = v; }
    public void setSemesterId(long v)      { this.semesterId = v; }
    public void setTotalAmount(double v)   { this.totalAmount = v; }
    public void setPaidAmount(double v)    { this.paidAmount = v; }
    public void setDueDate(String v)       { this.dueDate = v; }
    public void setStatus(String v)        { this.status = v; }
    public void setPaymentMethod(String v) { this.paymentMethod = v; }
    public void setPaidAt(String v)        { this.paidAt = v; }
    public void setName(String v)          { this.name = v; }
    public void setDetails(String v)       { this.details = v; }

    /** Tiện ích: true nếu đã đóng đủ. */
    public boolean isPaid()    { return STATUS_PAID.equals(status); }
    /** Tiện ích: true nếu quá hạn. */
    public boolean isOverdue() { return STATUS_OVERDUE.equals(status); }
}