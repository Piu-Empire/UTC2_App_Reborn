package com.utc2.appreborn.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.utc2.appreborn.data.local.entity.AcademicWarningEntity;

import java.util.List;

/**
 * AcademicWarningDao - Room DAO cho bảng "academic_warning".
 *
 * Các query chính phục vụ màn hình WarningsFragment:
 *  - Lấy cảnh báo theo user, theo kỳ, theo trạng thái
 *  - Lấy cảnh báo chưa giải quyết để hiển thị badge thông báo
 */
@Dao
public interface AcademicWarningDao {

    // ─── INSERT ─────────────────────────────────────────────────────────────────

    /** Thêm mới một cảnh báo vào cache. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(AcademicWarningEntity warning);

    /**
     * Upsert danh sách cảnh báo từ server.
     * REPLACE cập nhật status nếu server đã resolve warning.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<AcademicWarningEntity> warnings);

    // ─── UPDATE ─────────────────────────────────────────────────────────────────

    /** Cập nhật một cảnh báo (ví dụ: cập nhật status sau khi resolve). */
    @Update
    void update(AcademicWarningEntity warning);

    // ─── DELETE ─────────────────────────────────────────────────────────────────

    /** Xoá một cảnh báo khỏi cache. */
    @Delete
    void delete(AcademicWarningEntity warning);

    /** Xoá toàn bộ cảnh báo của một user (dùng khi logout). */
    @Query("DELETE FROM academic_warning WHERE user_id = :userId")
    void deleteAllByUserId(long userId);

    /** Xoá cảnh báo theo kỳ học (dùng khi refresh cache một kỳ cụ thể). */
    @Query("DELETE FROM academic_warning WHERE semester_id = :semesterId")
    void deleteBySemesterId(long semesterId);

    // ─── QUERY ──────────────────────────────────────────────────────────────────

    /**
     * Lấy toàn bộ cảnh báo của một sinh viên, sắp xếp mới nhất lên đầu.
     * Trả về LiveData để WarningsFragment tự cập nhật.
     *
     * @param userId ID của sinh viên
     */
    @Query("SELECT * FROM academic_warning WHERE user_id = :userId " +
            "ORDER BY issued_at DESC")
    LiveData<List<AcademicWarningEntity>> getWarningsByUserId(long userId);

    /**
     * Lấy cảnh báo của một sinh viên trong một kỳ học cụ thể.
     *
     * @param userId     ID sinh viên
     * @param semesterId ID kỳ học
     */
    @Query("SELECT * FROM academic_warning " +
            "WHERE user_id = :userId AND semester_id = :semesterId " +
            "ORDER BY issued_at DESC")
    LiveData<List<AcademicWarningEntity>> getWarningsByUserAndSemester(long userId, long semesterId);

    /**
     * Lấy các cảnh báo chưa được giải quyết (status = 'ACTIVE' hoặc tương đương).
     * Dùng để hiển thị badge đỏ trên bottom navigation.
     *
     * @param userId ID sinh viên
     * @param status Trạng thái cần lọc (VD: "ACTIVE", "PENDING")
     */
    @Query("SELECT * FROM academic_warning " +
            "WHERE user_id = :userId AND status = :status " +
            "ORDER BY issued_at DESC")
    LiveData<List<AcademicWarningEntity>> getWarningsByStatus(long userId, String status);

    /**
     * Đếm số cảnh báo chưa giải quyết — dùng để hiển thị badge số.
     *
     * @param userId ID sinh viên
     * @param status Trạng thái "chưa giải quyết" (VD: "ACTIVE")
     */
    @Query("SELECT COUNT(*) FROM academic_warning " +
            "WHERE user_id = :userId AND status = :status")
    int countActiveWarnings(long userId, String status);

    /**
     * Lấy chi tiết một cảnh báo theo ID.
     */
    @Query("SELECT * FROM academic_warning WHERE warning_id = :warningId LIMIT 1")
    AcademicWarningEntity getWarningById(long warningId);
}