package com.utc2.appreborn.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "schedule",
        foreignKeys = {
                @ForeignKey(
                        entity = CourseEntity.class,
                        parentColumns = "courseId",
                        childColumns = "courseId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = SemesterEntity.class,
                        parentColumns = "semesterId",
                        childColumns = "semesterId",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {
                @Index("courseId"),
                @Index("semesterId")
        }
)
public class ScheduleEntity {

    @PrimaryKey
    public long scheduleId;

    public long courseId;
    public long semesterId;

    @ColumnInfo(name = "day_of_week")
    public int dayOfWeek;

    @ColumnInfo(name = "start_period")
    public int startPeriod;

    @ColumnInfo(name = "end_period")
    public int endPeriod;

    @ColumnInfo(name = "start_time")
    public String startTime;

    @ColumnInfo(name = "end_time")
    public String endTime;

    public String room;
    public String building;

    @ColumnInfo(name = "lecturer_name")
    public String lecturerName;

    @ColumnInfo(name = "week_start")
    public int weekStart;

    @ColumnInfo(name = "week_end")
    public int weekEnd;
}