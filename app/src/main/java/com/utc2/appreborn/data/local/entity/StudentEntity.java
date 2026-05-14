package com.utc2.appreborn.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * StudentEntity
 * ──────────────────────────────────────────────────────────────
 * Room Entity mapping bảng STUDENT_PROFILE.
 *
 * MySQL schema:
 *   TABLE STUDENT_PROFILE (
 *     user_id       BIGINT FK→USER,
 *     student_code  VARCHAR(50) UNIQUE,   ← MSSV, encode vào QR
 *     faculty       VARCHAR(100),
 *     major         VARCHAR(100),
 *     academic_year VARCHAR(50),
 *     class_name    VARCHAR(50),
 *     status        VARCHAR(50)
 *   )
 *
 * Package: com.utc2.appreborn.data.local
 */
@Entity(
        tableName = "student_profile",
        foreignKeys = @ForeignKey(
                entity    = UserEntity.class,
                parentColumns = "user_id",
                childColumns  = "user_id",
                onDelete  = ForeignKey.CASCADE
        ),
        indices = {
                @Index(value = "user_id",      unique = true),
                @Index(value = "student_code", unique = true)
        }
)
public class StudentEntity {

    @PrimaryKey
    @ColumnInfo(name = "user_id")
    private long userId;

    /** MSSV — chuỗi này được encode vào QR bitmap */
    @ColumnInfo(name = "student_code")
    private String studentCode;

    @ColumnInfo(name = "faculty")
    private String faculty;

    /**
     * advisor_id — FK đến bảng ADVISOR.
     * Lưu dưới dạng Long để tương thích với MySQL BIGINT.
     * Dùng null khi sinh viên chưa có cố vấn.
     */
    @ColumnInfo(name = "advisor_id")
    private Long advisorId;

    @ColumnInfo(name = "major")
    private String major;

    /** VD: "2024", "2023-2024" */
    @ColumnInfo(name = "academic_year")
    private String academicYear;

    /** Tên lớp học, VD: "KTPM65A" */
    @ColumnInfo(name = "class_name")
    private String className;

    /** "ACTIVE", "GRADUATED", "SUSPENDED", v.v. */
    @ColumnInfo(name = "status")
    private String status;

    // ── Constructor mặc định (bắt buộc với Room) ─────────────
    public StudentEntity() {}

    // ── Getters & Setters ─────────────────────────────────────
    public long   getUserId()      { return userId;      }
    public String getStudentCode() { return studentCode; }
    public String getFaculty()     { return faculty;     }
    public Long   getAdvisorId()   { return advisorId;   }
    public String getMajor()       { return major;       }
    public String getAcademicYear(){ return academicYear;}
    public String getClassName()   { return className;   }
    public String getStatus()      { return status;      }

    public void setUserId(long v)       { userId       = v; }
    public void setStudentCode(String v){ studentCode  = v; }
    public void setFaculty(String v)    { faculty      = v; }
    public void setAdvisorId(Long v)    { advisorId    = v; }
    public void setMajor(String v)      { major        = v; }
    public void setAcademicYear(String v){ academicYear= v; }
    public void setClassName(String v)  { className    = v; }
    public void setStatus(String v)     { status       = v; }
}