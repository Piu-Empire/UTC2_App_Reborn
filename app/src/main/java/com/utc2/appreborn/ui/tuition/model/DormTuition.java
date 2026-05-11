package com.utc2.appreborn.ui.tuition.model;

/**
 * DormTuition
 * ──────────────────────────────────────────────────────────────
 * Đại diện khoản phí ký túc xá.
 *
 * Mapping: TABLE DORMITORY_REGISTRATION JOIN TABLE DORMITORY_ROOM JOIN TABLE FEE
 *
 * dormRegId    ↔ DORMITORY_REGISTRATION.dorm_reg_id  PK
 * roomId       ↔ DORMITORY_REGISTRATION.room_id      FK → DORMITORY_ROOM
 * building     ↔ DORMITORY_ROOM.building              ← THÊM MỚI
 * pricePerMonth↔ DORMITORY_ROOM.price_per_month       ← THÊM MỚI
 * startDate    ↔ DORMITORY_REGISTRATION.start_date    ← THÊM MỚI
 * endDate      ↔ DORMITORY_REGISTRATION.end_date      ← THÊM MỚI
 * approvedBy   ↔ DORMITORY_REGISTRATION.approved_by   ← THÊM MỚI
 * approvedAt   ↔ DORMITORY_REGISTRATION.approved_at   ← THÊM MỚI
 * regStatus    ↔ DORMITORY_REGISTRATION.status  "PENDING"|"APPROVED"|"REJECTED"|"CHECKED_OUT"  ← THÊM MỚI
 *
 * FIX: parent status (FEE.status) và regStatus (DORMITORY_REGISTRATION.status) là 2 trường khác nhau.
 *      Code cũ dùng paidStatus từ DORMITORY_REGISTRATION.paid_status (boolean) truyền lên
 *      Tuition.status (String) → gây nhầm lẫn. Nay tách rõ 2 trường.
 */
public class DormTuition extends Tuition {

    // ── Hằng DORMITORY_REGISTRATION.status ───────────────────
    public static final String REG_PENDING     = "PENDING";
    public static final String REG_APPROVED    = "APPROVED";
    public static final String REG_REJECTED    = "REJECTED";
    public static final String REG_CHECKED_OUT = "CHECKED_OUT";

    // ── Fields ────────────────────────────────────────────────
    private long   dormRegId;      // DORMITORY_REGISTRATION.dorm_reg_id  PK
    private long   roomId;         // DORMITORY_REGISTRATION.room_id  FK
    private String building;       // DORMITORY_ROOM.building  ← THÊM MỚI
    private long   pricePerMonth;  // DORMITORY_ROOM.price_per_month  ← THÊM MỚI
    private String startDate;      // DORMITORY_REGISTRATION.start_date "yyyy-MM-dd"  ← THÊM MỚI
    private String endDate;        // DORMITORY_REGISTRATION.end_date  "yyyy-MM-dd"   ← THÊM MỚI
    private long   approvedBy;     // DORMITORY_REGISTRATION.approved_by  FK → USER   ← THÊM MỚI
    private String approvedAt;     // DORMITORY_REGISTRATION.approved_at               ← THÊM MỚI
    private String regStatus;      // DORMITORY_REGISTRATION.status                    ← THÊM MỚI

    /**
     * Constructor tối giản — tương thích ngược với code cũ.
     *
     * @param dormRegId  DORMITORY_REGISTRATION.dorm_reg_id
     * @param roomName   DORMITORY_ROOM.room_code (hiển thị)
     * @param details    mô tả tháng thuê
     * @param totalFee   DORMITORY_REGISTRATION.total_fee → FEE.total_amount
     * @param feeStatus  FEE.status — dùng hằng Tuition.STATUS_*
     */
    public DormTuition(long dormRegId, String roomName, String details,
                       long totalFee, String feeStatus) {
        super(roomName, details, totalFee, feeStatus);
        this.dormRegId = dormRegId;
        this.feeType   = Tuition.TYPE_DORMITORY;
        this.regStatus = REG_APPROVED; // tương thích ngược — giả định đã duyệt
    }

    /**
     * Constructor đầy đủ — dùng khi map từ API / Room.
     */
    public DormTuition(long feeId, long userId,
                       long dormRegId, long roomId,
                       String roomName, String building,
                       long pricePerMonth, String details,
                       long totalFee, long paidAmount,
                       String feeStatus, String dueDate,
                       String paymentMethod, String paidAt,
                       String receiptNumber, long semesterId,
                       String startDate, String endDate,
                       long approvedBy, String approvedAt, String regStatus) {
        super(feeId, userId, Tuition.TYPE_DORMITORY,
                roomName, details, totalFee, paidAmount,
                feeStatus, dueDate, paymentMethod, paidAt,
                receiptNumber, semesterId);
        this.dormRegId     = dormRegId;
        this.roomId        = roomId;
        this.building      = building;
        this.pricePerMonth = pricePerMonth;
        this.startDate     = startDate;
        this.endDate       = endDate;
        this.approvedBy    = approvedBy;
        this.approvedAt    = approvedAt;
        this.regStatus     = regStatus;
    }

    @Override
    public String getIdentifier() {
        return "DORM-" + dormRegId;
    }

    // ── Getters ──────────────────────────────────────────────

    public long   getDormRegId()     { return dormRegId; }
    public long   getRoomId()        { return roomId; }
    /** DORMITORY_ROOM.building. */
    public String getBuilding()      { return building; }
    /** DORMITORY_ROOM.price_per_month (VND/tháng). */
    public long   getPricePerMonth() { return pricePerMonth; }
    /** DORMITORY_REGISTRATION.start_date "yyyy-MM-dd". */
    public String getStartDate()     { return startDate; }
    /** DORMITORY_REGISTRATION.end_date "yyyy-MM-dd". */
    public String getEndDate()       { return endDate; }
    /** DORMITORY_REGISTRATION.approved_by (user_id của admin duyệt). */
    public long   getApprovedBy()    { return approvedBy; }
    /** DORMITORY_REGISTRATION.approved_at. */
    public String getApprovedAt()    { return approvedAt; }
    /** DORMITORY_REGISTRATION.status — "PENDING"|"APPROVED"|"REJECTED"|"CHECKED_OUT". */
    public String getRegStatus()     { return regStatus; }

    // ── Setters ──────────────────────────────────────────────

    public void setRoomId(long roomId)           { this.roomId = roomId; }
    public void setBuilding(String building)     { this.building = building; }
    public void setStartDate(String startDate)   { this.startDate = startDate; }
    public void setEndDate(String endDate)       { this.endDate = endDate; }
    public void setApprovedBy(long approvedBy)   { this.approvedBy = approvedBy; }
    public void setApprovedAt(String approvedAt) { this.approvedAt = approvedAt; }
    public void setRegStatus(String regStatus)   { this.regStatus = regStatus; }

    /** Tiện ích: true nếu đơn KTX đã được duyệt. */
    public boolean isApproved() { return REG_APPROVED.equals(regStatus); }
    /** Tiện ích: true nếu đơn KTX đang chờ duyệt. */
    public boolean isPending()  { return REG_PENDING.equals(regStatus); }
}