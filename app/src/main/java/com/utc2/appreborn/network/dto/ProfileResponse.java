package com.utc2.appreborn.network.dto;

import com.google.gson.annotations.SerializedName;

/**
 * FIX BUG 1: dateOfBirth nhận String "yyyy-MM-dd" thay vì LocalDate.
 * Backend đã thêm @JsonFormat nên luôn serialize dạng ISO String.
 *
 * FIX BUG 2: phoneNumber (read) khác phone (write in UpdateProfileRequest).
 * File này chỉ dùng để ĐỌC — field phoneNumber ở đây là đúng.
 * Khi ghi dùng UpdateProfileRequest với field "phone".
 */
public class ProfileResponse {
    @SerializedName("id")             public Long   id;
    @SerializedName("studentId")      public String studentId;
    @SerializedName("username")       public String username;
    @SerializedName("email")          public String email;
    @SerializedName("fullName")       public String fullName;
    @SerializedName("phoneNumber")    public String phoneNumber;   // READ field
    @SerializedName("address")        public String address;

    // FIX BUG 1: String thay vì giữ nguyên — nhận "2003-08-15" từ backend
    @SerializedName("dateOfBirth")    public String dateOfBirth;  // "yyyy-MM-dd"

    @SerializedName("gender")         public String gender;
    @SerializedName("faculty")        public String faculty;
    @SerializedName("major")          public String major;
    @SerializedName("academicYear")   public String academicYear;
    @SerializedName("className")      public String className;
    @SerializedName("status")         public String status;
    @SerializedName("avatarUrl")      public String avatarUrl;
    @SerializedName("studentCardUrl") public String studentCardUrl;
    @SerializedName("advisorName")    public String advisorName;
    @SerializedName("role")           public String role;
}