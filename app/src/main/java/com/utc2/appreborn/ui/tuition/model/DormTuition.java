package com.utc2.appreborn.ui.tuition.model;

/**
 * DormTuition
 * ──────────────────────────────────────────────────────────────
 * Đại diện khoản phí ký túc xá.
 *
 * Mapping: TABLE DORMITORY_REGISTRATION JOIN DORMITORY_ROOM JOIN FEE
 *
 * Từ DORMITORY_REGISTRATION:
 *   dorm_reg_id, user_id, room_id, start_date, end_date,
 *   status (regStatus), total_fee (dormTotalFee), paid_status (dormPaidStatus)
 *
 * Từ DORMITORY_ROOM:
 *   building, price_per_month
 *
 * Từ FEE (qua lớp cha Tuition):
 *   fee_id, semester_id, total_amount, paid_amount, due_date,
 *   status, payment_method, paid_at
 *
 * FIX: pricePerMonth / dormTotalFee đổi từ long → double để khớp DECIMAL(15,2).
 * FIX: Thêm dormTotalFee (DORMITORY_REGISTRATION.total_fee)
 *      và dormPaidStatus (DORMITORY_REGISTRATION.paid_status).
 */
public class DormTuition extends Tuition {

    // ── Hằng DORMITORY_REGISTRATION.status — khớp backend ───
    public static final String REG_PENDING     = "chờ duyệt";
    public static final String REG_APPROVED    = "đã duyệt";
    public static final String REG_REJECTED    = "từ chối";
    public static final String REG_CHECKED_OUT = "đã trả phòng";

    // ── Hằng DORMITORY_REGISTRATION.paid_status ──────────────
    public static final String DORM_PAY_UNPAID = "chưa đóng";
    public static final String DORM_PAY_PAID   = "đã đóng";

    // ── Fields từ DORMITORY_REGISTRATION + DORMITORY_ROOM ────
    private long   dormRegId;       // DORMITORY_REGISTRATION.dorm_reg_id
    private long   roomId;          // DORMITORY_REGISTRATION.room_id
    private String building;        // DORMITORY_ROOM.building
    private double pricePerMonth;   // DORMITORY_ROOM.price_per_month  DECIMAL(15,2)
    private String startDate;       // DORMITORY_REGISTRATION.start_date
    private String endDate;         // DORMITORY_REGISTRATION.end_date
    private String regStatus;       // DORMITORY_REGISTRATION.status
    private double dormTotalFee;    // DORMITORY_REGISTRATION.total_fee  DECIMAL(15,2)
    private String dormPaidStatus;  // DORMITORY_REGISTRATION.paid_status

    /**
     * Constructor tối giản — dùng cho mock data trong Activity.
     */
    public DormTuition(long dormRegId, String roomName, String details,
                       double totalAmount, String status) {
        super();
        this.dormRegId   = dormRegId;
        this.name        = roomName;
        this.details     = details;
        this.totalAmount = totalAmount;
        this.paidAmount  = STATUS_PAID.equals(status) ? totalAmount : 0.0;
        this.status      = status;
    }

    /**
     * Constructor đầy đủ — dùng khi map từ API / Room.
     */
    public DormTuition(long feeId, long userId, long semesterId,
                       double totalAmount, double paidAmount,
                       String dueDate, String feeStatus,
                       String paymentMethod, String paidAt,
                       long dormRegId, long roomId,
                       String roomName, String building,
                       double pricePerMonth,
                       String startDate, String endDate,
                       String regStatus,
                       double dormTotalFee, String dormPaidStatus) {
        super(feeId, userId, semesterId, totalAmount, paidAmount,
                dueDate, feeStatus, paymentMethod, paidAt);
        this.name           = roomName;
        this.details        = building + " | " + startDate + " → " + endDate;
        this.dormRegId      = dormRegId;
        this.roomId         = roomId;
        this.building       = building;
        this.pricePerMonth  = pricePerMonth;
        this.startDate      = startDate;
        this.endDate        = endDate;
        this.regStatus      = regStatus;
        this.dormTotalFee   = dormTotalFee;
        this.dormPaidStatus = dormPaidStatus;
    }

    @Override
    public String getIdentifier() { return "DORM-" + dormRegId; }

    // ── Getters ──────────────────────────────────────────────
    public long   getDormRegId()      { return dormRegId; }
    public long   getRoomId()         { return roomId; }
    public String getBuilding()       { return building; }
    public double getPricePerMonth()  { return pricePerMonth; }
    public String getStartDate()      { return startDate; }
    public String getEndDate()        { return endDate; }
    public String getRegStatus()      { return regStatus; }
    /** DORMITORY_REGISTRATION.total_fee */
    public double getDormTotalFee()   { return dormTotalFee; }
    /** DORMITORY_REGISTRATION.paid_status */
    public String getDormPaidStatus() { return dormPaidStatus; }

    // ── Setters ──────────────────────────────────────────────
    public void setRoomId(long v)            { this.roomId = v; }
    public void setBuilding(String v)        { this.building = v; }
    public void setStartDate(String v)       { this.startDate = v; }
    public void setEndDate(String v)         { this.endDate = v; }
    public void setRegStatus(String v)       { this.regStatus = v; }
    public void setDormTotalFee(double v)    { this.dormTotalFee = v; }
    public void setDormPaidStatus(String v)  { this.dormPaidStatus = v; }

    public boolean isApproved()   { return REG_APPROVED.equals(regStatus); }
    public boolean isPendingReg() { return REG_PENDING.equals(regStatus); }
}