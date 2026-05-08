package com.utc2.appreborn.ui.dormitory.model;

import com.utc2.appreborn.ui.dormitory.exception.DormitoryException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Repository quản lý toàn bộ dữ liệu KTX trong bộ nhớ.
 *
 * [Chương 5 - Collection]
 *  - List<Room>                         : danh sách phòng có thứ tự để hiển thị
 *  - Map<String, Room>                  : tra cứu nhanh phòng theo ID (O(1))
 *  - Map<String, DormitoryRegistration> : lưu lịch sử đăng ký (LinkedHashMap giữ thứ tự)
 *
 * [Chương 4 - Xử lý ngoại lệ]
 *  - throws DormitoryException khi input không hợp lệ hoặc nghiệp vụ lỗi
 */
public class DormitoryRepository {

    // ── Collection 1: List – hiển thị có thứ tự ──────────────────────────────
    private final List<Room> roomList = new ArrayList<>();

    // ── Collection 2: HashMap – tra cứu theo ID ──────────────────────────────
    private final Map<String, Room> roomMap = new HashMap<>();

    // ── Collection 3: LinkedHashMap – lịch sử đăng ký (giữ thứ tự thêm vào) ─
    private final Map<String, DormitoryRegistration> registrations = new LinkedHashMap<>();

    // ── Singleton ─────────────────────────────────────────────────────────────
    private static DormitoryRepository instance;

    private DormitoryRepository() {
        loadSampleData();
    }

    public static DormitoryRepository getInstance() {
        if (instance == null) {
            instance = new DormitoryRepository();
        }
        return instance;
    }

    // ── Dữ liệu mẫu ──────────────────────────────────────────────────────────
    private void loadSampleData() {
        put(new Room("r1", "Phòng 201 - Tòa A", "A", 4, 300000, true,  Room.RoomType.NAM));
        put(new Room("r2", "Phòng 202 - Tòa A", "A", 6, 450000, true,  Room.RoomType.NU));
        put(new Room("r3", "Phòng 203 - Tòa B", "B", 5, 500000, false, Room.RoomType.NAM));
        put(new Room("r4", "Phòng 204 - Tòa B", "B", 3, 250000, true,  Room.RoomType.NU));
        put(new Room("r5", "Phòng 205 - Tòa C", "C", 8, 700000, false, Room.RoomType.NAM));
        put(new Room("r6", "Phòng 206 - Tòa C", "C", 4, 350000, true,  Room.RoomType.NU));
    }

    private void put(Room room) {
        roomList.add(room);
        roomMap.put(room.getId(), room);
    }

    // ── API phòng ─────────────────────────────────────────────────────────────

    /** Trả về bản sao danh sách toàn bộ phòng. */
    public List<Room> getAllRooms() {
        return new ArrayList<>(roomList);
    }

    /**
     * Lọc phòng theo tòa, giá tối đa, loại phòng.
     *
     * @throws DormitoryException nếu giá lọc âm
     */
    public List<Room> filterRooms(String building, int maxPrice, Room.RoomType roomType)
            throws DormitoryException {

        if (maxPrice < 0) {
            throw new DormitoryException("Giá lọc không được âm: " + maxPrice);
        }

        List<Room> result = new ArrayList<>();
        for (Room room : roomList) {
            boolean okBuilding = building == null || building.isEmpty()
                    || room.getBuilding().equalsIgnoreCase(building);
            boolean okPrice    = maxPrice == 0 || room.getPricePerMonth() <= maxPrice;
            boolean okType     = roomType == null || room.getRoomType() == roomType;

            if (okBuilding && okPrice && okType) {
                result.add(room);
            }
        }
        return result;
    }

    /**
     * Tìm phòng theo ID.
     *
     * @throws DormitoryException nếu không tìm thấy
     */
    public Room findRoomById(String roomId) throws DormitoryException {
        if (roomId == null || roomId.isEmpty()) {
            throw new DormitoryException("ID phòng không được để trống.");
        }
        Room room = roomMap.get(roomId);
        if (room == null) {
            throw new DormitoryException("Không tìm thấy phòng: " + roomId);
        }
        return room;
    }

    // ── API đăng ký ───────────────────────────────────────────────────────────

    /**
     * Tạo đăng ký phòng mới cho sinh viên.
     *
     * @throws DormitoryException nếu phòng hết chỗ hoặc số tháng không hợp lệ
     */
    public DormitoryRegistration registerRoom(String roomId, int months)
            throws DormitoryException {

        if (months <= 0 || months > 12) {
            throw new DormitoryException("Số tháng đăng ký phải từ 1 đến 12.");
        }

        Room room = findRoomById(roomId);

        if (!room.isAvailable()) {
            throw new DormitoryException("Phòng " + room.getName() + " đã hết chỗ!");
        }

        String regId = "reg_" + System.currentTimeMillis();
        DormitoryRegistration reg = new DormitoryRegistration(regId, room, months);

        // Lưu vào Collection Map
        registrations.put(regId, reg);
        return reg;
    }

    /**
     * Hủy đăng ký theo ID.
     *
     * @throws DormitoryException nếu không tìm thấy đăng ký
     */
    public void cancelRegistration(String regId) throws DormitoryException {
        DormitoryRegistration reg = registrations.get(regId);
        if (reg == null) {
            throw new DormitoryException("Không tìm thấy đăng ký: " + regId);
        }
        reg.setStatus("CANCELLED");
    }

    /** Lấy tất cả đăng ký. */
    public List<DormitoryRegistration> getAllRegistrations() {
        return new ArrayList<>(registrations.values());
    }
}
