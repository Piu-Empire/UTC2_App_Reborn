package com.utc2.appreborn.ui.dormitory.model;

/**
 * Lớp đại diện cho một phòng kí túc xá.
 *
 * [Chương 3 - OOP]
 *  - Kế thừa: extends DormitoryItem
 *  - Bao đóng: tất cả field đều private, truy cập qua getters
 *  - Override: getDisplayInfo() từ lớp cha DormitoryItem
 *  - Overload: 2 constructor (có roomType / không có roomType)
 */
public class Room extends DormitoryItem {

    // ── Enum loại phòng ───────────────────────────────────────────────────────
    public enum RoomType {
        NAM("Nam"), NU("Nữ");

        private final String label;

        RoomType(String label) { this.label = label; }

        public String getLabel() { return label; }
    }

    // ── Private fields (Bao đóng) ─────────────────────────────────────────────
    private final String   building;       // Tòa: A / B / C
    private final int      capacity;       // Sức chứa (số người)
    private final int      pricePerMonth;  // Giá mỗi tháng (VND)
    private final boolean  available;      // Còn chỗ hay không
    private final RoomType roomType;       // Nam / Nữ

    // ── Overload constructor 1: đầy đủ ───────────────────────────────────────
    public Room(String id, String name, String building,
                int capacity, int pricePerMonth,
                boolean available, RoomType roomType) {
        super(id, name);
        this.building      = building;
        this.capacity      = capacity;
        this.pricePerMonth = pricePerMonth;
        this.available     = available;
        this.roomType      = roomType;
    }

    // ── Overload constructor 2: mặc định loại NAM ────────────────────────────
    public Room(String id, String name, String building,
                int capacity, int pricePerMonth, boolean available) {
        this(id, name, building, capacity, pricePerMonth, available, RoomType.NAM);
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public String   getBuilding()      { return building; }
    public int      getCapacity()      { return capacity; }
    public int      getPricePerMonth() { return pricePerMonth; }
    public boolean  isAvailable()      { return available; }
    public RoomType getRoomType()      { return roomType; }

    // ── Override từ DormitoryItem ─────────────────────────────────────────────
    @Override
    public String getDisplayInfo() {
        return "👥 " + capacity
                + "  |  " + String.format("%,d", pricePerMonth) + "đ/tháng"
                + "  Loại: " + roomType.getLabel();
    }

    /** Tính tổng tiền theo số tháng. */
    public int calculateTotal(int months) {
        return pricePerMonth * months;
    }
}
