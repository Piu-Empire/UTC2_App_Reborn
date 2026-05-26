package com.utc2.appreborn.network.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Mapping với backend DormRegistrationDto.
 * Endpoint: GET /api/v1/dormitory/my
 *
 * paid_status từ DB: "đã đóng" / "chưa đóng"
 * status (đăng ký):  "đã duyệt" / "chờ duyệt" / "đã hủy"
 */
public class DormRegistrationResponse {

    @SerializedName("dormRegId")
    public Long   dormRegId;

    @SerializedName("roomId")
    public Long   roomId;

    @SerializedName("roomCode")
    public String roomCode;

    @SerializedName("building")
    public String building;

    @SerializedName("roomType")
    public String roomType;

    @SerializedName("pricePerMonth")
    public Double pricePerMonth;

    @SerializedName("startDate")
    public String startDate;

    @SerializedName("endDate")
    public String endDate;

    /** Trạng thái đăng ký: "đã duyệt" / "chờ duyệt" / "đã hủy" */
    @SerializedName("status")
    public String status;

    /** Tổng phí KTX (pricePerMonth × số tháng) */
    @SerializedName("totalFee")
    public Double totalFee;

    /** Trạng thái đóng tiền: "đã đóng" / "chưa đóng" */
    @SerializedName("paidStatus")
    public String paidStatus;

    @SerializedName("registeredAt")
    public String registeredAt;

    /** Helper: lấy totalFee an toàn, trả 0.0 nếu null */
    public double getTotalFeeAsDouble() {
        return totalFee != null ? totalFee : 0.0;
    }

    /** Helper: true nếu đã đóng tiền KTX */
    public boolean isPaid() {
        return "đã đóng".equals(paidStatus);
    }
}