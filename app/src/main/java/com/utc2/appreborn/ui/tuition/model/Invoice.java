package com.utc2.appreborn.ui.tuition.model;

/**
 * Lớp Invoice - Đại diện cho hóa đơn điện tử sau khi thanh toán.
 * Trong MySQL, bảng 'invoices' sẽ có foreign key trỏ tới bảng 'tuition'.
 */
public class Invoice {
    private String invoiceID; // Mã hóa đơn (Ví dụ: IVC12345) - PRIMARY KEY trong DB
    private String date;      // Ngày thanh toán (Nên để định dạng YYYY-MM-DD để MySQL dễ hiểu)
    private Tuition tuition;  // Đối tượng học phí được thanh toán (Đa hình ở đây!)

    public Invoice(String invoiceID, String date, Tuition tuition) {
        this.invoiceID = invoiceID;
        this.date = date;
        this.tuition = tuition;
    }

    // Getter
    public String getInvoiceID() { return invoiceID; }
    public String getDate() { return date; }
    public Tuition getTuition() { return tuition; }

    /**
     * Hàm tiện ích để lấy số tiền trực tiếp từ đối tượng Tuition bên trong.
     * Giúp code ở phần UI ngắn gọn hơn.
     */
    public long getPaidAmount() {
        return tuition.getAmount();
    }
}