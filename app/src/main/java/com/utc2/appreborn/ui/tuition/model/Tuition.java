package com.utc2.appreborn.ui.tuition.model;

/**
 * Lớp trừu tượng Tuition - Đại diện cho các loại hóa đơn/lệ phí.
 * Việc để abstract giúp đảm bảo không tạo đối tượng Tuition chung chung.
 */
public abstract class Tuition {
    // Các thuộc tính này tương ứng với các COLUMN trong database (MySQL)
    protected String name;    // Tên khoản phí (Ví dụ: "Lập trình Java" hoặc "Phòng 403")
    protected String details; // Mô tả chi tiết (Ví dụ: "Học kỳ 2" hoặc "Tiền điện nước")
    protected long amount;    // Số tiền (Nên dùng BIGINT trong MySQL để tương ứng với long)
    protected int status;     // Trạng thái: 0 - Chưa đóng, 1 - Đã đóng (Tương ứng với TINYINT hoặc INT)

    public Tuition(String name, String details, long amount, int status) {
        this.name = name;
        this.details = details;
        this.amount = amount;
        this.status = status;
    }

    /**
     * Phương thức abstract: Ép các lớp con phải có định danh riêng.
     * Rất hữu ích khi bạn làm chức năng SEARCH hoặc FILTER trên Web/Database.
     */
    public abstract String getIdentifier();

    // Getter & Setter
    public String getName() { return name; }
    public String getDetails() { return details; }
    public long getAmount() { return amount; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
}