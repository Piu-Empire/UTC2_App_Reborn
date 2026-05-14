package com.utc2.appreborn.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.utc2.appreborn.data.local.entity.DormitoryRegistrationEntity;
import com.utc2.appreborn.data.local.entity.DormitoryRoomEntity;

import java.util.List;

/**
 * DormitoryDao — DAO cho 2 bảng KTX:
 *  - dormitory_room         (Phòng KTX)
 *  - dormitory_registration (Đăng ký KTX)
 *
 * Thay thế DormitoryRepository (RAM), dữ liệu tồn tại sau khi thoát app.
 */
@Dao
public interface DormitoryDao {

    // ════════════════════════════════════════════
    //  DORMITORY_ROOM
    // ════════════════════════════════════════════

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRoom(DormitoryRoomEntity room);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRooms(List<DormitoryRoomEntity> rooms);

    @Update
    void updateRoom(DormitoryRoomEntity room);

    @Query("UPDATE dormitory_room SET status = :status WHERE room_id = :roomId")
    void updateRoomStatus(long roomId, String status);

    @Query("DELETE FROM dormitory_room")
    void deleteAllRooms();

    @Query("SELECT * FROM dormitory_room ORDER BY building ASC, room_name ASC")
    LiveData<List<DormitoryRoomEntity>> getAllRooms();

    @Query("SELECT * FROM dormitory_room ORDER BY building ASC, room_name ASC")
    List<DormitoryRoomEntity> getAllRoomsSync();

    @Query("SELECT * FROM dormitory_room WHERE room_id = :roomId LIMIT 1")
    DormitoryRoomEntity getRoomById(long roomId);

    @Query("SELECT * FROM dormitory_room WHERE " +
            "(:building = '' OR building = :building) AND " +
            "(:maxPrice = 0 OR price_per_month <= :maxPrice) AND " +
            "(:roomType = '' OR room_type = :roomType) " +
            "ORDER BY building ASC, room_name ASC")
    List<DormitoryRoomEntity> filterRooms(String building, int maxPrice, String roomType);

    @Query("SELECT * FROM dormitory_room WHERE status = 'AVAILABLE' ORDER BY building ASC")
    LiveData<List<DormitoryRoomEntity>> getAvailableRooms();

    @Query("SELECT COUNT(*) FROM dormitory_room")
    int countRooms();

    // ════════════════════════════════════════════
    //  DORMITORY_REGISTRATION
    // ════════════════════════════════════════════

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertRegistration(DormitoryRegistrationEntity registration);

    @Update
    void updateRegistration(DormitoryRegistrationEntity registration);

    @Query("UPDATE dormitory_registration SET status = :status WHERE registration_id = :regId")
    void updateRegistrationStatus(long regId, String status);

    @Query("DELETE FROM dormitory_registration WHERE user_id = :userId")
    void deleteRegistrationsByUserId(long userId);

    /** Đăng ký đang hoạt động (chưa hủy) — dùng cho UI */
    @Query("SELECT * FROM dormitory_registration WHERE user_id = :userId " +
            "AND status != 'CANCELLED' ORDER BY created_at DESC LIMIT 1")
    LiveData<DormitoryRegistrationEntity> getActiveRegistration(long userId);

    /** Phiên bản đồng bộ — dùng trong background thread */
    @Query("SELECT * FROM dormitory_registration WHERE user_id = :userId " +
            "AND status != 'CANCELLED' ORDER BY created_at DESC LIMIT 1")
    DormitoryRegistrationEntity getActiveRegistrationSync(long userId);

    /** Lịch sử đăng ký */
    @Query("SELECT * FROM dormitory_registration WHERE user_id = :userId ORDER BY created_at DESC")
    LiveData<List<DormitoryRegistrationEntity>> getAllRegistrations(long userId);

    @Query("SELECT * FROM dormitory_registration WHERE user_id = :userId ORDER BY created_at DESC")
    List<DormitoryRegistrationEntity> getAllRegistrationsSync(long userId);

    @Query("SELECT * FROM dormitory_registration WHERE registration_id = :regId LIMIT 1")
    DormitoryRegistrationEntity getRegistrationById(long regId);
}