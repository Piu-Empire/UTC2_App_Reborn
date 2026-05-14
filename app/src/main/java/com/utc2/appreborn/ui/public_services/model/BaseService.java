package com.utc2.appreborn.ui.public_services.model;

import android.text.format.DateFormat;

import java.io.Serializable;

/**
 * BaseService (abstract)
 * ──────────────────────────────────────────────────────────────
 * Lớp cha cho tất cả các loại dịch vụ công.
 *
 * Mapping với TABLE SERVICE_REQUEST:
 *   request_id      BIGINT PK AUTO_INCREMENT
 *   user_id         BIGINT FK → USER
 *   service_type    VARCHAR(100)
 *   description     TEXT
 *   status          VARCHAR(30)
 *   submitted_at    TIMESTAMP
 *   resolved_at     TIMESTAMP
 *   handled_by      BIGINT FK → USER
 *   result_note     TEXT
 *   attachment_url  VARCHAR(500)
 *   result_file_url VARCHAR(500)
 *
 * FIX:
 *  - Dùng STATUS tiếng Anh khớp DB schema
 *  - Giữ STATUS tiếng Việt @Deprecated để tương thích code cũ
 *  - Thêm handledBy + resultFileUrl
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

    /**
     * @deprecated Dùng STATUS_* tiếng Anh thay thế.
     */
    @Deprecated public static final String STATUS_PENDING_VI    = "chờ xử lý";
    @Deprecated public static final String STATUS_PROCESSING_VI = "đang xử lý";
    @Deprecated public static final String STATUS_DONE_VI       = "hoàn thành";
    @Deprecated public static final String STATUS_REJECTED_VI   = "từ chối";

    // ── Fields mapping DB ────────────────────────────────────

    protected long   requestId;      // SERVICE_REQUEST.request_id
    protected long   userId;         // SERVICE_REQUEST.user_id
    protected String serviceType;    // SERVICE_REQUEST.service_type
    protected String description;    // SERVICE_REQUEST.description
    protected String status;         // SERVICE_REQUEST.status
    protected long   submittedAt;    // SERVICE_REQUEST.submitted_at
    protected String resolvedAt;     // SERVICE_REQUEST.resolved_at

    // ── Fields mở rộng ───────────────────────────────────────

    protected long   handledBy;      // SERVICE_REQUEST.handled_by
    protected String resultNote;     // SERVICE_REQUEST.result_note
    protected String attachmentUrl;  // SERVICE_REQUEST.attachment_url
    protected String resultFileUrl;  // SERVICE_REQUEST.result_file_url

    // ── UI Field (không có trong DB) ─────────────────────────

    protected String title;

    /** Constructor mặc định — bắt buộc cho Serializable */
    public BaseService() {}

    /**
     * Constructor dùng khi tạo request mới.
     */
    public BaseService(
            String title,
            String description,
            long submittedAt,
            String status,
            String serviceType
    ) {
        this.title       = title;
        this.description = description;
        this.submittedAt = submittedAt;
        this.status      = status;
        this.serviceType = serviceType;
    }

    // ── Getters ──────────────────────────────────────────────

    public long getRequestId() {
        return requestId;
    }

    public long getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getServiceType() {
        return serviceType;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public long getTimestamp() {
        return submittedAt;
    }

    public long getSubmittedAt() {
        return submittedAt;
    }

    public String getResolvedAt() {
        return resolvedAt;
    }

    public long getHandledBy() {
        return handledBy;
    }

    public String getResultNote() {
        return resultNote;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public String getResultFileUrl() {
        return resultFileUrl;
    }

    /**
     * Format ngày hiển thị.
     */
    public String getDate() {
        return DateFormat
                .format("dd/MM/yyyy", submittedAt)
                .toString();
    }

    // ── Setters ──────────────────────────────────────────────

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSubmittedAt(long submittedAt) {
        this.submittedAt = submittedAt;
    }

    public void setResolvedAt(String resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public void setHandledBy(long handledBy) {
        this.handledBy = handledBy;
    }

    public void setResultNote(String resultNote) {
        this.resultNote = resultNote;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public void setResultFileUrl(String resultFileUrl) {
        this.resultFileUrl = resultFileUrl;
    }

    // ── Tiện ích ─────────────────────────────────────────────

    public boolean isPending() {
        return STATUS_PENDING.equals(status);
    }

    public boolean isProcessing() {
        return STATUS_PROCESSING.equals(status);
    }

    public boolean isCompleted() {
        return STATUS_COMPLETED.equals(status);
    }

    public boolean isRejected() {
        return STATUS_REJECTED.equals(status);
    }
}