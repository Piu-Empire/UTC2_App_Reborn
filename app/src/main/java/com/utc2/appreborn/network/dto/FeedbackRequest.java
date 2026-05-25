package com.utc2.appreborn.network.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Body gửi lên POST /api/v1/interaction/feedback
 * type: "Lỗi" | "Góp ý"
 */
public class FeedbackRequest {

    @SerializedName("type")
    public String type;

    @SerializedName("content")
    public String content;

    public FeedbackRequest(String type, String content) {
        this.type    = type;
        this.content = content;
    }
}