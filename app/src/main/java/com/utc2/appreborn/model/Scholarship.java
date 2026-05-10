package com.utc2.appreborn.model;

/**
 * Scholarship - Model cho một học bổng.
 *
 * Fields:
 *  name          - Tên học bổng (VD: "Học bổng Khuyến khích học tập")
 *  organization  - Tổ chức/trường cấp học bổng
 *  valueAmount   - Số tiền (VD: 3000000)
 *  valuePeriod   - Chu kỳ nhận (VD: "HK" = học kỳ, "năm" = năm)
 *  status        - Trạng thái: "Chưa nhận" hoặc "Đã nhận"
 *  minGpa        - GPA tối thiểu để đủ điều kiện
 */
public class Scholarship {

    public static final String STATUS_NOT_RECEIVED = "Chưa nhận";
    public static final String STATUS_RECEIVED = "Đã nhận";

    private String name;
    private String organization;
    private long valueAmount;
    private String valuePeriod;
    private String status;
    private double minGpa;

    public Scholarship(String name, String organization, long valueAmount,
                       String valuePeriod, String status, double minGpa) {
        this.name = name;
        this.organization = organization;
        this.valueAmount = valueAmount;
        this.valuePeriod = valuePeriod;
        this.status = status;
        this.minGpa = minGpa;
    }

    public String getName() { return name; }
    public String getOrganization() { return organization; }
    public long getValueAmount() { return valueAmount; }
    public String getValuePeriod() { return valuePeriod; }
    public String getStatus() { return status; }
    public double getMinGpa() { return minGpa; }

    public boolean isReceived() {
        return STATUS_RECEIVED.equals(status);
    }
}
