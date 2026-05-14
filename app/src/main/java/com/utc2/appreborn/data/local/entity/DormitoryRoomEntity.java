package com.utc2.appreborn.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * DormitoryRoomEntity — ánh xạ bảng DORMITORY_ROOM (Phòng KTX).
 *
 * Quan hệ:
 *  DORMITORY_ROOM ──< DORMITORY_REGISTRATION
 *
 * Ánh xạ từ model: ui/dormitory/model/Room.java
 */
@Entity(tableName = "dormitory_room")
public class DormitoryRoomEntity {

    @PrimaryKey
    @ColumnInfo(name = "room_id")
    public long roomId;

    /** Tên phòng — VD: "Phòng 201" */
    @ColumnInfo(name = "room_name")
    public String roomName;

    /** Tòa nhà — "A", "B", "C" */
    @ColumnInfo(name = "building")
    public String building;

    /** Sức chứa tối đa (số người) */
    @ColumnInfo(name = "capacity")
    public int capacity;

    /** Số người đang ở hiện tại */
    @ColumnInfo(name = "current_occupants")
    public int currentOccupants;

    /** Giá mỗi tháng (VND) */
    @ColumnInfo(name = "price_per_month")
    public int pricePerMonth;

    /** "NAM" / "NU" */
    @ColumnInfo(name = "room_type")
    public String roomType;

    /** "AVAILABLE" / "FULL" / "MAINTENANCE" */
    @ColumnInfo(name = "status")
    public String status;

    /** Mô tả thêm (tiện nghi, tầng, ...) */
    @ColumnInfo(name = "description")
    public String description;

    public DormitoryRoomEntity() {}
}