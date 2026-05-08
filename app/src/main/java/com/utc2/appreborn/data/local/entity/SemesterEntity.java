package com.utc2.appreborn.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "semester")
public class SemesterEntity {

    @PrimaryKey
    public long semesterId;

    @ColumnInfo(name = "semester_name")
    public String semesterName;

    @ColumnInfo(name = "semester_number")
    public int semesterNumber;

    @ColumnInfo(name = "academic_year")
    public String academicYear;

    @ColumnInfo(name = "start_date")
    public String startDate;

    @ColumnInfo(name = "end_date")
    public String endDate;
}