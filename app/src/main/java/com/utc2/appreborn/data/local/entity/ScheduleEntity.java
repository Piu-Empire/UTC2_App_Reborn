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
                        parentColumns = "course_id",
                        childColumns = "course_id",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = SemesterEntity.class,
                        parentColumns = "semester_id",
                        childColumns = "semester_id",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {
                @Index("course_id"),
                @Index("semester_id")
        }
)
public class ScheduleEntity {

    @PrimaryKey
    @ColumnInfo(name = "schedule_id")
    public long scheduleId;

    @ColumnInfo(name = "course_id")
    public long courseId;
    @ColumnInfo(name = "semester_id")
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