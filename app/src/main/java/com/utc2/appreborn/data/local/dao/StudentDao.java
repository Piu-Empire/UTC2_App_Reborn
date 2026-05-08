package com.utc2.appreborn.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.utc2.appreborn.data.local.entity.StudentEntity;

/**
 * StudentDao
 * ──────────────────────────────────────────────────────────────
 * Room DAO cho bảng student_profile.
 *
 * Package: com.utc2.appreborn.data.local
 */
@Dao
public interface StudentDao {

    // ══════════════════════════════════════════════════════════
    //  CREATE
    // ══════════════════════════════════════════════════════════

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertStudent(StudentEntity student);

    // ══════════════════════════════════════════════════════════
    //  READ
    // ══════════════════════════════════════════════════════════

    /**
     * Lấy hồ sơ sinh viên theo user_id — đây là query chính,
     * được gọi sau khi Firebase Auth xác thực thành công.
     */
    @Query("SELECT * FROM student_profile WHERE user_id = :userId LIMIT 1")
    LiveData<StudentEntity> getStudentByUserId(long userId);

    /**
     * Phiên bản đồng bộ — dùng trong background thread
     * (ví dụ: khi generate QR trong QrFragment).
     */
    @Query("SELECT * FROM student_profile WHERE user_id = :userId LIMIT 1")
    StudentEntity getStudentByUserIdSync(long userId);

    /**
     * Lấy theo MSSV (student_code) — dùng khi scan QR để tra cứu.
     */
    @Query("SELECT * FROM student_profile WHERE student_code = :studentCode LIMIT 1")
    LiveData<StudentEntity> getStudentByCode(String studentCode);

    /**
     * Lấy MSSV (String) theo user_id — shortcut tiện lợi
     * thay vì lấy cả StudentEntity khi chỉ cần mã.
     */
    @Query("SELECT student_code FROM student_profile WHERE user_id = :userId LIMIT 1")
    String getStudentCodeSync(long userId);

    /**
     * Đếm — kiểm tra DB đã có dữ liệu sinh viên chưa.
     */
    @Query("SELECT COUNT(*) FROM student_profile")
    int countStudents();

    // ══════════════════════════════════════════════════════════
    //  UPDATE
    // ══════════════════════════════════════════════════════════

    @Update
    int updateStudent(StudentEntity student);

    /**
     * Cập nhật trạng thái sinh viên (ACTIVE, GRADUATED, SUSPENDED...).
     */
    @Query("UPDATE student_profile SET status = :status WHERE user_id = :userId")
    void updateStatus(long userId, String status);

    // ══════════════════════════════════════════════════════════
    //  DELETE
    // ══════════════════════════════════════════════════════════

    @Delete
    void deleteStudent(StudentEntity student);

    /**
     * Xoá hết khi logout — gọi cùng UserDao.deleteAllUsers().
     */
    @Query("DELETE FROM student_profile")
    void deleteAllStudents();
}