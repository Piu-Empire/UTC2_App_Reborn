package com.utc2.appreborn.network.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TuitionSummaryResponse {
    @SerializedName("studentId")
    public String studentId;
    @SerializedName("fullName")
    public String fullName;
    /**
     * FIX: Backend trả BigDecimal → dùng String để tránh mất precision.
     * Dùng helper getTotalDebtAsDouble() khi cần hiển thị số.
     */
    @SerializedName("totalDebt")
    public String totalDebt;
    @SerializedName("semesters")
    public List<TuitionResponse> semesters;

    /** Danh sách phí KTX — null nếu chưa đăng ký KTX */
    @SerializedName("dormitory")
    public List<TuitionResponse> dormitory;

    /** Helper: lấy totalDebt dưới dạng double, trả 0.0 nếu null/lỗi. */
    public double getTotalDebtAsDouble() {
        if (totalDebt == null || totalDebt.isEmpty()) return 0.0;
        try {
            return new java.math.BigDecimal(totalDebt).doubleValue();
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}