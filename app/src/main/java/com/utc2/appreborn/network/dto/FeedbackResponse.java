package com.utc2.appreborn.network.dto;

import com.google.gson.annotations.SerializedName;

public class FeedbackResponse {

    @SerializedName("id")
    public Long id;

    @SerializedName("type")
    public String type;

    @SerializedName("content")
    public String content;

    @SerializedName("status")
    public String status;      // "chưa đọc" | "đã đọc" | "đã phản hồi"

    @SerializedName("adminReply")
    public String adminReply;  // phản hồi từ admin (nullable)

    @SerializedName("submittedAt")
    public String submittedAt;
}