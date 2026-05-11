package com.utc2.appreborn.ui.tuition.model;

/**
 * Tuition (abstract)
 * ──────────────────────────────────────────────────────────────
 * Lớp cha cho các loại học phí/lệ phí trong ứng dụng.
 *
 * Mapping với TABLE FEE (MySQL schema):
 *   feeId          ↔ FEE.fee_id            PK                  ← THÊM MỚI
 *   userId         ↔ FEE.user_id           FK → USER           ← THÊM MỚI
 *   feeType        ↔ FEE.fee_type          "TUITION"|"DORMITORY"|"INSURANCE"|"OTHER"  ← THÊM MỚI
 *   name           ↔ (tên hiển thị — ghép từ feeType + semester_name)
 *   details        ↔ (mô tả — semester_name từ JOIN SEMESTER)
 *   totalAmount    ↔ FEE.total_amount      DECIMAL(15,2) → long VND
 *   paidAmount     ↔ FEE.paid_amount
 *   remainingAmount↔ FEE.remaining_amount  (computed: total - paid)
 *   dueDate        ↔ FEE.due_date          "yyyy-MM-dd"
 *   status         ↔ FEE.status            "UNPAID"|"PARTIAL"|"PAID"|"OVERDUE"  ← SỬA (khớp DB)
 *   paymentMethod  ↔ FEE.payment_method    "BANK_TRANSFER"|"CASH"|"MOMO"|...
 *   paidAt         ↔ FEE.paid_at           TIMESTAMP → String  ← THÊM MỚI
 *   receiptNumber  ↔ FEE.receipt_number                        ← THÊM MỚI
 *   semesterId     ↔ FEE.semester_id       FK → SEMESTER
 *
 * FIX: Hằng STATUS đổi sang tiếng Anh cho khớp với DB schema.
 *      Code cũ dùng "chưa đóng" / "đóng một phần" / "đã đóng đủ" —
 *      giữ lại @Deprecated để tương thích ngược, sẽ xoá sau khi migrate.
 */
public abstract class Tuition {

    // ── Hằng fee_type ─────────────────────────────────────────
    public static final String TYPE_TUITION   = "TUITION";
    public static final String TYPE_DORMITORY = "DORMITORY";
    public static final String TYPE_INSURANCE = "INSURANCE";
    public static final String TYPE_OTHER     = "OTHER";

    // ── Hằng status — khớp với FEE.status trong DB ────────────
    public static final String STATUS_UNPAID  = "UNPAID";
    public static final String STATUS_PARTIAL = "PARTIAL";
    public static final String STATUS_PAID    = "PAID";
    public static final String STATUS_OVERDUE = "OVERDUE";

    /**
     * @deprecated Dùng STATUS_UNPAID. Sẽ xoá sau khi toàn bộ màn hình migrate.
     */
    @Deprecated public static final String STATUS_UNPAID_VI  = "chưa đóng";
    @Deprecated public static final String STATUS_PARTIAL_VI = "đóng một phần";
    @Deprecated public static final String STATUS_PAID_VI    = "đã đóng đủ";

    // ── Fields ────────────────────────────────────────────────
    protected long   feeId;          // FEE.fee_id  (PK)  ← THÊM MỚI
    protected long   userId;         // FEE.user_id (FK → USER)  ← THÊM MỚI
    protected String feeType;        // FEE.fee_type  ← THÊM MỚI
    protected String name;           // tên hiển thị
    protected String details;        // mô tả / học kỳ
    protected long   totalAmount;    // FEE.total_amount
    protected long   paidAmount;     // FEE.paid_amount
    protected String status;         // FEE.status
    protected String dueDate;        // FEE.due_date  "yyyy-MM-dd"
    protected String paymentMethod;  // FEE.payment_method
    protected String paidAt;         // FEE.paid_at  "dd/MM/yyyy HH:mm"  ← THÊM MỚI
    protected String receiptNumber;  // FEE.receipt_number  ← THÊM MỚI
    protected long   semesterId;     // FEE.semester_id

    // ── Constructor tối giản ──────────────────────────────────

