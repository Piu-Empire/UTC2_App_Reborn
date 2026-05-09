package com.utc2.appreborn.ui.courseregistration.model;

import com.utc2.appreborn.ui.courseregistration.exception.CourseException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CourseRepository {

    private static final int MAX_CREDITS = 24;

    private final List<Course>                    courseList    = new ArrayList<>();
    private final Map<String, CourseRegistration> registrations = new LinkedHashMap<>();

    private static CourseRepository instance;
    private CourseRepository() { loadSampleData(); }
    public static CourseRepository getInstance() {
        if (instance == null) instance = new CourseRepository();
        return instance;
    }

    private void loadSampleData() {
        // id, name, code, credits, lecturer, schedule, room,
        // maxStudents, currentStudents, semester, faculty, major, khoaHoc,
        // startDate, endDate, totalPeriods

        courseList.add(new Course("c1", "Lập trình hướng đối tượng",
                "IT0588485", 3, "Trần Thị Dung", "T2, T4 (7:00-9:30)", "A201",
                60, 45, "HK1", "CNTT", "CNTT", "K65",
                "10/02/2025", "25/05/2025", 45));

        courseList.add(new Course("c2", "Cấu trúc dữ liệu và giải thuật",
                "CS301", 3, "TS. Nguyễn Văn An", "T2, T4 (7:00-9:30)", "A201",
                60, 45, "HK1", "CNTT", "CNTT", "K65",
                "10/02/2025", "25/05/2025", 45));

        courseList.add(new Course("c3", "Cơ sở dữ liệu",
                "IT0411", 3, "PGS. Lê Hoài Nam", "T3, T5 (9:30-11:30)", "B305",
                60, 20, "HK1", "CNTT", "CNTT", "K65",
                "12/02/2025", "27/05/2025", 45));

        courseList.add(new Course("c4", "Giải tích 2",
                "MA102", 3, "TS. Trần Thị Hoa", "T2, T6 (11:00-13:00)", "D201",
                60, 60, "HK2", "CNTT", "CNTT", "K65",
                "01/09/2025", "20/12/2025", 45));

        courseList.add(new Course("c5", "Công nghệ Java",
                "IT0512", 3, "Trần Thị Dung", "T3, T5 (9:30-11:30)", "B305",
                60, 40, "HK2", "CNTT", "CNTT", "K65",
                "01/09/2025", "20/12/2025", 60));

        courseList.add(new Course("c6", "Lập trình Web",
                "IT0523", 3, "ThS. Phan Minh Tuấn", "T4, T6 (13:00-15:00)", "C102",
                50, 48, "HK2", "CNTT", "CNTT", "K65",
                "03/09/2025", "22/12/2025", 60));

        courseList.add(new Course("c7", "Kinh tế chính trị Mác",
                "ML201", 2, "TS. Lê Văn Bình", "T4 (13:00-15:00)", "C102",
                80, 60, "HK3", "CNTT", "CNTT", "K65",
                "05/01/2026", "30/04/2026", 30));

        courseList.add(new Course("c8", "Chủ nghĩa khoa học xã hội",
                "SS101", 3, "PGS. Phạm Thu Hà", "T2, T6 (11:00-13:00)", "D201",
                70, 35, "HK3", "CNTT", "CNTT", "K65",
                "05/01/2026", "30/04/2026", 45));

        courseList.add(new Course("c9", "Lập trình C/C++",
                "IT0101", 3, "ThS. Nguyễn Hoàng Long", "T2, T5 (7:00-9:30)", "A101",
                60, 30, "HK1", "CNTT", "CNTT", "K66",
                "10/02/2025", "25/05/2025", 45));

        courseList.add(new Course("c10", "Toán rời rạc",
                "MA201", 3, "TS. Võ Thị Lan", "T3, T6 (9:30-11:30)", "B201",
                60, 55, "HK1", "CNTT", "CNTT", "K66",
                "10/02/2025", "25/05/2025", 45));

        courseList.add(new Course("c11", "Kiểm thử phần mềm",
                "SE301", 3, "TS. Hồ Thanh Phong", "T2, T4 (13:00-15:00)", "C203",
                45, 20, "HK2", "CNTT", "KTPM", "K66",
                "01/09/2025", "20/12/2025", 45));

        courseList.add(new Course("c12", "Phân tích thiết kế hệ thống",
                "SE201", 3, "PGS. Trần Quang Minh", "T3, T5 (7:00-9:30)", "A301",
                50, 50, "HK2", "CNTT", "KTPM", "K66",
                "01/09/2025", "20/12/2025", 45));

        courseList.add(new Course("c13", "Hệ thống thông tin quản lý",
                "IS101", 3, "TS. Lê Thị Thanh", "T4, T6 (9:30-11:30)", "D102",
                55, 15, "HK1", "CNTT", "HTTT", "K66",
                "12/02/2025", "27/05/2025", 45));

        courseList.add(new Course("c14", "Nhập môn lập trình",
                "IT0001", 3, "ThS. Phạm Văn Đức", "T2, T4 (9:30-11:30)", "A102",
                70, 25, "HK1", "CNTT", "CNTT", "K67",
                "10/02/2025", "25/05/2025", 45));

        courseList.add(new Course("c15", "Giải tích 1",
                "MA101", 3, "TS. Nguyễn Minh Khoa", "T3, T5 (11:00-13:00)", "C301",
                65, 40, "HK1", "CNTT", "CNTT", "K67",
                "10/02/2025", "25/05/2025", 45));

        courseList.add(new Course("c16", "Vẽ kỹ thuật",
                "ME101", 2, "ThS. Trần Xuân Hiệp", "T2 (7:00-11:00)", "E101",
                40, 38, "HK1", "CK", "CK", "K65",
                "10/02/2025", "25/05/2025", 30));

        courseList.add(new Course("c17", "Cơ học lý thuyết",
                "ME201", 3, "TS. Phạm Quốc Hùng", "T4, T6 (7:00-9:30)", "F201",
                45, 30, "HK1", "CK", "CK", "K65",
                "10/02/2025", "25/05/2025", 45));

        courseList.add(new Course("c18", "Sức bền vật liệu",
                "CE301", 3, "PGS. Ngô Thị Bích", "T2, T5 (9:30-11:30)", "G102",
                40, 22, "HK2", "XD", "XD", "K66",
                "01/09/2025", "20/12/2025", 45));

        courseList.add(new Course("c19", "Kế toán tài chính",
                "AC201", 3, "TS. Lý Thị Kim Anh", "T3, T6 (13:00-15:00)", "H201",
                60, 45, "HK2", "KT", "KT", "K66",
                "01/09/2025", "20/12/2025", 45));

        courseList.add(new Course("c20", "Mạng máy tính",
                "NT301", 3, "TS. Đỗ Văn Thắng", "T2, T4 (9:30-11:30)", "B102",
                55, 10, "HK2", "CNTT", "MMT", "K65",
                "01/09/2025", "20/12/2025", 45));
    }

    public List<Course> getAllCourses() { return new ArrayList<>(courseList); }

    public List<Course> filterCourses(String semester, String khoaHoc, String major) {
        List<Course> result = new ArrayList<>();
        for (Course c : courseList) {
            boolean okSem  = semester == null || semester.isEmpty() || c.getSemester().equalsIgnoreCase(semester);
            boolean okKhoa = khoaHoc  == null || khoaHoc.isEmpty()  || c.getKhoaHoc().equalsIgnoreCase(khoaHoc);
            boolean okMaj  = major    == null || major.isEmpty()    || c.getMajor().equalsIgnoreCase(major);
            if (okSem && okKhoa && okMaj) result.add(c);
        }
        return result;
    }

    public Map<String, CourseRegistration> getRegistrations() { return registrations; }

    public int getTotalRegisteredCredits() {
        int total = 0;
        for (CourseRegistration reg : registrations.values())
            if (!"CANCELLED".equals(reg.getStatus())) total += reg.getCourse().getCredits();
        return total;
    }

    public CourseRegistration registerCourse(String courseId) throws CourseException {
        Course course = null;
        for (Course c : courseList) if (c.getId().equals(courseId)) { course = c; break; }
        if (course == null) throw new CourseException("Không tìm thấy học phần: " + courseId);

        if (registrations.containsKey(courseId)
                && !"CANCELLED".equals(registrations.get(courseId).getStatus()))
            throw new CourseException("Bạn đã đăng ký môn \"" + course.getName() + "\" rồi!");

        if (getTotalRegisteredCredits() + course.getCredits() > MAX_CREDITS)
            throw new CourseException("Vượt quá số tín chỉ tối đa (" + MAX_CREDITS + " tín chỉ)!");

        if (!course.isAvailable())
            throw new CourseException("Học phần \"" + course.getName() + "\" đã đầy!");

        course.incrementStudents();
        CourseRegistration reg = new CourseRegistration("reg_" + System.currentTimeMillis(), course);
        registrations.put(courseId, reg);
        return reg;
    }

    public void cancelRegistration(String courseId) throws CourseException {
        CourseRegistration reg = registrations.get(courseId);
        if (reg == null || "CANCELLED".equals(reg.getStatus()))
            throw new CourseException("Không tìm thấy đăng ký để hủy.");
        reg.setStatus("CANCELLED");
        registrations.remove(courseId);
    }
    /** Xóa toàn bộ giỏ đăng ký tạm (pending) sau khi xác nhận. */
    public void clearPendingRegistrations() {
        registrations.clear();
    }

    /** Tìm Course theo id, trả về null nếu không tìm thấy. */
    public Course findById(String id) {
        for (Course c : courseList) {
            if (c.getId().equals(id)) return c;
        }
        return null;
    }
}