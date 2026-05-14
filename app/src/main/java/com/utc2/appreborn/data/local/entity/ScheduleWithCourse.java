package com.utc2.appreborn.data.local.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

public class ScheduleWithCourse {

    @Embedded
    public ScheduleEntity schedule;

    @Relation(
            parentColumn = "course_id",
            entityColumn = "course_id"
    )
    public CourseEntity course;

    @Relation(
            parentColumn = "semester_id",
            entityColumn = "semester_id"
    )
    public SemesterEntity semester;
}