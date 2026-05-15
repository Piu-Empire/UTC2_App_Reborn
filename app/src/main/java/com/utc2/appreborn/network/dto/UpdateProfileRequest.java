package com.utc2.appreborn.network.dto;

import com.google.gson.annotations.SerializedName;

/**
 * FIX BUG 1 + BUG 2:
 * - dateOfBirth gửi dạng String "yyyy-MM-dd" — backend dùng @JsonFormat để parse.
 * - Tên JSON key là "phone" (khớp backend UpdateProfileRequest.phone),
 *   KHÔNG phải "phoneNumber" (đó là field trong ProfileResponse khi đọc về).
 * - gender gửi dạng String "MALE" / "FEMALE" — khớp với enum Gender của backend.
 */
public class UpdateProfileRequest {

    @SerializedName("fullName")
    public String fullName;

    // FIX BUG 2: key JSON là "phone" khớp với backend UpdateProfileRequest.phone
    @SerializedName("phone")
    public String phone;

    @SerializedName("address")
    public String address;

    // FIX BUG 1: gửi dạng "yyyy-MM-dd" String, backend parse được nhờ @JsonFormat
    @SerializedName("dateOfBirth")
    public String dateOfBirth;  // format: "yyyy-MM-dd"

    // FIX: gửi "MALE" hoặc "FEMALE" khớp enum Gender backend
    @SerializedName("gender")
    public String gender;

    @SerializedName("avatarUrl")
    public String avatarUrl;

    public UpdateProfileRequest() {}

    public UpdateProfileRequest(String fullName, String phone, String address,
                                String dateOfBirth, String gender, String avatarUrl) {
        this.fullName    = fullName;
        this.phone       = phone;
        this.address     = address;
        this.dateOfBirth = dateOfBirth;
        this.gender      = gender;
        this.avatarUrl   = avatarUrl;
    }
}