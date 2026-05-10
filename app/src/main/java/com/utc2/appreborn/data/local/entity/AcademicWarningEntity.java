package com.utc2.appreborn.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * AcademicWarningEntity - Room cache entity ánh xạ từ bảng ACADEMIC_WARNING trên MySQL.
 *
 * Chú ý kiểu dữ liệu:
 *  - issued_at  (TIMESTAMP) → Long  (Unix epoch milliseconds, tiện sort/compare)
 *  - resolved_at (TIMESTAMP) → Long (Unix epoch milliseconds)
 *
 * Foreign Key:
 *  - semester_id tham chiếu SemesterEntity(semester_id)
 *    → deferred = true vì cache có thể insert theo thứ tự bất kỳ
 */
@Entity(
        tableName = "academic_warning",
        indices = {
                @Index("user_id"),
                @Index("semester_id")
        },
        foreignKeys = {
                @ForeignKey(
                        entity       = SemesterEntity.class,
                        parentColumns = "semester_id",
                        childColumns  = "semester_id",
                        onDelete      = ForeignKey.CASCADE,
                        deferred      = true
                )
        }
)
public class AcademicWarningEntity {

    @PrimaryKey
    @ColumnInfo(name = "warning_id")
    private long warningId;

    @ColumnInfo(name = "user_id")
    private long userId;

    @ColumnInfo(name = "semester_id")
    private long semesterId;

    @ColumnInfo(name = "warning_type")
    private String warningType;

    /** Mô tả chi tiết cảnh báo, ánh xạ từ kiểu TEXT của MySQL */
    @ColumnInfo(name = "description")
    private String description;

    /**
     * Thời điểm phát sinh cảnh báo.
     * Lưu dạng Unix epoch (ms) — dùng {@code System.currentTimeMillis()} hoặc
     * parse từ ISO-8601 string trả về bởi API.
     */
    @ColumnInfo(name = "issued_at")
    private Long issuedAt;

    /**
     * Thời điểm giải quyết cảnh báo.
     * Nullable vì cảnh báo có thể chưa được giải quyết.
     */
    @ColumnInfo(name = "resolved_at")
    private Long resolvedAt;

    @ColumnInfo(name = "status")
    private String status;

    // ─── Constructor ────────────────────────────────────────────────────────────

    public AcademicWarningEntity(long warningId, long userId, long semesterId,
                                 String warningType, String description,
                                 Long issuedAt, Long resolvedAt, String status) {
        this.warningId   = warningId;
        this.userId      = userId;
        this.semesterId  = semesterId;
        this.warningType = warningType;
        this.description = description;
        this.issuedAt    = issuedAt;
        this.resolvedAt  = resolvedAt;
        this.status      = status;
    }

    // ─── Getters & Setters ───────────────────────────────────────────────────────

    public long getWarningId()               { return warningId; }
    public void setWarningId(long v)         { this.warningId = v; }

    public long getUserId()                  { return userId; }
    public void setUserId(long v)            { this.userId = v; }

    public long getSemesterId()              { return semesterId; }
    public void setSemesterId(long v)        { this.semesterId = v; }

    public String getWarningType()           { return warningType; }
    public void setWarningType(String v)     { this.warningType = v; }

    public String getDescription()           { return description; }
    public void setDescription(String v)     { this.description = v; }

    public Long getIssuedAt()                { return issuedAt; }
    public void setIssuedAt(Long v)          { this.issuedAt = v; }

    public Long getResolvedAt()              { return resolvedAt; }
    public void setResolvedAt(Long v)        { this.resolvedAt = v; }

    public String getStatus()                { return status; }
    public void setStatus(String v)          { this.status = v; }
}