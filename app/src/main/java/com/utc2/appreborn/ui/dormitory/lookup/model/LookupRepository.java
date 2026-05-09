package com.utc2.appreborn.ui.dormitory.lookup.model;

import com.utc2.appreborn.ui.dormitory.exception.DormitoryException;
import com.utc2.appreborn.ui.dormitory.model.DormitoryRepository;
import com.utc2.appreborn.ui.dormitory.model.Room;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repository dữ liệu cho màn hình Tra phòng.
 *
 * [Chương 3 - OOP]
 *  - Tái sử dụng DormitoryRepository (composition)
 *  - Singleton pattern
 *
 * [Chương 4 - Xử lý ngoại lệ]
 *  - throws DormitoryException khi không tìm thấy phòng
 *
 * [Chương 5 - Collection]
 *  - Map<String, RoomDetail>: tra cứu nhanh chi tiết phòng theo ID (O(1))
 *  - List<RoomDetail>       : danh sách kết quả lọc/tìm kiếm
 */
public class LookupRepository {

    // ── Collection: Map tra cứu nhanh theo roomId ────────────────────────────
    private final Map<String, RoomDetail> detailMap = new HashMap<>();

    // ── Singleton ─────────────────────────────────────────────────────────────
    private static LookupRepository instance;

    private LookupRepository() {
        loadSampleData();
    }

    public static LookupRepository getInstance() {
        if (instance == null) {
            instance = new LookupRepository();
        }
        return instance;
    }

    // ── Dữ liệu mẫu ──────────────────────────────────────────────────────────
    private void loadSampleData() {
        List<Room> rooms = DormitoryRepository.getInstance().getAllRooms();

        // Dữ liệu mẫu: danh sách sinh viên từng phòng
        Map<String, List<RoomOccupant>> occupantsMap = new HashMap<>();

        occupantsMap.put("r1", Arrays.asList(
                new RoomOccupant("Nguyễn Văn Anh",   "6551071000", "CQ.CNTT.K65"),
                new RoomOccupant("Trần Minh Khoa",    "6551071001", "CQ.CNTT.K65"),
                new RoomOccupant("Lê Văn Hùng",       "6551071002", "CQ.CNTT.K65")
        ));
        occupantsMap.put("r2", Arrays.asList(
                new RoomOccupant("Nguyễn Thị Lan",    "6551071010", "CQ.CNTT.K65"),
                new RoomOccupant("Phạm Thị Mai",      "6551071011", "CQ.CNTT.K65"),
                new RoomOccupant("Hoàng Thị Hoa",     "6551071012", "CQ.CNTT.K65"),
                new RoomOccupant("Đỗ Thị Thu",        "6551071013", "CQ.CNTT.K65")
        ));
        occupantsMap.put("r3", Arrays.asList(
                new RoomOccupant("Vũ Văn Nam",        "6551071020", "CQ.CNTT.K65"),
                new RoomOccupant("Bùi Văn Đức",       "6551071021", "CQ.CNTT.K65"),
                new RoomOccupant("Đinh Văn Long",     "6551071022", "CQ.CNTT.K65"),
                new RoomOccupant("Phan Văn Tùng",     "6551071023", "CQ.CNTT.K65"),
                new RoomOccupant("Lý Văn Phúc",       "6551071024", "CQ.CNTT.K65")
        ));
        occupantsMap.put("r4", Arrays.asList(
                new RoomOccupant("Nguyễn Thị Ngọc",   "6551071030", "CQ.CNTT.K65"),
                new RoomOccupant("Trần Thị Bích",     "6551071031", "CQ.CNTT.K65")
        ));
        occupantsMap.put("r5", Arrays.asList(
                new RoomOccupant("Cao Văn Minh",      "6551071040", "CQ.CNTT.K65"),
                new RoomOccupant("Ngô Văn Tuấn",      "6551071041", "CQ.CNTT.K65"),
                new RoomOccupant("Dương Văn Kiên",    "6551071042", "CQ.CNTT.K65"),
                new RoomOccupant("Hà Văn Sơn",        "6551071043", "CQ.CNTT.K65"),
                new RoomOccupant("Mai Văn Thắng",     "6551071044", "CQ.CNTT.K65"),
                new RoomOccupant("Lưu Văn Quân",      "6551071045", "CQ.CNTT.K65"),
                new RoomOccupant("Tô Văn Bình",       "6551071046", "CQ.CNTT.K65"),
                new RoomOccupant("Chu Văn Dũng",      "6551071047", "CQ.CNTT.K65")
        ));
        occupantsMap.put("r6", Arrays.asList(
                new RoomOccupant("Võ Thị Hương",      "6551071050", "CQ.CNTT.K65"),
                new RoomOccupant("Lê Thị Yến",        "6551071051", "CQ.CNTT.K65"),
                new RoomOccupant("Nguyễn Thị Thảo",  "6551071052", "CQ.CNTT.K65")
        ));

        for (Room room : rooms) {
            List<RoomOccupant> list = occupantsMap.containsKey(room.getId())
                    ? occupantsMap.get(room.getId())
                    : new ArrayList<>();
            RoomDetail detail = new RoomDetail(room, list.size(), list);
            detailMap.put(room.getId(), detail);
        }
    }

    // ── API ───────────────────────────────────────────────────────────────────

    /** Lấy tất cả chi tiết phòng dưới dạng List. */
    public List<RoomDetail> getAllRoomDetails() {
        return new ArrayList<>(detailMap.values());
    }

    /**
     * Tìm chi tiết phòng theo ID.
     *
     * @throws DormitoryException nếu không tìm thấy
     */
    public RoomDetail findById(String roomId) throws DormitoryException {
        if (roomId == null || roomId.isEmpty()) {
            throw new DormitoryException("ID phòng không được để trống.");
        }
        RoomDetail detail = detailMap.get(roomId);
        if (detail == null) {
            throw new DormitoryException("Không tìm thấy phòng: " + roomId);
        }
        return detail;
    }

    /**
     * Lọc phòng theo tên phòng và tòa.
     *
     * [Chương 5] Duyệt Collection, lọc điều kiện
     */
    public List<RoomDetail> filter(String roomName, String building) {
        List<RoomDetail> result = new ArrayList<>();
        for (RoomDetail d : detailMap.values()) {
            boolean okRoom     = roomName == null || roomName.isEmpty()
                    || d.getRoom().getName().toLowerCase()
                    .contains(roomName.toLowerCase());
            boolean okBuilding = building == null || building.isEmpty()
                    || d.getRoom().getBuilding().equalsIgnoreCase(building);
            if (okRoom && okBuilding) {
                result.add(d);
            }
        }
        return result;
    }
}