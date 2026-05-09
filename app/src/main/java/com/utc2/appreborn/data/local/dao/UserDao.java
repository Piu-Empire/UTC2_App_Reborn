package com.utc2.appreborn.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.utc2.appreborn.data.local.entity.StudentProfileEntity;
import com.utc2.appreborn.data.local.entity.UserEntity;

/**
 * UserDao
 * ──────────────────────────────────────────────────────────────
 * Room DAO cho bảng user_profile.
 *
 * Tất cả query trả về LiveData để UI tự cập nhật khi DB thay đổi.
 * Các hàm write (insert/update/delete) chạy trên background thread
 * — gọi từ ViewModel qua Executor hoặc coroutine.
 *
 * Package: com.utc2.appreborn.data.local
 */
@Dao
public interface UserDao {

// ══════════════════════════════════════════════════════════
    // INSERT
    // ══════════════════════════════════════════════════════════

    /**
     * Chèn user mới.
     * Nếu đã tồn tại → REPLACE (upsert).
     *
     * @return rowId vừa insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertUser(UserEntity user);

    /**
     * Insert student profile.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertStudentProfile(StudentProfileEntity profile);

    // ══════════════════════════════════════════════════════════
    // READ — USER
    // ══════════════════════════════════════════════════════════

    /**
     * Lấy user theo ID — LiveData.
     */
    @Query("SELECT * FROM user_profile WHERE user_id = :userId LIMIT 1")
    LiveData<UserEntity> getUserById(long userId);

    /**
     * Lấy user theo email — LiveData.
     */
    @Query("SELECT * FROM user_profile WHERE email = :email LIMIT 1")
    LiveData<UserEntity> getUserByEmail(String email);

    /**
     * Query sync theo userId.
     */
    @Query("SELECT * FROM user_profile WHERE user_id = :userId LIMIT 1")
    UserEntity getUserByIdSync(long userId);

    /**
     * Query sync theo email.
     */
    @Query("SELECT * FROM user_profile WHERE email = :email LIMIT 1")
    UserEntity getUserByEmailSync(String email);

    /**
     * Đếm tổng user.
     */
    @Query("SELECT COUNT(*) FROM user_profile")
    int countUsers();

    // ══════════════════════════════════════════════════════════
    // READ — STUDENT PROFILE
    // ══════════════════════════════════════════════════════════

    /**
     * Lấy student profile theo userId.
     */
    @Query("SELECT * FROM student_profile WHERE user_id = :userId LIMIT 1")
    StudentProfileEntity getStudentProfileByUserId(long userId);

    /**
     * Lấy student profile theo MSSV.
     */
    @Query("SELECT * FROM student_profile WHERE student_code = :studentCode LIMIT 1")
    StudentProfileEntity getStudentProfileByCode(String studentCode);

    // ══════════════════════════════════════════════════════════
    // UPDATE
    // ══════════════════════════════════════════════════════════

    /**
     * Update toàn bộ user.
     *
     * @return số row bị ảnh hưởng
     */
    @Update
    int updateUser(UserEntity user);

    /**
     * Cập nhật chỉ tên hiển thị — dùng khi user đổi tên.
     */
    @Query(
            "UPDATE user_profile " +
                    "SET full_name = :fullName, updated_at = :updatedAt " +
                    "WHERE user_id = :userId"
    )
    void updateFullName(
            long userId,
            String fullName,
            String updatedAt
    );

    /**
     * Cập nhật avatar URL sau khi upload ảnh.
     */
    @Query(
            "UPDATE user_profile " +
                    "SET avatar_url = :url " +
                    "WHERE user_id = :userId"
    )
    void updateAvatarUrl(
            long userId,
            String url
    );

    // ══════════════════════════════════════════════════════════
    // DELETE
    // ══════════════════════════════════════════════════════════

    /**
     * Xóa user object.
     */
    @Delete
    void deleteUser(UserEntity user);

    /**
     * Xóa theo userId.
     */
    @Query("DELETE FROM user_profile WHERE user_id = :userId")
    void deleteUserById(long userId);

    /**
     * Xóa toàn bộ user.
     */
    @Query("DELETE FROM user_profile")
    void deleteAllUsers();
}