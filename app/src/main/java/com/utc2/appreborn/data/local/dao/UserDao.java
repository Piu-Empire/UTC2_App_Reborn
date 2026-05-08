package com.utc2.appreborn.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

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
    //  CREATE
    // ══════════════════════════════════════════════════════════

    /**
     * Chèn user mới. Nếu user_id đã tồn tại → REPLACE (upsert).
     * @return row ID vừa insert (= user_id)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertUser(UserEntity user);

    // ══════════════════════════════════════════════════════════
    //  READ
    // ══════════════════════════════════════════════════════════

    /**
     * Lấy user theo ID — trả LiveData để Fragment/ViewModel observe.
     */
    @Query("SELECT * FROM user_profile WHERE user_id = :userId LIMIT 1")
    LiveData<UserEntity> getUserById(long userId);

    /**
     * Lấy user theo email — dùng khi login bằng email/password.
     */
    @Query("SELECT * FROM user_profile WHERE email = :email LIMIT 1")
    LiveData<UserEntity> getUserByEmail(String email);

    /**
     * Phiên bản đồng bộ (không LiveData) — dùng trong background thread.
     */
    @Query("SELECT * FROM user_profile WHERE user_id = :userId LIMIT 1")
    UserEntity getUserByIdSync(long userId);

    /**
     * Đếm số user — dùng để kiểm tra DB có dữ liệu chưa.
     */
    @Query("SELECT COUNT(*) FROM user_profile")
    int countUsers();

    // ══════════════════════════════════════════════════════════
    //  UPDATE
    // ══════════════════════════════════════════════════════════

    /**
     * Cập nhật toàn bộ thông tin user.
     * @return số row bị ảnh hưởng
     */
    @Update
    int updateUser(UserEntity user);

    /**
     * Cập nhật chỉ tên hiển thị — dùng khi user đổi tên.
     */
    @Query("UPDATE user_profile SET full_name = :fullName, updated_at = :updatedAt "
            + "WHERE user_id = :userId")
    void updateFullName(long userId, String fullName, String updatedAt);

    /**
     * Cập nhật avatar URL sau khi upload ảnh.
     */
    @Query("UPDATE user_profile SET avatar_url = :url WHERE user_id = :userId")
    void updateAvatarUrl(long userId, String url);

    // ══════════════════════════════════════════════════════════
    //  DELETE
    // ══════════════════════════════════════════════════════════

    /**
     * Xoá một user (và cascade xoá StudentEntity liên quan).
     */
    @Delete
    void deleteUser(UserEntity user);

    /**
     * Xoá tất cả dữ liệu — dùng khi logout để clear local cache.
     */
    @Query("DELETE FROM user_profile")
    void deleteAllUsers();
}