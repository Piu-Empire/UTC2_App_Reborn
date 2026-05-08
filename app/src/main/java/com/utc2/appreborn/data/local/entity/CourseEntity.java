package com.utc2.appreborn.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


// Chú thích Entity phải nằm TRÊN class như thế này
@Entity(tableName = "course")
public class CourseEntity {

    @PrimaryKey
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
}

