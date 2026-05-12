package com.utc2.appreborn.ui.public_services.model;

import android.text.format.DateFormat;
import java.io.Serializable;

/**
 * BaseService (abstract)
 * ──────────────────────────────────────────────────────────────
 * Lớp cha cho tất cả các loại dịch vụ công.
 *
 * Mapping TABLE SERVICE_REQUEST:
 *   request_id     BIGINT PK AUTO_INCREMENT
 *   user_id        BIGINT FK → USER
 *   service_type   VARCHAR(100)
 *   description    TEXT
 *   status         VARCHAR(50)   DEFAULT 'chờ xử lý'
 *   submitted_at   TIMESTAMP
 *   resolved_at    TIMESTAMP
 *   result_note    TEXT
 *   attachment_url TEXT
 */
public abstract class BaseService implements Serializable {

    // ── Hằng service_type ────────────────────────────────────
    public static final String TYPE_TRANSCRIPT   = "TRANSCRIPT";
    public static final String TYPE_CONFIRMATION = "CONFIRMATION_LETTER";
    public static final String TYPE_CARD_REISSUE = "CARD_REISSUE";
    public static final String TYPE_LOAN_SUPPORT = "LOAN_SUPPORT";
    public static final String TYPE_FACILITY     = "FACILITY";
    public static final String TYPE_OTHER        = "OTHER";

    // ── Hằng status ──────────────────────────────────────────
    public static final String STATUS_PENDING    = "PENDING";
    public static final String STATUS_PROCESSING = "PROCESSING";
    public static final String STATUS_COMPLETED  = "COMPLETED";
    public static final String STATUS_REJECTED   = "REJECTED";

    // ── Fields ────────────────────────────────────────────────
    protected long   requestId;      // request_id
    protected long   userId;         // user_id
    protected String serviceType;    // service_type
    protected String description;    // description
    protected String status;         // status
    protected long   submittedAt;    // submitted_at (epoch millis)
    protected String resolvedAt;     // resolved_at  (null nếu chưa xong)
    protected String resultNote;     // result_note
    protected String attachmentUrl;  // attachment_url

    // ── Trường UI bổ sung (không có trong DB) ────────────────
    protected String title;

    public BaseService() {}

    public BaseService(String title, String description,
                       long submittedAt, String status, String serviceType) {
        this.title       = title;
        this.description = description;
        this.submittedAt = submittedAt;
        this.status      = status;
        this.serviceType = serviceType;
    }

    // ── Getters ───────────────────────────────────────────────
    public long   getRequestId()     { return requestId; }
    public long   getUserId()        { return userId; }
    public String getTitle()         { return title; }
    public String getServiceType()   { return serviceType; }
    public String getDescription()   { return description; }
    public String getStatus()        { return status; }
    public long   getSubmittedAt()   { return submittedAt; }
    public String getResolvedAt()    { return resolvedAt; }
    public String getResultNote()    { return resultNote; }
    public String getAttachmentUrl() { return attachmentUrl; }

    /** Định dạng ngày hiển thị từ submittedAt. */
    public String getDate() {
        return DateFormat.format("dd/MM/yyyy", submittedAt).toString();
    }

    // ── Setters ───────────────────────────────────────────────
    public void setRequestId(long v)      { this.requestId = v; }
    public void setUserId(long v)         { this.userId = v; }
    public void setTitle(String v)        { this.title = v; }
    public void setServiceType(String v)  { this.serviceType = v; }
    public void setDescription(String v)  { this.description = v; }
    public void setStatus(String v)       { this.status = v; }
    public void setSubmittedAt(long v)    { this.submittedAt = v; }
    public void setResolvedAt(String v)   { this.resolvedAt = v; }
    public void setResultNote(String v)   { this.resultNote = v; }
    public void setAttachmentUrl(String v){ this.attachmentUrl = v; }

    // ── Tiện ích ─────────────────────────────────────────────
    public boolean isPending()    { return STATUS_PENDING.equals(status); }
    public boolean isProcessing() { return STATUS_PROCESSING.equals(status); }
    public boolean isCompleted()  { return STATUS_COMPLETED.equals(status); }
    public boolean isRejected()   { return STATUS_REJECTED.equals(status); }
}