package com.utc2.appreborn.ui.public_services.model;

public class TranscriptService extends BaseService {
    private String studentName;
    private String className; // Đã đổi từ studentClass sang className theo yêu cầu của bạn
    private String academicYear;
    private String semester;
    private String quantity;

    public TranscriptService(String title, String description, long timestamp, int status, String serviceType,
                             String studentName, String studentId, String className,
                             String academicYear, String semester, String quantity) {
        super(title, description, timestamp, status, serviceType);
        this.studentId = studentId;
        this.studentName = studentName;
        this.className = className;
        this.academicYear = academicYear;
        this.semester = semester;
        this.quantity = quantity;
    }

    public String getStudentName() { return studentName; }
    public String getClassName() { return className; }
    public String getAcademicYear() { return academicYear; }
    public String getSemester() { return semester; }
    public String getQuantity() { return quantity; }
}