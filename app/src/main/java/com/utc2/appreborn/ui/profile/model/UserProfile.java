package com.utc2.appreborn.ui.profile.model;

/**
 * UserProfile
 * ──────────────────────────────────────────────────────────────
 * Model gộp dữ liệu từ TABLE USER + TABLE USER_PROFILE.
 * Dùng trong InfoFragment để hiển thị thông tin cá nhân.
 *
 * TABLE USER:
 *   user_id       BIGINT PK
 *   email         VARCHAR(255)
 *   auth_provider VARCHAR(50)
 *   created_at    TIMESTAMP
 *   updated_at    TIMESTAMP
 *
 * TABLE USER_PROFILE:
 *   user_id       BIGINT PK/FK → USER
 *   full_name     VARCHAR(255)
 *   phone_number  VARCHAR(20)
 *   avatar_url    TEXT
 *   date_of_birth DATE
 *   gender        VARCHAR(20)
 */
public class UserProfile {

    // ── Hằng auth_provider ───────────────────────────────────
    public static final String PROVIDER_EMAIL     = "EMAIL";
    public static final String PROVIDER_GOOGLE    = "GOOGLE";
    public static final String PROVIDER_MICROSOFT = "MICROSOFT";

    // ── TABLE USER ────────────────────────────────────────────
    private long   userId;
    private String email;
    private String authProvider;
    private String createdAt;
    private String updatedAt;

    // ── TABLE USER_PROFILE ────────────────────────────────────
    private String fullName;
    private String phoneNumber;
    private String avatarUrl;
    private String dateOfBirth;  // "yyyy-MM-dd"
    private String gender;

    public UserProfile() {}

    public UserProfile(long userId, String email, String authProvider,
                       String fullName, String phoneNumber,
                       String avatarUrl, String dateOfBirth, String gender) {
        this.userId       = userId;
        this.email        = email;
        this.authProvider = authProvider;
        this.fullName     = fullName;
        this.phoneNumber  = phoneNumber;
        this.avatarUrl    = avatarUrl;
        this.dateOfBirth  = dateOfBirth;
        this.gender       = gender;
    }

    // ── Getters ───────────────────────────────────────────────
    public long   getUserId()      { return userId; }
    public String getEmail()       { return email; }
    public String getAuthProvider(){ return authProvider; }
    public String getCreatedAt()   { return createdAt; }
    public String getUpdatedAt()   { return updatedAt; }
    public String getFullName()    { return fullName; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getAvatarUrl()   { return avatarUrl; }
    public String getDateOfBirth() { return dateOfBirth; }
    public String getGender()      { return gender; }

    // ── Setters ───────────────────────────────────────────────
    public void setUserId(long v)        { this.userId = v; }
    public void setEmail(String v)       { this.email = v; }
    public void setAuthProvider(String v){ this.authProvider = v; }
    public void setCreatedAt(String v)   { this.createdAt = v; }
    public void setUpdatedAt(String v)   { this.updatedAt = v; }
    public void setFullName(String v)    { this.fullName = v; }
    public void setPhoneNumber(String v) { this.phoneNumber = v; }
    public void setAvatarUrl(String v)   { this.avatarUrl = v; }
    public void setDateOfBirth(String v) { this.dateOfBirth = v; }
    public void setGender(String v)      { this.gender = v; }

    /** Tiện ích: true nếu đăng nhập bằng Google. */
    public boolean isGoogleAuth() { return PROVIDER_GOOGLE.equals(authProvider); }
}