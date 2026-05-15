package com.utc2.appreborn.network.dto;

import com.google.gson.annotations.SerializedName;

/**
 * FIX BUG 3: submittedAt / resolvedAt nhận String ISO "yyyy-MM-dd'T'HH:mm:ss"
 * khớp với @JsonFormat đã thêm vào backend ServiceRequestResponse.
 */
public class ServiceRequestResponse {

    @SerializedName("id")
    public Long   id;

    @SerializedName("serviceType")
    public String serviceType;

    @SerializedName("description")
    public String description;

    @SerializedName("status")
    public String status;

    @SerializedName("resultNote")
    public String resultNote;

    // FIX BUG 3: String thay vì LocalDateTime — Gson parse ISO string không cần adapter
    @SerializedName("submittedAt")
    public String submittedAt;

    @SerializedName("resolvedAt")
    public String resolvedAt;
}