package com.utc2.appreborn.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.utc2.appreborn.data.local.entity.CourseEntity;

import java.util.List;

/**
 * CourseDao - Room DAO cho bảng "course".
 *
 * Bảng COURSE là dữ liệu catalog (ít thay đổi), nên cache một lần và
 * chỉ refresh khi cần. Các query hỗ trợ tìm kiếm theo tên và mã môn học.
 */
@Dao
public interface CourseDao {

    // ─── INSERT ─────────────────────────────────────────────────────────────────

    /** Thêm mới một môn học. Nếu trùng course_code, bỏ qua. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(CourseEntity course);

    /**
     * Upsert danh sách môn học từ server.
     * REPLACE đảm bảo thông tin môn học luôn được cập nhật.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CourseEntity> courses);

    // ─── UPDATE ─────────────────────────────────────────────────────────────────

    /** Cập nhật thông tin một môn học (match theo PK course_id). */
    @Update
    void update(CourseEntity course);

    // ─── DELETE ─────────────────────────────────────────────────────────────────

    /** Xoá một môn học khỏi cache. */
    @Delete
    void delete(CourseEntity course);

    /** Xoá toàn bộ cache môn học (dùng khi force refresh). */
    @Query("DELETE FROM course")
    void deleteAll();

    // ─── QUERY ──────────────────────────────────────────────────────────────────

    /**
     * Lấy toàn bộ môn học, sắp xếp theo tên.
     * Trả về LiveData để RecyclerView tự cập nhật.
     */
    @Query("SELECT * FROM course ORDER BY course_name ASC")
    LiveData<List<CourseEntity>> getAllCourses();

    /**
     * Tìm môn học theo mã môn (course_code là UNIQUE).
     */
    @Query("SELECT * FROM course WHERE course_code = :courseCode LIMIT 1")
    CourseEntity getCourseByCode(String courseCode);

    /**
     * Tìm môn học theo ID.
     */
    @Query("SELECT * FROM course WHERE course_id = :courseId LIMIT 1")
    CourseEntity getCourseById(long courseId);

    /**
     * Tìm kiếm môn học theo từ khoá (tìm trong tên và mã môn).
     * Dùng cho SearchView — pattern phải được wrap bởi "%keyword%".
     *
     * VD: courseDao.searchCourses("%java%")
     */
    @Query("SELECT * FROM course WHERE course_name LIKE :keyword " +
            "OR course_code LIKE :keyword ORDER BY course_name ASC")
    LiveData<List<CourseEntity>> searchCourses(String keyword);

    /**
     * Lấy danh sách môn học theo khoa/bộ môn.
     */
    @Query("SELECT * FROM course WHERE department = :department ORDER BY course_name ASC")
    LiveData<List<CourseEntity>> getCoursesByDepartment(String department);

    /**
     * Đếm tổng số môn đang cache (kiểm tra cache còn hạn không).
     */
    @Query("SELECT COUNT(*) FROM course")
    int countAll();
}