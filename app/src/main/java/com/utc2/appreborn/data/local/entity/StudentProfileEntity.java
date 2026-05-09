// PATH: app/src/main/java/com/utc2/appreborn/data/local/entity/StudentProfileEntity.java

package com.utc2.appreborn.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
/**
 * StudentProfileEntity
 * ──────────────────────────────────────────────────────────────
 * Room Entity mapping bảng STUDENT_PROFILE.
 *
 * MySQL schema:
 *   TABLE STUDENT_PROFILE (
 *     user_id       BIGINT FK→USER,
 *     student_code  VARCHAR(50) UNIQUE,
 *     faculty       VARCHAR(100),
 *     major         VARCHAR(100),
 *     academic_year VARCHAR(50),
 *     class_name    VARCHAR(50),
 *     status        VARCHAR(50),
 *     advisor_id    BIGINT FK→ADVISOR
 *   )
 *
 * Package: com.utc2.appreborn.data.local.entity
 */
@Entity(
        tableName = "student_profile",
        foreignKeys = {
                @ForeignKey(
                        entity = UserEntity.class,
                        parentColumns = "user_id",
                        childColumns = "user_id",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = AdvisorEntity.class,
                        parentColumns = "advisor_id",
                        childColumns = "advisor_id",
                        onDelete = ForeignKey.SET_NULL
                )
        },
        indices = {
                @Index(value = "user_id", unique = true),
                @Index(value = "student_code", unique = true),
                @Index(value = "advisor_id")
        }
)
public class StudentProfileEntity {

    @PrimaryKey
    @ColumnInfo(name = "user_id")
    public long userId;

    @ColumnInfo(name = "full_name")
    public String fullName;

    @ColumnInfo(name = "student_code")
    public String studentCode;

    @ColumnInfo(name = "faculty")
    public String faculty;

    /** Khóa ngoại -> ADVISOR */
    @ColumnInfo(name = "advisor_id")
    public Long advisorId;

    @ColumnInfo(name = "major")
    public String major;

    @ColumnInfo(name = "academic_year")
    public String academicYear;

    @ColumnInfo(name = "class_name")
    public String className;

    @ColumnInfo(name = "status")
    public String status;
    // ── Constructor mặc định (bắt buộc với Room) ─────────────
    public StudentProfileEntity() {}

    // ── Getters & Setters ─────────────────────────────────────

    public long getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public String getFaculty() {
        return faculty;
    }

    public Long getAdvisorId() {
        return advisorId;
    }

    public String getMajor() {
        return major;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public String getClassName() {
        return className;
    }

    public String getStatus() {
        return status;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public void setAdvisorId(Long advisorId) {
        this.advisorId = advisorId;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}