package com.utc2.appreborn.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.utc2.appreborn.data.local.entity.DormitoryRegistrationEntity;

import java.util.List;

/**
 * DormitoryRegistrationDao - Room DAO cho bảng "dormitory_registration".
 *
 * Phục vụ màn hình Kí túc xá:
 *  - Lưu lượt đăng ký phòng của sinh viên
 *  - Xem lịch sử đăng ký, hủy đăng ký
 */
@Dao
public interface DormitoryRegistrationDao {

    // ─── INSERT ──────────────────────────────────────────────────────────────

    /** Lưu một lượt đăng ký mới. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DormitoryRegistrationEntity registration);

    // ─── UPDATE ──────────────────────────────────────────────────────────────

    @Update
    void update(DormitoryRegistrationEntity registration);

    /** Cập nhật trạng thái đăng ký (PENDING / CONFIRMED / CANCELLED). */
    @Query("UPDATE dormitory_registration SET status = :status WHERE registration_id = :regId")
    void updateStatus(long regId, String status);

    // ─── DELETE ──────────────────────────────────────────────────────────────

    @Delete
    void delete(DormitoryRegistrationEntity registration);

    /** Xóa toàn bộ đăng ký của một user (dùng khi logout). */
    @Query("DELETE FROM dormitory_registration WHERE user_id = :userId")
    void deleteAllByUserId(long userId);

    /** Xóa một đăng ký theo regId. */
    @Query("DELETE FROM dormitory_registration WHERE registration_id = :regId")
    void deleteByRegId(long regId);

    // ─── QUERY ───────────────────────────────────────────────────────────────

    /**
     * Lấy toàn bộ lịch sử đăng ký của một sinh viên,
     * sắp xếp mới nhất lên đầu.
     */
    @Query("SELECT * FROM dormitory_registration WHERE user_id = :userId " +
            "ORDER BY created_at DESC")
    LiveData<List<DormitoryRegistrationEntity>> getRegistrationsByUserId(long userId);

    /** Phiên bản đồng bộ. */
    @Query("SELECT * FROM dormitory_registration WHERE user_id = :userId " +
            "ORDER BY created_at DESC")
    List<DormitoryRegistrationEntity> getRegistrationsByUserIdSync(long userId);

    /**
     * Lấy đăng ký đang hoạt động (chưa hủy) của sinh viên.
     * Một sinh viên thường chỉ có 1 đăng ký ACTIVE tại một thời điểm.
     */
    @Query("SELECT * FROM dormitory_registration WHERE user_id = :userId " +
            "AND status != 'CANCELLED' ORDER BY created_at DESC LIMIT 1")
    LiveData<DormitoryRegistrationEntity> getActiveRegistration(long userId);

    /** Tìm đăng ký theo regId. */
    @Query("SELECT * FROM dormitory_registration WHERE registration_id = :regId LIMIT 1")
    DormitoryRegistrationEntity getRegistrationById(long regId);

    /**
     * Lấy đăng ký theo phòng — dùng để kiểm tra phòng đã có ai đăng ký chưa.
     */
    @Query("SELECT * FROM dormitory_registration WHERE room_id = :roomId " +
            "AND status != 'CANCELLED'")
    List<DormitoryRegistrationEntity> getRegistrationsByRoomId(long roomId);

    /** Đếm số đăng ký chưa hủy của một user. */
    @Query("SELECT COUNT(*) FROM dormitory_registration WHERE user_id = :userId " +
            "AND status != 'CANCELLED'")
    int countActiveByUserId(long userId);
}