package com.utc2.appreborn.ui.public_services.model;

public class StudentConfirmationService extends BaseService {
    private String studentName;
    private String className;

    public StudentConfirmationService(String title, String description, long timestamp, int status, String serviceType,
                                      String studentName, String studentId, String className) {
        super(title, description, timestamp, status, serviceType);
        this.studentId = studentId;
        this.studentName = studentName;
        this.className = className;
    }

    public String getStudentName() { return studentName; }
    public String getClassName() { return className; }
}