package com.utc2.appreborn.network.dto;

import com.google.gson.annotations.SerializedName;

/**
 * FIX: totalAmount / paidAmount / remainingAmount đổi từ Double sang String.
 *
 * Backend trả BigDecimal (ví dụ "4500000.00"). Gson map BigDecimal → Double
 * có thể gây mất precision với số tiền lớn (floating-point representation).
 * Nhận String an toàn hơn; khi cần tính toán dùng new BigDecimal(value).
 *
 * Cách dùng trong Activity:
 *   import java.math.BigDecimal;
 *   double remaining = t.remainingAmount != null
 *       ? new BigDecimal(t.remainingAmount).doubleValue() : 0.0;
 */
public class TuitionResponse {
    @SerializedName("id")              public Long    id;
    @SerializedName("studentId")       public String  studentId;
    @SerializedName("fullName")        public String  fullName;
    @SerializedName("semesterId")      public Long    semesterId;
    @SerializedName("semesterName")    public String  semesterName;
    /** FK → dormitory_registration — chỉ có giá trị khi feeType = DORMITORY */
    @SerializedName("dormRegId")       public Long    dormRegId;
    /** ADD: tổng tín chỉ kỳ này — để hiển thị "X TC" trên màn hình thanh toán */
    @SerializedName("totalCredits")    public Integer totalCredits;
    @SerializedName("totalAmount")     public String  totalAmount;     // BigDecimal → String
    @SerializedName("paidAmount")      public String  paidAmount;      // BigDecimal → String
    @SerializedName("remainingAmount") public String  remainingAmount; // BigDecimal → String
    @SerializedName("dueDate")         public String  dueDate;
    @SerializedName("paidAt")          public String  paidAt;
    @SerializedName("status")          public String  status;
    @SerializedName("paymentMethod")   public String  paymentMethod;

    /** Helper: lấy remainingAmount dưới dạng double, trả 0.0 nếu null/lỗi. */
    public double getRemainingAmountAsDouble() {
        if (remainingAmount == null || remainingAmount.isEmpty()) return 0.0;
        try {
            return new java.math.BigDecimal(remainingAmount).doubleValue();
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /** Helper: lấy totalAmount dưới dạng double, trả 0.0 nếu null/lỗi. */
    public double getTotalAmountAsDouble() {
        if (totalAmount == null || totalAmount.isEmpty()) return 0.0;
        try {
            return new java.math.BigDecimal(totalAmount).doubleValue();
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /** Helper: lấy paidAmount dưới dạng double, trả 0.0 nếu null/lỗi. */
    public double getPaidAmountAsDouble() {
        if (paidAmount == null || paidAmount.isEmpty()) return 0.0;
        try {
            return new java.math.BigDecimal(paidAmount).doubleValue();
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}