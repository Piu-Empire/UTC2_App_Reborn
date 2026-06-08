package com.utc2.appreborn.ui.notification;

/**
 * NotificationItem — Model đại diện cho 1 thông báo email sinh viên.
 * <p>
 * Fields:
 * senderName  — Tên người/phòng ban gửi (hiển thị lên card)
 * subject     — Tiêu đề email
 * preview     — Đoạn preview nội dung (2 dòng đầu)
 * timeLabel   — Chuỗi thời gian hiển thị: "10:30", "Hôm qua", "28/04"
 * isRead      — true = đã đọc, false = chưa đọc (hiển thị dot xanh)
 */
public class NotificationItem {

    private final long notifId;
    private final String source;
    private final String senderName;
    private final String subject;
    private final String preview;
    private final String timeLabel;
    private boolean isRead;

    public NotificationItem(long notifId, String source, String senderName, String subject,
                            String preview, String timeLabel, boolean isRead) {
        this.notifId = notifId;
        this.source = source;
        this.senderName = senderName;
        this.subject = subject;
        this.preview = preview;
        this.timeLabel = timeLabel;
        this.isRead = isRead;
    }

    // ── Getters ──────────────────────────────────────────────────

    public long getNotifId() {
        return notifId;
    }

    public String getSource() {
        return source;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getSubject() {
        return subject;
    }

    public String getPreview() {
        return preview;
    }

    public String getTimeLabel() {
        return timeLabel;
    }

    public boolean isRead() {
        return isRead;
    }

    // ── Setter ───────────────────────────────────────────────────

    public void setRead(boolean read) {
        isRead = read;
    }

    /**
     * Lấy chữ cái đầu của sender để hiển thị trên avatar tròn.
     * Ví dụ: "Phòng Đào Tạo" → "P"
     */
    public String getAvatarLetter() {
        if (senderName == null || senderName.isEmpty()) return "?";
        return String.valueOf(senderName.charAt(0)).toUpperCase();
    }
}