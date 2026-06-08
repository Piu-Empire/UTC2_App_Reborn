package com.utc2.appreborn.network.dto;

import com.google.gson.annotations.SerializedName;

public class NotificationSettingRequest {
    @SerializedName("systemNotifEnabled")
    public Boolean systemNotifEnabled;

    @SerializedName("gmailNotifEnabled")
    public Boolean gmailNotifEnabled;
    
    public NotificationSettingRequest(Boolean systemNotifEnabled, Boolean gmailNotifEnabled) {
        this.systemNotifEnabled = systemNotifEnabled;
        this.gmailNotifEnabled = gmailNotifEnabled;
    }
}
