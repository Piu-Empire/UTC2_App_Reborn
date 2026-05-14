package com.utc2.appreborn.ui.login;

/**
 * User
 * ──────────────────────────────────────────────────────────────
 * Model đại diện cho bảng USER trong MySQL schema.
 *
 * MySQL schema:
 *   TABLE USER (
 *     user_id       BIGINT PK AUTO_INCREMENT,
 *     email         VARCHAR(255) UNIQUE,
 *     password_hash VARCHAR(255),
 *     auth_provider VARCHAR(50),   -- "EMAIL" | "GOOGLE"
 *     created_at    TIMESTAMP,
 *     updated_at    TIMESTAMP
 *   )
 *
 * Dùng trong LoginActivity để truyền thông tin user sau khi xác thực
 * thành công (Email/Password hoặc Google Sign-In).
 */
public class User {

    private long userId;          // user_id  — BIGINT PK
    private String email;         // email    — VARCHAR(255) UNIQUE
    private String passwordHash;  // password_hash — VARCHAR(255), NULL nếu login Google
    private String authProvider;  // auth_provider — "EMAIL" | "GOOGLE"
    private String createdAt;     // created_at — TIMESTAMP (ISO-8601 string)
    private String updatedAt;     // updated_at — TIMESTAMP (ISO-8601 string)

    /** Constructor mặc định */
    public User() {}

    /**
     * Constructor đầy đủ — dùng khi map response từ API hoặc Room.
     */
    public User(long userId, String email, String authProvider) {
        this.userId = userId;
        this.email = email;
        this.authProvider = authProvider;
    }

    // ── Getters ───────────────────────────────────────────────

    public long getUserId()         { return userId; }
    public String getEmail()        { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getAuthProvider() { return authProvider; }
    public String getCreatedAt()    { return createdAt; }
    public String getUpdatedAt()    { return updatedAt; }

    // ── Setters ───────────────────────────────────────────────

    public void setUserId(long userId)           { this.userId = userId; }
    public void setEmail(String email)           { this.email = email; }
    public void setPasswordHash(String hash)     { this.passwordHash = hash; }
    public void setAuthProvider(String provider) { this.authProvider = provider; }
    public void setCreatedAt(String createdAt)   { this.createdAt = createdAt; }
    public void setUpdatedAt(String updatedAt)   { this.updatedAt = updatedAt; }
}