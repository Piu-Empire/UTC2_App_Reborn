package com.utc2.appreborn.network.dto;

import com.google.gson.annotations.SerializedName;

public class NotificationSettingResponse {
    @SerializedName("systemNotifEnabled")
    public boolean systemNotifEnabled;

    @SerializedName("gmailNotifEnabled")
    public boolean gmailNotifEnabled;

    @SerializedName("gmailLinked")
    public boolean gmailLinked;

    @SerializedName("gmailTokenExpiry")
    public String gmailTokenExpiry;

    @SerializedName("fcmRegistered")
    public boolean fcmRegistered;
}
