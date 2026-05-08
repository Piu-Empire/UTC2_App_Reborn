package com.utc2.appreborn.ui.dormitory.lookup.model;

/**
 * Lớp đại diện cho một sinh viên đang ở trong phòng KTX.
 *
 * [Chương 3 - OOP]
 *  - Bao đóng: tất cả field private, truy cập qua getter
 */
public class RoomOccupant {

    private final String name;    // Họ và tên
    private final String mssv;    // Mã số sinh viên
    private final String classId; // Lớp học

    public RoomOccupant(String name, String mssv, String classId) {
        this.name    = name;
        this.mssv    = mssv;
        this.classId = classId;
    }

    public String getName()    { return name; }
    public String getMssv()    { return mssv; }
    public String getClassId() { return classId; }
}