package com.utc2.appreborn.ui.public_services.model;

import android.text.format.DateFormat;

import java.io.Serializable;

public abstract class BaseService implements Serializable {
    protected String requestId;
    protected String studentId;
    protected long timestamp;
    protected int status;
    protected String serviceType;
    protected String title;
    protected String description;

    public BaseService() {
    }

    public BaseService(String title, String description, long timestamp, int status, String serviceType) {
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
        this.status = status;
        this.serviceType = serviceType;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public long getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getServiceType() { return serviceType; }

    // Hàm fix lỗi "Cannot resolve method 'getDate'"
    public String getDate() {
        return DateFormat.format("dd/MM/yyyy", timestamp).toString();
    }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
}