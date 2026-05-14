package com.utc2.appreborn.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.utc2.appreborn.data.local.entity.CourseRegistrationEntity;

import java.util.List;

/**
 * CourseRegistrationDao - Room DAO cho bảng "course_registration".
 *
 * Phục vụ màn hình Đăng ký học phần:
 *  - Tab Đăng ký: lưu môn học vào giỏ, xác nhận đăng ký
 *  - Tab Kết quả: hiển thị danh sách môn đã đăng ký, hủy đăng ký
 */
@Dao
public interface CourseRegistrationDao {

    // ─── INSERT ──────────────────────────────────────────────────────────────

    /** Lưu một lượt đăng ký học phần. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CourseRegistrationEntity registration);

    /** Upsert danh sách đăng ký (dùng khi sync từ server). */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CourseRegistrationEntity> registrations);

    // ─── UPDATE ──────────────────────────────────────────────────────────────

    @Update
    void update(CourseRegistrationEntity registration);

    /** Cập nhật trạng thái một đăng ký (PENDING / CONFIRMED / CANCELLED). */
    @Query("UPDATE course_registration SET status = :status WHERE reg_id = :regId")
    void updateStatus(String regId, String status);

    /**
     * Xác nhận toàn bộ đăng ký PENDING của user
     * → chuyển sang CONFIRMED (dùng khi bấm nút Xác nhận).
     */
    @Query("UPDATE course_registration SET status = 'CONFIRMED' " +
            "WHERE user_id = :userId AND status = 'PENDING'")
    void confirmAllPending(long userId);

    // ─── DELETE ──────────────────────────────────────────────────────────────

    @Delete
    void delete(CourseRegistrationEntity registration);

    /** Xóa một đăng ký theo regId. */
    @Query("DELETE FROM course_registration WHERE reg_id = :regId")
    void deleteByRegId(String regId);

    /** Hủy đăng ký một môn học của user (set CANCELLED). */
    @Query("UPDATE course_registration SET status = 'CANCELLED' " +
            "WHERE user_id = :userId AND course_id = :courseId")
    void cancelByCourseId(long userId, String courseId);

    /** Xóa toàn bộ đăng ký của một user (dùng khi logout). */
    @Query("DELETE FROM course_registration WHERE user_id = :userId")
    void deleteAllByUserId(long userId);

    // ─── QUERY ───────────────────────────────────────────────────────────────

    /**
     * Lấy tất cả môn đã CONFIRMED của user — dùng cho tab Kết quả.
     * Trả về LiveData để UI tự cập nhật.
     */
    @Query("SELECT * FROM course_registration WHERE user_id = :userId " +
            "AND status = 'CONFIRMED' ORDER BY registered_at ASC")
    LiveData<List<CourseRegistrationEntity>> getConfirmedByUserId(long userId);

    /** Phiên bản đồng bộ — dùng trong background thread. */
    @Query("SELECT * FROM course_registration WHERE user_id = :userId " +
            "AND status = 'CONFIRMED' ORDER BY registered_at ASC")
    List<CourseRegistrationEntity> getConfirmedByUserIdSync(long userId);

    /**
     * Lấy các môn đang PENDING (trong giỏ chưa xác nhận).
     */
    @Query("SELECT * FROM course_registration WHERE user_id = :userId " +
            "AND status = 'PENDING' ORDER BY registered_at ASC")
    LiveData<List<CourseRegistrationEntity>> getPendingByUserId(long userId);

    /**
     * Lấy toàn bộ đăng ký (mọi trạng thái) của user.
     */
    @Query("SELECT * FROM course_registration WHERE user_id = :userId " +
            "ORDER BY registered_at DESC")
    LiveData<List<CourseRegistrationEntity>> getAllByUserId(long userId);

    /**
     * Tìm đăng ký theo courseId — kiểm tra user đã đăng ký môn này chưa.
     */
    @Query("SELECT * FROM course_registration WHERE user_id = :userId " +
            "AND course_id = :courseId AND status != 'CANCELLED' LIMIT 1")
    CourseRegistrationEntity getRegistrationByCourseId(long userId, String courseId);

    /**
     * Tính tổng tín chỉ đã đăng ký (PENDING + CONFIRMED) của user.
     * Dùng để kiểm tra không vượt quá 24 tín chỉ.
     */
    @Query("SELECT SUM(credits) FROM course_registration WHERE user_id = :userId " +
            "AND status != 'CANCELLED'")
    int getTotalCredits(long userId);

    /** Đếm số môn đã CONFIRMED của user. */
    @Query("SELECT COUNT(*) FROM course_registration WHERE user_id = :userId " +
            "AND status = 'CONFIRMED'")
    int countConfirmed(long userId);

    /**
     * Lấy danh sách courseId đã xác nhận — thay thế CourseStorage (file JSON).
     */
    @Query("SELECT course_id FROM course_registration WHERE user_id = :userId " +
            "AND status = 'CONFIRMED'")
    List<String> getConfirmedCourseIds(long userId);
}