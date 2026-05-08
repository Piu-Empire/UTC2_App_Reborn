package com.utc2.appreborn.ui.tuition.model;

public class SubjectTuition extends Tuition {
    // ID này thường là Khóa chính (PRIMARY KEY) tự tăng trong database
    private int id;

    public SubjectTuition(int id, String name, String details, long amount, int status) {
        // super() đẩy dữ liệu lên lớp cha để quản lý tập trung
        super(name, details, amount, status);
        this.id = id;
    }

    @Override
    public String getIdentifier() {
        // Trả về mã định danh để hiển thị lên giao diện hoặc dùng trong câu lệnh SQL (WHERE id = ...)
        return "SUBJ-" + id;
    }

    public int getId() { return id; }
}