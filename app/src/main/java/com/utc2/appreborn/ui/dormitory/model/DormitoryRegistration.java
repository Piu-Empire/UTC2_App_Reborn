package com.utc2.appreborn.ui.dormitory.model;

/**
 * Lớp đại diện cho một lượt đăng ký phòng KTX.
 *
 * [Chương 3 - OOP]
 *  - Kế thừa: extends DormitoryItem (lớp con thứ 2)
 *  - Bao đóng: private fields
 *  - Override: getDisplayInfo() từ DormitoryItem
 */
public class DormitoryRegistration extends DormitoryItem {

    private final Room   room;
    private final int    months;
    private       String status;   // "PENDING" / "CONFIRMED" / "CANCELLED"

    public DormitoryRegistration(String id, Room room, int months) {
        super(id, "Đăng ký: " + room.getName());
        this.room   = room;
        this.months = months;
        this.status = "PENDING";
    }

    // ── Getters / Setter ──────────────────────────────────────────────────────
    public Room   getRoom()   { return room; }
    public int    getMonths() { return months; }
    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    /** Tổng tiền cần thanh toán. */
    public int getTotalPrice() {
        return room.calculateTotal(months);
    }

    // ── Override từ DormitoryItem ─────────────────────────────────────────────
    @Override
    public String getDisplayInfo() {
        return room.getName()
                + " | " + months + " tháng"
                + " | Tổng: " + String.format("%,d", getTotalPrice()) + "đ"
                + " | " + status;
    }
}
