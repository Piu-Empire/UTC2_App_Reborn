package com.utc2.appreborn.ui.dormitory.lookup.model;

import com.utc2.appreborn.ui.dormitory.model.DormitoryItem;
import com.utc2.appreborn.ui.dormitory.model.Room;

import java.util.ArrayList;
import java.util.List;

/**
 * Lớp chi tiết phòng dùng cho màn hình Tra phòng.
 *
 * [Chương 3 - OOP]
 *  - Kế thừa: extends DormitoryItem (tận dụng lớp cha chung)
 *  - Override: getDisplayInfo() – đa hình
 *  - Bao đóng: private fields
 *
 * [Chương 5 - Collection]
 *  - List<RoomOccupant>: danh sách sinh viên đang ở trong phòng
 */
public class RoomDetail extends DormitoryItem {

    private final Room               room;        // Thông tin phòng gốc
    private final int                currentOccupants; // Số người đang ở thực tế
    private final List<RoomOccupant> occupantList;     // Danh sách sinh viên

    public RoomDetail(Room room, int currentOccupants, List<RoomOccupant> occupantList) {
        super(room.getId(), room.getName());
        this.room             = room;
        this.currentOccupants = currentOccupants;
        this.occupantList     = new ArrayList<>(occupantList);
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public Room               getRoom()             { return room; }
    public int                getCurrentOccupants() { return currentOccupants; }
    public List<RoomOccupant> getOccupantList()     { return new ArrayList<>(occupantList); }

    /** Tỉ lệ lấp đầy phòng (0.0 – 1.0) dùng cho thanh progress. */
    public float getOccupancyRatio() {
        if (room.getCapacity() == 0) return 0f;
        return (float) currentOccupants / room.getCapacity();
    }

    public String getStatusLabel() {
        return room.isAvailable() ? "Còn chỗ" : "Hết chỗ";
    }

    // ── Override đa hình từ DormitoryItem ─────────────────────────────────────
    @Override
    public String getDisplayInfo() {
        return room.getName()
                + " | Loại: " + room.getRoomType().getLabel()
                + " | Sức chứa: " + room.getCapacity()
                + " | Hiện tại: " + currentOccupants
                + " | " + getStatusLabel();
    }
}