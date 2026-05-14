package com.utc2.appreborn.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * DormitoryRegistrationEntity — ánh xạ bảng DORMITORY_REGISTRATION (Đăng ký KTX).
 *
 * FIX: Bỏ Foreign Key tới user_profile vì user chưa chắc tồn tại trong DB
 *      (app dùng userId hardcode hoặc từ SharedPreferences, không insert vào Room).
 *      Chỉ giữ FK tới dormitory_room để đảm bảo tính toàn vẹn dữ liệu phòng.
 */
@Entity(
        tableName = "dormitory_registration",
        foreignKeys = {
                // ❌ BỎ FK user_profile — gây SQLiteConstraintException khi user chưa có trong DB
                // @ForeignKey(
                //         entity        = UserEntity.class,
                //         parentColumns = "user_id",
                //         childColumns  = "user_id",
                //         onDelete      = ForeignKey.CASCADE
                // ),
                @ForeignKey(
                        entity        = DormitoryRoomEntity.class,
                        parentColumns = "room_id",
                        childColumns  = "room_id",
                        onDelete      = ForeignKey.CASCADE
                )
        },
        indices = {
                @Index("user_id"),
                @Index("room_id")
        }
)
public class DormitoryRegistrationEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "registration_id")
    public long registrationId;

    @ColumnInfo(name = "user_id")
    public long userId;

    @ColumnInfo(name = "room_id")
    public long roomId;

    /** Số tháng đăng ký (1–12) */
    @ColumnInfo(name = "months")
    public int months;

    /** Tổng tiền = price_per_month * months */
    @ColumnInfo(name = "total_price")
    public int totalPrice;

    /** "PENDING" / "CONFIRMED" / "CANCELLED" */
    @ColumnInfo(name = "status")
    public String status;

    /** Ngày bắt đầu ở — "yyyy-MM-dd" */
    @ColumnInfo(name = "start_date")
    public String startDate;

    /** Ngày kết thúc — "yyyy-MM-dd" */
    @ColumnInfo(name = "end_date")
    public String endDate;

    /** Unix epoch (ms) — thời điểm tạo đăng ký */
    @ColumnInfo(name = "created_at")
    public long createdAt;

    public DormitoryRegistrationEntity() {}
}