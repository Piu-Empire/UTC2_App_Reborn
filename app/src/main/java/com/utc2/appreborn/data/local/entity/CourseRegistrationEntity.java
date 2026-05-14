package com.utc2.appreborn.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * CourseRegistrationEntity - Room cache entity ánh xạ từ bảng COURSE_REGISTRATION.
 *
 * Ánh xạ từ model: ui/courseregistration/model/CourseRegistration.java
 *
 * Lưu ý:
 *  - course_id là String (VD: "c1", "c2") — khớp với id trong CourseRepository
 *  - user_id liên kết với sinh viên đang đăng nhập
 *  - Kết hợp (user_id + course_id) là UNIQUE → không đăng ký trùng
 *
 * Bảng: course_registration
 */
@Entity(
        tableName = "course_registration",
        indices = {
                @Index(value = {"user_id", "course_id"}, unique = true),
                @Index("user_id")
        }
)
public class CourseRegistrationEntity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "reg_id")
    public String regId = "";            // VD: "reg_1234567890"

    @ColumnInfo(name = "user_id")
    public long userId;             // FK → user_profile

    @ColumnInfo(name = "course_id")
    public String courseId;         // VD: "c1", "c2" — khớp với CourseRepository

    @ColumnInfo(name = "course_code")
    public String courseCode;       // VD: "IT0588485"

    @ColumnInfo(name = "course_name")
    public String courseName;       // Tên học phần

    @ColumnInfo(name = "credits")
    public int credits;             // Số tín chỉ

    @ColumnInfo(name = "lecturer")
    public String lecturer;         // Tên giảng viên

    @ColumnInfo(name = "schedule")
    public String schedule;         // Lịch học, VD: "T2, T4 (7:00-9:30)"

    @ColumnInfo(name = "room")
    public String room;             // Phòng học

    @ColumnInfo(name = "semester")
    public String semester;         // "HK1", "HK2", "HK3"

    @ColumnInfo(name = "status")
    public String status;           // "PENDING" / "CONFIRMED" / "CANCELLED"

    @ColumnInfo(name = "registered_at")
    public long registeredAt;       // Unix epoch (ms) — thời điểm đăng ký

    // ── Constructor mặc định (bắt buộc với Room) ──────────────────────────────
    public CourseRegistrationEntity() {}

    // ── Getters & Setters ─────────────────────────────────────────────────────
    public String getRegId()            { return regId; }
    public void setRegId(String v)      { this.regId = v; }

    public long getUserId()             { return userId; }
    public void setUserId(long v)       { this.userId = v; }

    public String getCourseId()         { return courseId; }
    public void setCourseId(String v)   { this.courseId = v; }

    public String getCourseCode()           { return courseCode; }
    public void setCourseCode(String v)     { this.courseCode = v; }

    public String getCourseName()           { return courseName; }
    public void setCourseName(String v)     { this.courseName = v; }

    public int getCredits()             { return credits; }
    public void setCredits(int v)       { this.credits = v; }

    public String getLecturer()         { return lecturer; }
    public void setLecturer(String v)   { this.lecturer = v; }

    public String getSchedule()         { return schedule; }
    public void setSchedule(String v)   { this.schedule = v; }

    public String getRoom()             { return room; }
    public void setRoom(String v)       { this.room = v; }

    public String getSemester()         { return semester; }
    public void setSemester(String v)   { this.semester = v; }

    public String getStatus()           { return status; }
    public void setStatus(String v)     { this.status = v; }

    public long getRegisteredAt()           { return registeredAt; }
    public void setRegisteredAt(long v)     { this.registeredAt = v; }
}