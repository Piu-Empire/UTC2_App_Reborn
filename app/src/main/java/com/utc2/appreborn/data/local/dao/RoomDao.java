package com.utc2.appreborn.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.utc2.appreborn.data.local.entity.DormitoryRoomEntity;

import java.util.List;

/**
 * RoomDao - Room DAO cho bảng "ktx_room".
 *
 * Phục vụ màn hình Kí túc xá:
 *  - Tab Đăng ký: hiển thị danh sách phòng, lọc theo tòa/giá/loại
 *  - Tab Tra phòng: tìm phòng theo ID hoặc tòa
 */
@Dao
public interface RoomDao {

    // ─── INSERT ──────────────────────────────────────────────────────────────

    /** Thêm mới một phòng. Nếu trùng PK → bỏ qua. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(DormitoryRoomEntity room);

    /** Upsert danh sách phòng từ server. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<DormitoryRoomEntity> rooms);

    // ─── UPDATE ──────────────────────────────────────────────────────────────

    /** Cập nhật thông tin phòng. */
    @Update
    void update(DormitoryRoomEntity room);

    /** Cập nhật trạng thái theo roomId. */
    @Query("UPDATE dormitory_room SET status = :status WHERE room_id = :roomId")
    void updateStatus(long roomId, String status);

    // ─── DELETE ──────────────────────────────────────────────────────────────

    @Delete
    void delete(DormitoryRoomEntity room);

    @Query("DELETE FROM dormitory_room")
    void deleteAll();

    // ─── QUERY ───────────────────────────────────────────────────────────────

    /** Lấy toàn bộ danh sách phòng — dùng cho tab Đăng ký. */
    @Query("SELECT * FROM dormitory_room ORDER BY building ASC, room_name ASC")
    LiveData<List<DormitoryRoomEntity>> getAllRooms();

    /** Phiên bản đồng bộ — dùng trong background thread. */
    @Query("SELECT * FROM dormitory_room ORDER BY building ASC, room_name ASC")
    List<DormitoryRoomEntity> getAllRoomsSync();

    /** Tìm phòng theo ID. */
    @Query("SELECT * FROM dormitory_room WHERE room_id = :roomId LIMIT 1")
    DormitoryRoomEntity getRoomById(long roomId);

    /** Lấy danh sách phòng theo tòa. */
    @Query("SELECT * FROM dormitory_room WHERE building = :building ORDER BY room_name ASC")
    LiveData<List<DormitoryRoomEntity>> getRoomsByBuilding(String building);

    /**
     * Lọc phòng theo tòa, giá tối đa, loại phòng.
     * Truyền "" hoặc null cho building/roomType để bỏ qua điều kiện đó.
     * Truyền 0 cho maxPrice để bỏ qua điều kiện giá.
     */
    @Query("SELECT * FROM dormitory_room WHERE " +
            "(:building = '' OR building = :building) AND " +
            "(:maxPrice = 0 OR price_per_month <= :maxPrice) AND " +
            "(:roomType = '' OR room_type = :roomType) " +
            "ORDER BY building ASC, room_name ASC")
    List<DormitoryRoomEntity> filterRooms(String building, int maxPrice, String roomType);

    /** Chỉ lấy các phòng còn chỗ. */
    @Query("SELECT * FROM dormitory_room WHERE status = 'AVAILABLE' ORDER BY building ASC, room_name ASC")
    LiveData<List<DormitoryRoomEntity>> getAvailableRooms();

    /** Đếm tổng số phòng đang cache. */
    @Query("SELECT COUNT(*) FROM dormitory_room")
    int countAll();
}