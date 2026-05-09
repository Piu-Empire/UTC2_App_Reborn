package com.utc2.appreborn.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * CourseEntity - Room cache entity ánh xạ từ bảng COURSE trên MySQL.
 *
 * Chú ý:
 *  - course_code có ràng buộc UNIQUE → khai báo index unique trong @Entity
 *  - description (TEXT) → String, Room không giới hạn độ dài String
 */
@Entity(
        tableName = "course",
        indices = { @Index(value = "course_code", unique = true) }
)
public class CourseEntity {

    @PrimaryKey
    @ColumnInfo(name = "course_id")
    public long courseId;

    @ColumnInfo(name = "course_code")
    public String courseCode;

    @ColumnInfo(name = "course_name")
    public String courseName;

    public int credits;

    @ColumnInfo(name = "theory_hours")
    public int theoryHours;

    @ColumnInfo(name = "practice_hours")
    public int practiceHours;

    public String department;

    @ColumnInfo(name = "description")
    private String description;


    public CourseEntity(long courseId, String courseCode, String courseName,
                        int credits, int theoryHours, int practiceHours,
                        String department, String description) {
        this.courseId      = courseId;
        this.courseCode    = courseCode;
        this.courseName    = courseName;
        this.credits       = credits;
        this.theoryHours   = theoryHours;
        this.practiceHours = practiceHours;
        this.department    = department;
        this.description   = description;
    }

    // ─── Getters & Setters ───────────────────────────────────────────────────────

    public long getCourseId()               { return courseId; }
    public void setCourseId(long v)         { this.courseId = v; }

    public String getCourseCode()           { return courseCode; }
    public void setCourseCode(String v)     { this.courseCode = v; }

    public String getCourseName()           { return courseName; }
    public void setCourseName(String v)     { this.courseName = v; }

    public int getCredits()                 { return credits; }
    public void setCredits(int v)           { this.credits = v; }

    public int getTheoryHours()             { return theoryHours; }
    public void setTheoryHours(int v)       { this.theoryHours = v; }

    public int getPracticeHours()           { return practiceHours; }
    public void setPracticeHours(int v)     { this.practiceHours = v; }

    public String getDepartment()           { return department; }
    public void setDepartment(String v)     { this.department = v; }

    public String getDescription()          { return description; }
    public void setDescription(String v)    { this.description = v; }
}