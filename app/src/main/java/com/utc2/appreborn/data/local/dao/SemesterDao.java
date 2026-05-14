package com.utc2.appreborn.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.utc2.appreborn.data.local.entity.SemesterEntity;

import java.util.List;

/**
 * SemesterDao - Room DAO cho bảng "semester".
 *
 * Quy ước OnConflictStrategy:
 *  - REPLACE  → dùng cho upsert (sync cache từ server về)
 *  - ABORT    → mặc định, throw exception nếu conflict (insert thủ công)
 */
@Dao
public interface SemesterDao {

    // ─── INSERT ─────────────────────────────────────────────────────────────────

    /** Thêm mới một kỳ học. Nếu trùng PK, bỏ qua (IGNORE). */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(SemesterEntity semester);

    /**
     * Upsert danh sách kỳ học — dùng khi sync toàn bộ cache từ server.
     * REPLACE = delete + insert, giữ dữ liệu luôn mới nhất.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<SemesterEntity> semesters);

    // ─── UPDATE ─────────────────────────────────────────────────────────────────

    /** Cập nhật thông tin một kỳ học (match theo PK). */
    @Update
    void update(SemesterEntity semester);

    // ─── DELETE ─────────────────────────────────────────────────────────────────

    /** Xoá một kỳ học khỏi cache. */
    @Delete
    void delete(SemesterEntity semester);

    /** Xoá toàn bộ cache kỳ học của một user (dùng khi logout). */
    @Query("DELETE FROM semester WHERE user_id = :userId")
    void deleteAllByUserId(long userId);

    // ─── QUERY ──────────────────────────────────────────────────────────────────

    /**
     * Lấy toàn bộ kỳ học của một sinh viên, sắp xếp theo năm học và số kỳ.
     * Trả về LiveData để UI tự cập nhật khi cache thay đổi.
     */
    @Query("SELECT * FROM semester WHERE user_id = :userId " +
            "ORDER BY academic_year ASC, semester_number ASC")
    LiveData<List<SemesterEntity>> getSemestersByUserId(long userId);

    /**
     * Lấy kỳ học theo ID (dùng cho detail screen).
     */
    @Query("SELECT * FROM semester WHERE semester_id = :semesterId LIMIT 1")
    SemesterEntity getSemesterById(long semesterId);

    /**
     * Lấy kỳ học mới nhất của user (semester_number lớn nhất trong năm học lớn nhất).
     */
    @Query("SELECT * FROM semester WHERE user_id = :userId " +
            "ORDER BY academic_year DESC, semester_number DESC LIMIT 1")
    SemesterEntity getLatestSemester(long userId);

    /**
     * Đếm số kỳ học đang được cache cho một user.
     * Dùng để kiểm tra cache có cần refresh không.
     */
    @Query("SELECT COUNT(*) FROM semester WHERE user_id = :userId")
    int countByUserId(long userId);
}