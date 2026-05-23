package com.utc2.appreborn.ui.profile.model;

/**
 * FeedbackItem — UI model cho 1 phản hồi trong danh sách lịch sử.
 */
public class FeedbackItem {

    private final long   id;
    private final String type;        // "Lỗi" | "Góp ý"
    private final String content;
    private final String status;      // "chưa đọc" | "đã đọc" | "đã phản hồi"
    private final String adminReply;  // nullable
    private final String timeLabel;   // "22/05/2026 10:30"

    public FeedbackItem(long id, String type, String content,
                        String status, String adminReply, String timeLabel) {
        this.id         = id;
        this.type       = type;
        this.content    = content;
        this.status     = status;
        this.adminReply = adminReply;
        this.timeLabel  = timeLabel;
    }

    public long   getId()        { return id; }
    public String getType()      { return type; }
    public String getContent()   { return content; }
    public String getStatus()    { return status; }
    public String getAdminReply(){ return adminReply; }
    public String getTimeLabel() { return timeLabel; }

    public String getAvatarLetter() {
        if (type == null || type.isEmpty()) return "?";
        return String.valueOf(type.charAt(0)).toUpperCase();
    }

    public boolean hasAdminReply() {
        return adminReply != null && !adminReply.isEmpty();
    }
}