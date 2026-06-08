package com.utc2.appreborn.network.dto;

import com.google.gson.annotations.SerializedName;

public class GmailMessageResponse {
    @SerializedName("messageId")
    public String messageId;

    @SerializedName("subject")
    public String subject;

    @SerializedName("from")
    public String from;

    @SerializedName("snippet")
    public String snippet;

    @SerializedName("receivedAt")
    public String receivedAt;

    @SerializedName("isUnread")
    public boolean isUnread;
}
