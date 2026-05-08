package com.utc2.appreborn.data.local.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

public class ScheduleWithCourse {

    @Embedded
    public ScheduleEntity schedule;

    @Relation(
            parentColumn = "courseId",
            entityColumn = "courseId"
    )
    public CourseEntity course;

    @Relation(
            parentColumn = "semesterId",
            entityColumn = "semesterId"
    )
    public SemesterEntity semester;
}