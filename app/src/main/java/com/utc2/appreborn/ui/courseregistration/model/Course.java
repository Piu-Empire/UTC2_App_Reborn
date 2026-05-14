package com.utc2.appreborn.ui.courseregistration.model;

public class Course extends CourseItem {

    private final String courseCode;
    private final int    credits;
    private final String lecturer;
    private final String schedule;
    private final String room;
    private final int    maxStudents;
    private       int    currentStudents;
    private final String semester;
    private final String faculty;
    private final String major;
    private final String khoaHoc;
    private final String startDate;    // Ngày bắt đầu
    private final String endDate;      // Ngày kết thúc
    private final int    totalPeriods; // Số tiết học

    public Course(String id, String name, String courseCode, int credits,
                  String lecturer, String schedule, String room,
                  int maxStudents, int currentStudents,
                  String semester, String faculty, String major, String khoaHoc,
                  String startDate, String endDate, int totalPeriods) {
        super(id, name);
        this.courseCode      = courseCode;
        this.credits         = credits;
        this.lecturer        = lecturer;
        this.schedule        = schedule;
        this.room            = room;
        this.maxStudents     = maxStudents;
        this.currentStudents = currentStudents;
        this.semester        = semester;
        this.faculty         = faculty;
        this.major           = major;
        this.khoaHoc         = khoaHoc;
        this.startDate       = startDate;
        this.endDate         = endDate;
        this.totalPeriods    = totalPeriods;
    }

    public String getCourseCode()      { return courseCode; }
    public int    getCredits()         { return credits; }
    public String getLecturer()        { return lecturer; }
    public String getSchedule()        { return schedule; }
    public String getRoom()            { return room; }
    public int    getMaxStudents()     { return maxStudents; }
    public int    getCurrentStudents() { return currentStudents; }
    public String getSemester()        { return semester; }
    public String getFaculty()         { return faculty; }
    public String getMajor()           { return major; }
    public String getKhoaHoc()         { return khoaHoc; }
    public String getStartDate()       { return startDate; }
    public String getEndDate()         { return endDate; }
    public int    getTotalPeriods()    { return totalPeriods; }

    public boolean isAvailable()  { return currentStudents < maxStudents; }
    public void incrementStudents() { currentStudents++; }

    @Override
    public String getDisplayInfo() {
        return "Mã môn: " + courseCode
                + "\nSố tín chỉ: " + credits
                + "\nGiảng viên: " + lecturer
                + "\nThời gian: " + schedule
                + "\nPhòng: " + room
                + "\nNgày bắt đầu: " + startDate
                + "\nNgày kết thúc: " + endDate
                + "\nSố tiết: " + totalPeriods;
    }
}
