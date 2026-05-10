package com.utc2.appreborn.model;

import com.utc2.appreborn.utils.DateUtils;

import java.util.Calendar;

public class ScheduleItem {
    private String subjectCode;
    private String subjectName;
    private String type;
    private String lecturer;
    private int dayOfWeek;
    private int startPeriod;
    private int endPeriod;
    private int totalPeriodsStudied;
    private String startTime;
    private String endTime;
    private String startDate;
    private String endDate;
    private String room;
    private String building;
    private String location;
    private int totalWeeks;
    private Calendar displayDate;
    private String className = "CQ.65.CNTT";
    private String semester = "Học kỳ 2 (2025 - 2026)";
    private int studentCount = 89;
    private int remainingPeriods = 12;

    // khởi tạo thông tin môn học và tính tổng tuần học
    public ScheduleItem(String subjectCode, String subjectName, String type, String lecturer,
                        int dayOfWeek, int startPeriod, int endPeriod, int totalPeriodsStudied,
                        String startTime, String endTime, String startDate, String endDate,
                        String room, String building) {
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.type = type;
        this.lecturer = lecturer;
        this.dayOfWeek = dayOfWeek;
        this.startPeriod = startPeriod;
        this.endPeriod = endPeriod;
        this.totalPeriodsStudied = totalPeriodsStudied;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startDate = startDate;
        this.endDate = endDate;
        this.room = room;
        this.building = building;
        this.location = (building != null ? building : "") + " - " + (room != null ? room : "");
        this.totalWeeks = computeTotalWeeks();
    }

    // tạo đối tượng rỗng để thiết lập dữ liệu sau này
    public ScheduleItem() {
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLecturer() {
        return lecturer;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getStartPeriod() {
        return startPeriod;
    }

    public void setStartPeriod(int startPeriod) {
        this.startPeriod = startPeriod;
    }

    public int getEndPeriod() {
        return endPeriod;
    }

    public void setEndPeriod(int endPeriod) {
        this.endPeriod = endPeriod;
    }

    public int getTotalPeriodsStudied() {
        return totalPeriodsStudied;
    }

    public void setTotalPeriodsStudied(int totalPeriodsStudied) {
        this.totalPeriodsStudied = totalPeriodsStudied;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
        updateLocation();
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
        updateLocation();
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Calendar getDisplayDate() {
        return displayDate;
    }

    public void setDisplayDate(Calendar displayDate) {
        this.displayDate = displayDate;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public int getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(int studentCount) {
        this.studentCount = studentCount;
    }

    public void setRemainingPeriods(int remainingPeriods) {
        this.remainingPeriods = remainingPeriods;
    }

    // tự động cập nhật chuỗi văn bản vị trí lớp học
    private void updateLocation() {
        this.location = (building != null ? building : "") + " - " + (room != null ? room : "");
    }

    // tính toán số tiết học còn lại của môn học này
    public int getRemainingPeriods(Calendar atDate) {
        Calendar start = DateUtils.parseDate(this.startDate);
        Calendar end = DateUtils.parseDate(this.endDate);

        if (start == null || end == null) return 0;

        Calendar target = DateUtils.getStartOfDay(atDate);

        if (target.before(start)) {
            return computeTotalPeriods();
        }

        if (target.after(end)) {
            return 0;
        }

        int calDayOfWeek = (dayOfWeek == 6) ? Calendar.SUNDAY : (dayOfWeek + 2);

        Calendar firstClass = (Calendar) start.clone();
        int startDow = firstClass.get(Calendar.DAY_OF_WEEK);
        int daysToShift = (calDayOfWeek - startDow + 7) % 7;
        firstClass.add(Calendar.DAY_OF_MONTH, daysToShift);

        if (firstClass.after(end)) return 0;

        int periodsPerSession = endPeriod - startPeriod + 1;
        int total = computeTotalPeriods();

        if (target.before(firstClass)) {
            return total;
        }

        long diffMs = target.getTimeInMillis() - firstClass.getTimeInMillis();
        int daysDiff = (int) (diffMs / (1000L * 60 * 60 * 24));

        int sessionsStudied = daysDiff / 7;

        if (target.get(Calendar.DAY_OF_WEEK) == calDayOfWeek
                && !target.equals(firstClass)) {
        }

        long totalDiffMs = end.getTimeInMillis() - firstClass.getTimeInMillis();
        int totalDays = (int) (totalDiffMs / (1000L * 60 * 60 * 24));
        int totalSessions = (totalDays / 7) + 1;

        sessionsStudied = Math.min(sessionsStudied, totalSessions);

        int studied = sessionsStudied * periodsPerSession;

        return Math.max(0, total - studied);
    }

    // xác định môn học có diễn ra vào ngày đó không
    public boolean isHappeningOn(Calendar date) {
        Calendar start = DateUtils.parseDate(this.startDate);
        Calendar end = DateUtils.parseDate(this.endDate);
        if (start == null || end == null) return true;
        Calendar target = DateUtils.getStartOfDay(date);
        if (target.before(start) || target.after(end)) return false;
        int calDay = target.get(Calendar.DAY_OF_WEEK);
        int mappedDay = (calDay == Calendar.SUNDAY) ? 6 : calDay - 2;
        return mappedDay == this.dayOfWeek;
    }

    // kiểm tra môn học có hiển thị trong tuần học này
    public boolean isVisibleInWeek(Calendar weekStart) {
        Calendar itemStart = DateUtils.parseDate(this.startDate);
        Calendar itemEnd = DateUtils.parseDate(this.endDate);
        if (itemStart == null || itemEnd == null) return true;
        Calendar exactClassDate = (Calendar) weekStart.clone();
        exactClassDate.add(Calendar.DAY_OF_MONTH, this.dayOfWeek);
        exactClassDate = DateUtils.getStartOfDay(exactClassDate);
        if (exactClassDate.before(itemStart) || exactClassDate.after(itemEnd)) return false;
        return true;
    }

    // tính tổng số tiết học dựa trên số tuần thực tế
    public int computeTotalPeriods() {
        if (endPeriod < startPeriod) return -1;

        int totalWeeks = computeTotalWeeks();
        int periodsPerSession = endPeriod - startPeriod + 1;

        return totalWeeks * periodsPerSession;
    }

    // tính toán tổng số tuần diễn ra môn học từ ngày
    public int computeTotalWeeks() {
        Calendar start = DateUtils.parseDate(this.startDate);
        Calendar end = DateUtils.parseDate(this.endDate);

        if (start == null || end == null) return 0;

        long diffMs = end.getTimeInMillis() - start.getTimeInMillis();
        long diffDays = diffMs / (1000L * 60 * 60 * 24);

        return (int) Math.ceil((diffDays + 1) / 7.0);
    }
}