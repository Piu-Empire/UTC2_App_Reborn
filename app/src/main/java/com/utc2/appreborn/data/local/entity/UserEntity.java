package com.utc2.appreborn.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * UserEntity
 * ──────────────────────────────────────────────────────────────
 * Room Entity mapping bảng USER + USER_PROFILE (đã gộp lại cho
 * tiện dùng trong Android — JOIN sẽ xử lý ở DAO nếu cần tách).
 *
 * MySQL schema tương ứng:
 *   TABLE USER         (user_id, email, password_hash, auth_provider)
 *   TABLE USER_PROFILE (user_id, full_name, phone_number, avatar_url,
 *                       date_of_birth, gender)
 *
 * Package: com.utc2.appreborn.data.local
 *
 * ─── Cách dùng ────────────────────────────────────────────────
 * // HIỆN TẠI dùng Mock:
 * UserEntity user = new UserEntity();
 * user.setFullName(MockHelper.getMockFullName());
 *
 * // KHI CÓ DB: bỏ comment dòng dưới, xoá dòng mock bên trên
 * // UserEntity user = userDao.getUserById(currentUserId);
 */
@Entity(
        tableName = "user_profile",
        indices = { @Index(value = "email", unique = true) }
)
public class UserEntity {

    // ── Primary key ───────────────────────────────────────────
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    private long userId;

    // ── TABLE USER ────────────────────────────────────────────
    @ColumnInfo(name = "email")
    private String email;

    /** Không lưu password plaintext — chỉ lưu hash nếu cần offline auth */
    @ColumnInfo(name = "password_hash")
    private String passwordHash;

    /** "EMAIL", "GOOGLE", "FACEBOOK", v.v. */
    @ColumnInfo(name = "auth_provider")
    private String authProvider;

    @ColumnInfo(name = "created_at")
    private String createdAt;

    @ColumnInfo(name = "updated_at")
    private String updatedAt;

    // ── TABLE USER_PROFILE ────────────────────────────────────
    @ColumnInfo(name = "full_name")
    private String fullName;

    @ColumnInfo(name = "phone_number")
    private String phoneNumber;

    @ColumnInfo(name = "avatar_url")
    private String avatarUrl;

    /** Định dạng "yyyy-MM-dd" */
    @ColumnInfo(name = "date_of_birth")
    private String dateOfBirth;

    /** "MALE", "FEMALE", "OTHER" */
    @ColumnInfo(name = "gender")
    private String gender;

    // ── Constructor mặc định (bắt buộc với Room) ─────────────
    public UserEntity() {}

    // ── Getters & Setters ─────────────────────────────────────
    public long   getUserId()       { return userId;       }
    public String getEmail()        { return email;        }
    public String getPasswordHash() { return passwordHash; }
    public String getAuthProvider() { return authProvider; }
    public String getCreatedAt()    { return createdAt;    }
    public String getUpdatedAt()    { return updatedAt;    }
    public String getFullName()     { return fullName;     }
    public String getPhoneNumber()  { return phoneNumber;  }
    public String getAvatarUrl()    { return avatarUrl;    }
    public String getDateOfBirth()  { return dateOfBirth;  }
    public String getGender()       { return gender;       }

    public void setUserId(long v)        { userId       = v; }
    public void setEmail(String v)       { email        = v; }
    public void setPasswordHash(String v){ passwordHash = v; }
    public void setAuthProvider(String v){ authProvider = v; }
    public void setCreatedAt(String v)   { createdAt    = v; }
    public void setUpdatedAt(String v)   { updatedAt    = v; }
    public void setFullName(String v)    { fullName     = v; }
    public void setPhoneNumber(String v) { phoneNumber  = v; }
    public void setAvatarUrl(String v)   { avatarUrl    = v; }
    public void setDateOfBirth(String v) { dateOfBirth  = v; }
    public void setGender(String v)      { gender       = v; }
}