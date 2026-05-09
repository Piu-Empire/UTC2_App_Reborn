package com.utc2.appreborn.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * SemesterEntity - Room cache entity ánh xạ từ bảng SEMESTER trên MySQL.
 *
 * Chú ý kiểu dữ liệu:
 *  - start_date / end_date (DATE)   → String  (format "yyyy-MM-dd", dễ parse/hiển thị)
 *  - gpa (DECIMAL 4,2)              → double
 *  - Các số nguyên giữ nguyên int/long
 */
@Entity(tableName = "semester")
public class SemesterEntity {

    @PrimaryKey
    @ColumnInfo(name = "semester_id")
    public long semesterId;

    @ColumnInfo(name = "user_id")
    private long userId;

    @ColumnInfo(name = "semester_name")
    public String semesterName;

    @ColumnInfo(name = "semester_number")
    public int semesterNumber;

    @ColumnInfo(name = "academic_year")
    public String academicYear;

    /** Ngày bắt đầu kỳ học, lưu dạng "yyyy-MM-dd" */
    @ColumnInfo(name = "start_date")
    public String startDate;

    /** Ngày kết thúc kỳ học, lưu dạng "yyyy-MM-dd" */
    @ColumnInfo(name = "end_date")
    public String endDate;

    /** GPA của kỳ học, thang 4.0 */
    @ColumnInfo(name = "gpa")
    private double gpa;

    @ColumnInfo(name = "total_credits")
    private int totalCredits;

    @ColumnInfo(name = "passed_credits")
    private int passedCredits;

    // ─── Constructor ────────────────────────────────────────────────────────────

    public SemesterEntity(long semesterId, long userId, String semesterName,
                          String academicYear, int semesterNumber,
                          String startDate, String endDate,
                          double gpa, int totalCredits, int passedCredits) {
        this.semesterId    = semesterId;
        this.userId        = userId;
        this.semesterName  = semesterName;
        this.academicYear  = academicYear;
        this.semesterNumber = semesterNumber;
        this.startDate     = startDate;
        this.endDate       = endDate;
        this.gpa           = gpa;
        this.totalCredits  = totalCredits;
        this.passedCredits = passedCredits;
    }

    // ─── Getters & Setters ───────────────────────────────────────────────────────

    public long getSemesterId()       { return semesterId; }
    public void setSemesterId(long v) { this.semesterId = v; }

    public long getUserId()           { return userId; }
    public void setUserId(long v)     { this.userId = v; }

    public String getSemesterName()           { return semesterName; }
    public void setSemesterName(String v)     { this.semesterName = v; }

    public String getAcademicYear()           { return academicYear; }
    public void setAcademicYear(String v)     { this.academicYear = v; }

    public int getSemesterNumber()            { return semesterNumber; }
    public void setSemesterNumber(int v)      { this.semesterNumber = v; }

    public String getStartDate()              { return startDate; }
    public void setStartDate(String v)        { this.startDate = v; }

    public String getEndDate()                { return endDate; }
    public void setEndDate(String v)          { this.endDate = v; }

    public double getGpa()                    { return gpa; }
    public void setGpa(double v)              { this.gpa = v; }

    public int getTotalCredits()              { return totalCredits; }
    public void setTotalCredits(int v)        { this.totalCredits = v; }

    public int getPassedCredits()             { return passedCredits; }
    public void setPassedCredits(int v)       { this.passedCredits = v; }
}