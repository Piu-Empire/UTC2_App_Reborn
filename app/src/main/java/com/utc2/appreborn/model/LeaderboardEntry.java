package com.utc2.appreborn.model;

/**
 * LeaderboardEntry - Model cho một dòng trong bảng xếp hạng.
 *
 * Fields:
 *  rank         - Thứ hạng (1, 2, 3, ...)
 *  studentName  - Họ và tên sinh viên
 *  avatarInitials - 2 chữ cái viết tắt tên (VD: "VB" cho Văn Bình)
 *  totalCredits - Tổng số tín chỉ tích lũy
 *  gpa          - Điểm trung bình tích lũy (thang 4)
 *  isCurrentUser - true nếu đây là sinh viên đang đăng nhập
 */
public class LeaderboardEntry {

    private int rank;
    private String studentName;
    private String avatarInitials;
    private int totalCredits;
    private double gpa;
    private boolean isCurrentUser;

    public LeaderboardEntry(int rank, String studentName, String avatarInitials,
                            int totalCredits, double gpa, boolean isCurrentUser) {
        this.rank = rank;
        this.studentName = studentName;
        this.avatarInitials = avatarInitials;
        this.totalCredits = totalCredits;
        this.gpa = gpa;
        this.isCurrentUser = isCurrentUser;
    }

    public int getRank() { return rank; }
    public String getStudentName() { return studentName; }
    public String getAvatarInitials() { return avatarInitials; }
    public int getTotalCredits() { return totalCredits; }
    public double getGpa() { return gpa; }
    public boolean isCurrentUser() { return isCurrentUser; }
}
