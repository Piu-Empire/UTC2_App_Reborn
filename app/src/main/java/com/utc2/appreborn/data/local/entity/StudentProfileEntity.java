// PATH: app/src/main/java/com/utc2/appreborn/data/local/entity/StudentProfileEntity.java

package com.utc2.appreborn.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

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
                @Index("user_id"),
                @Index("advisor_id")
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
}