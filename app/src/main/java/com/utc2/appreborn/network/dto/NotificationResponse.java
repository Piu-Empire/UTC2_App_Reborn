package com.utc2.appreborn.network.dto;

import com.google.gson.annotations.SerializedName;

public class NotificationResponse {
    @SerializedName("notificationId")
    public long notificationId;

    @SerializedName("title")
    public String title;

    @SerializedName("body")
    public String body;

    @SerializedName("type")
    public String type;

    @SerializedName("source")
    public String source;

    @SerializedName("isRead")
    public boolean isRead;

    @SerializedName("sentAt")
    public String sentAt;

    @SerializedName("relatedEntityType")
    public String relatedEntityType;

    @SerializedName("relatedEntityId")
    public Long relatedEntityId;
}
