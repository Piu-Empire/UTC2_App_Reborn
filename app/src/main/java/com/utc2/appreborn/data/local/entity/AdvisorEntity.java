// PATH: app/src/main/java/com/utc2/appreborn/data/local/entity/AdvisorEntity.java

package com.utc2.appreborn.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "advisor")
public class AdvisorEntity {

    @PrimaryKey
    @ColumnInfo(name = "advisor_id")
    public long advisorId;

    /** Họ và tên Cố vấn học tập */
    @ColumnInfo(name = "full_name")
    public String fullName;

    /** Email liên hệ công việc */
    @ColumnInfo(name = "email")
    public String email;

    /** Số điện thoại liên hệ */
    @ColumnInfo(name = "phone")
    public String phone;

    /** Khoa trực thuộc */
    @ColumnInfo(name = "faculty")
    public String faculty;

    /** Phòng làm việc */
    @ColumnInfo(name = "office_room")
    public String officeRoom;
}