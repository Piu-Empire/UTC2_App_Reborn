package com.utc2.appreborn.ui.public_services.model;

public class LoanSupportService extends BaseService {
    private String loanAmount;
    private String loanReason;
    private String phoneNumber;

    public LoanSupportService(String title, String description, long timestamp, int status, String serviceType,
                              String loanAmount, String loanReason, String phoneNumber) {
        // Gọi constructor của lớp cha BaseService
        super(title, description, timestamp, status, serviceType);
        this.loanAmount = loanAmount;
        this.loanReason = loanReason;
        this.phoneNumber = phoneNumber;
    }

    // Các hàm Getter dùng để hiển thị hoặc lưu trữ sau này
    public String getLoanAmount() { return loanAmount; }
    public String getLoanReason() { return loanReason; }
    public String getPhoneNumber() { return phoneNumber; }
}