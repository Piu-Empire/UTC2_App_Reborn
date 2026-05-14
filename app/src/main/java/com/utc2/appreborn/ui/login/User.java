package com.utc2.appreborn.ui.login;

/**
 * User
 * ──────────────────────────────────────────────────────────────
 * Model mapping TABLE USER (MySQL schema).
 *
 * TABLE USER:
 *   user_id       BIGINT PK AUTO_INCREMENT
 *   email         VARCHAR(255) UNIQUE
 *   password_hash VARCHAR(255)
 *   auth_provider VARCHAR(50)
 *   created_at    TIMESTAMP
 *   updated_at    TIMESTAMP
 */
public class User {

    // ── Hằng auth_provider ───────────────────────────────────
    public static final String PROVIDER_EMAIL     = "EMAIL";
    public static final String PROVIDER_GOOGLE    = "GOOGLE";
    public static final String PROVIDER_MICROSOFT = "MICROSOFT";

    // ── Fields ────────────────────────────────────────────────
    private long   userId;        // user_id
    private String email;         // email
    private String passwordHash;  // password_hash
    private String authProvider;  // auth_provider
    private String createdAt;     // created_at
    private String updatedAt;     // updated_at

    public User() {}

    public User(long userId, String email, String authProvider) {
        this.userId       = userId;
        this.email        = email;
        this.authProvider = authProvider;
    }

    // ── Getters ───────────────────────────────────────────────
    public long   getUserId()       { return userId; }
    public String getEmail()        { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getAuthProvider() { return authProvider; }
    public String getCreatedAt()    { return createdAt; }
    public String getUpdatedAt()    { return updatedAt; }

    // ── Setters ───────────────────────────────────────────────
    public void setUserId(long v)         { this.userId = v; }
    public void setEmail(String v)        { this.email = v; }
    public void setPasswordHash(String v) { this.passwordHash = v; }
    public void setAuthProvider(String v) { this.authProvider = v; }
    public void setCreatedAt(String v)    { this.createdAt = v; }
    public void setUpdatedAt(String v)    { this.updatedAt = v; }

    /** Tiện ích: true nếu đăng nhập bằng Google. */
    public boolean isGoogleAuth() { return PROVIDER_GOOGLE.equals(authProvider); }
}