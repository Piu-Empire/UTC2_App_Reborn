package com.utc2.appreborn.ui.profile.model;

/**
 * UserProfile
 * ──────────────────────────────────────────────────────────────
 * Model gộp dữ liệu từ TABLE USER và TABLE USER_PROFILE.
 * Dùng trong InfoFragment để hiển thị thông tin cá nhân.
 *
 * THÊM MỚI — file này chưa tồn tại trong project.
 * Hiện tại InfoFragment đang dùng MockHelper + StudentInfoItem (label/value)
 * để hiển thị rời từng trường. Model này cung cấp POJO đầy đủ để sau này
 * thay thế MockHelper bằng Room query hoặc Retrofit call.
 *
 * TABLE USER:
 *   user_id        BIGINT PK
 *   email          VARCHAR(255)
 *   auth_provider  VARCHAR(50)   "EMAIL" | "GOOGLE" | "MICROSOFT"
 *   role           VARCHAR(20)   "STUDENT" | "ADMIN"
 *   is_active      BOOLEAN
 *
 * TABLE USER_PROFILE:
 *   user_id            BIGINT PK/FK → USER
 *   full_name          VARCHAR(150)
 *   phone_number       VARCHAR(20)
 *   avatar_url         VARCHAR(500)
 *   date_of_birth      DATE
 *   gender             VARCHAR(10)   "MALE" | "FEMALE" | "OTHER"
 *   id_card_number     VARCHAR(20)   CCCD/CMND
 *   permanent_address  VARCHAR(500)
 */
public class UserProfile {

    // ── TABLE USER ────────────────────────────────────────────
    private long    userId;          // USER.user_id
    private String  email;           // USER.email
    private String  authProvider;    // USER.auth_provider
    private String  role;            // USER.role
    private boolean isActive;        // USER.is_active

    // ── TABLE USER_PROFILE ────────────────────────────────────
    private String  fullName;          // USER_PROFILE.full_name
    private String  phoneNumber;       // USER_PROFILE.phone_number
    private String  avatarUrl;         // USER_PROFILE.avatar_url
    private String  dateOfBirth;       // USER_PROFILE.date_of_birth  "yyyy-MM-dd"
    private String  gender;            // USER_PROFILE.gender
    private String  idCardNumber;      // USER_PROFILE.id_card_number  (CCCD/CMND)
    private String  permanentAddress;  // USER_PROFILE.permanent_address

    // ── Hằng auth_provider ───────────────────────────────────
    public static final String PROVIDER_EMAIL     = "EMAIL";
    public static final String PROVIDER_GOOGLE    = "GOOGLE";
    public static final String PROVIDER_MICROSOFT = "MICROSOFT";

    // ── Hằng role ────────────────────────────────────────────
    public static final String ROLE_STUDENT = "STUDENT";
    public static final String ROLE_ADMIN   = "ADMIN";

    // ── Hằng gender ──────────────────────────────────────────
    public static final String GENDER_MALE   = "MALE";
    public static final String GENDER_FEMALE = "FEMALE";
    public static final String GENDER_OTHER  = "OTHER";

    public UserProfile() {}

    public UserProfile(long userId, String email, String authProvider, String role,
                       boolean isActive, String fullName, String phoneNumber,
                       String avatarUrl, String dateOfBirth, String gender,
                       String idCardNumber, String permanentAddress) {
        this.userId           = userId;
        this.email            = email;
        this.authProvider     = authProvider;
        this.role             = role;
        this.isActive         = isActive;
        this.fullName         = fullName;
        this.phoneNumber      = phoneNumber;
        this.avatarUrl        = avatarUrl;
        this.dateOfBirth      = dateOfBirth;
        this.gender           = gender;
        this.idCardNumber     = idCardNumber;
        this.permanentAddress = permanentAddress;
    }

    // ── Getters ──────────────────────────────────────────────

    public long    getUserId()           { return userId; }
    public String  getEmail()            { return email; }
    public String  getAuthProvider()     { return authProvider; }
    public String  getRole()             { return role; }
    public boolean isActive()            { return isActive; }
    public String  getFullName()         { return fullName; }
    public String  getPhoneNumber()      { return phoneNumber; }
    public String  getAvatarUrl()        { return avatarUrl; }
    public String  getDateOfBirth()      { return dateOfBirth; }
    public String  getGender()           { return gender; }
    /** CCCD/CMND — dùng cho dịch vụ hành chính. */
    public String  getIdCardNumber()     { return idCardNumber; }
    /** Địa chỉ thường trú — dùng cho đăng ký KTX. */
    public String  getPermanentAddress() { return permanentAddress; }

    // ── Setters ──────────────────────────────────────────────

    public void setUserId(long userId)                       { this.userId = userId; }
    public void setEmail(String email)                       { this.email = email; }
    public void setAuthProvider(String authProvider)         { this.authProvider = authProvider; }
    public void setRole(String role)                         { this.role = role; }
    public void setActive(boolean active)                    { isActive = active; }
    public void setFullName(String fullName)                 { this.fullName = fullName; }
    public void setPhoneNumber(String phoneNumber)           { this.phoneNumber = phoneNumber; }
    public void setAvatarUrl(String avatarUrl)               { this.avatarUrl = avatarUrl; }
    public void setDateOfBirth(String dateOfBirth)           { this.dateOfBirth = dateOfBirth; }
    public void setGender(String gender)                     { this.gender = gender; }
    public void setIdCardNumber(String idCardNumber)         { this.idCardNumber = idCardNumber; }
    public void setPermanentAddress(String permanentAddress) { this.permanentAddress = permanentAddress; }

    /** Tiện ích: true nếu đăng nhập bằng Google. */
    public boolean isGoogleAuth() { return PROVIDER_GOOGLE.equals(authProvider); }

    /** Tiện ích: true nếu là sinh viên. */
    public boolean isStudent() { return ROLE_STUDENT.equals(role); }
}