    public Tuition(String name, String details, long totalAmount, String status) {
        this.name        = name;
        this.details     = details;
        this.totalAmount = totalAmount;
        this.paidAmount  = STATUS_PAID.equals(status) ? totalAmount : 0L;
        this.status      = status;
        this.feeType     = TYPE_TUITION;
    }

    // ── Constructor đầy đủ ────────────────────────────────────

    public Tuition(long feeId, long userId, String feeType,
                   String name, String details,
                   long totalAmount, long paidAmount,
                   String status, String dueDate,
                   String paymentMethod, String paidAt,
                   String receiptNumber, long semesterId) {
        this.feeId         = feeId;
        this.userId        = userId;
        this.feeType       = feeType;
        this.name          = name;
        this.details       = details;
        this.totalAmount   = totalAmount;
        this.paidAmount    = paidAmount;
        this.status        = status;
        this.dueDate       = dueDate;
        this.paymentMethod = paymentMethod;
        this.paidAt        = paidAt;
        this.receiptNumber = receiptNumber;
        this.semesterId    = semesterId;
    }

    /**
     * Constructor tương thích ngược — giữ cho SubjectTuition / DormTuition cũ.
     *
     * @deprecated Dùng constructor đầy đủ.
     */
    @Deprecated
    public Tuition(String name, String details,
                   long totalAmount, long paidAmount,
                   String status, String dueDate,
                   String paymentMethod, long semesterId) {
        this.name          = name;
        this.details       = details;
        this.totalAmount   = totalAmount;
        this.paidAmount    = paidAmount;
        this.status        = status;
        this.dueDate       = dueDate;
        this.paymentMethod = paymentMethod;
        this.semesterId    = semesterId;
        this.feeType       = TYPE_TUITION;
    }

    /** Mỗi lớp con phải cung cấp định danh riêng (dùng cho search/filter DB). */
    public abstract String getIdentifier();

    // ── Getters ──────────────────────────────────────────────

    /** FEE.fee_id (PK). */
    public long   getFeeId()           { return feeId; }
    /** FEE.user_id (FK → USER). */
    public long   getUserId()          { return userId; }
    /** FEE.fee_type — "TUITION" | "DORMITORY" | "INSURANCE" | "OTHER". */
    public String getFeeType()         { return feeType; }
    public String getName()            { return name; }
    public String getDetails()         { return details; }
    public long   getAmount()          { return totalAmount; }
    public long   getTotalAmount()     { return totalAmount; }
    public long   getPaidAmount()      { return paidAmount; }
    public long   getRemainingAmount() { return totalAmount - paidAmount; }
    /** FEE.status — "UNPAID" | "PARTIAL" | "PAID" | "OVERDUE". */
    public String getStatus()          { return status; }
    public String getDueDate()         { return dueDate; }
    public String getPaymentMethod()   { return paymentMethod; }
    /** FEE.paid_at — "dd/MM/yyyy HH:mm", null nếu chưa đóng. */
    public String getPaidAt()          { return paidAt; }
    /** FEE.receipt_number — mã biên lai sau khi thanh toán. */
    public String getReceiptNumber()   { return receiptNumber; }
    public long   getSemesterId()      { return semesterId; }

    /** Tiện ích: true nếu đã đóng đủ. */
    public boolean isPaid()    { return STATUS_PAID.equals(status); }
    /** Tiện ích: true nếu quá hạn. */
    public boolean isOverdue() { return STATUS_OVERDUE.equals(status); }

    // ── Setters ──────────────────────────────────────────────

    public void setFeeId(long feeId)               { this.feeId = feeId; }
    public void setUserId(long userId)             { this.userId = userId; }
    public void setFeeType(String feeType)         { this.feeType = feeType; }
    public void setStatus(String status)           { this.status = status; }
    public void setPaidAmount(long paidAmount)     { this.paidAmount = paidAmount; }
    public void setPaymentMethod(String method)    { this.paymentMethod = method; }
    public void setPaidAt(String paidAt)           { this.paidAt = paidAt; }
    public void setReceiptNumber(String receipt)   { this.receiptNumber = receipt; }
}