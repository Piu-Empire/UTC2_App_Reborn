package com.utc2.appreborn.ui.profile.model;

/**
 * Subject
 * ──────────────────────────────────────────────────────────────
 * Đại diện một môn học trong kết quả học tập của sinh viên.
 *
 * Mapping với TABLE ENROLLMENT (JOIN COURSE, SEMESTER):
 *   code           ↔ COURSE.course_code
 *   name           ↔ COURSE.course_name
 *   credits        ↔ COURSE.credits
 *   score          ↔ ENROLLMENT.total_score  (String để hiển thị "N/A" khi null)
 *   letterGrade    ↔ ENROLLMENT.letter_grade  (A, B+, B, C+, C, D+, D, F)
 *   gradePoint     ↔ ENROLLMENT.grade_point  (0.0 – 4.0)
 *   isPassed       ↔ ENROLLMENT.is_passed
 *   semester       ↔ SEMESTER.semester_number  (1 hoặc 2)
 *   isHeader       ↔ không có trong DB — cờ UI để vẽ dòng tiêu đề "KỲ HỌC X"
 *
 * NOTE: attemptNumber đã bị xóa — cột attempt_number không tồn tại
 *       trong schema TABLE ENROLLMENT.
 */
public class Subject {

    private String  code;         // COURSE.course_code
    private String  name;         // COURSE.course_name
    private int     credits;      // COURSE.credits
    private String  score;        // ENROLLMENT.total_score (null → "N/A")
    private String  letterGrade;  // ENROLLMENT.letter_grade  (VD: "B+")
    private double  gradePoint;   // ENROLLMENT.grade_point  (VD: 3.0)
    private boolean isPassed;     // ENROLLMENT.is_passed
    private int     semester;     // SEMESTER.semester_number  (1 hoặc 2)
    private boolean isHeader;     // cờ UI — true nếu là dòng tiêu đề "KỲ HỌC X"

    // ── Constructor đầy đủ (dùng khi map từ DB / API) ────────

    public Subject(String code, String name, int credits, String score,
                   String letterGrade, double gradePoint, boolean isPassed,
                   int semester, boolean isHeader) {
        this.code        = code;
        this.name        = name;
        this.credits     = credits;
        this.score       = score;
        this.letterGrade = letterGrade;
        this.gradePoint  = gradePoint;
        this.isPassed    = isPassed;
        this.semester    = semester;
        this.isHeader    = isHeader;
    }

    /**
     * Constructor tương thích ngược — dùng cho mock / code cũ.
     *
     * @deprecated Dùng constructor đầy đủ để map đúng schema ENROLLMENT.
     */
    @Deprecated
    public Subject(String code, String name, String credit, String score,
                   int semester, boolean isHeader) {
        this.code        = code;
        this.name        = name;
        this.credits     = parseCredits(credit);
        this.score       = score;
        this.letterGrade = "";
        this.gradePoint  = 0.0;
        this.isPassed    = false;
        this.semester    = semester;
        this.isHeader    = isHeader;
    }

    /** Constructor header row (không có dữ liệu điểm). */
    public static Subject headerOf(int semesterNumber) {
        return new Subject("", "KỲ HỌC " + semesterNumber, 0,
                "", "", 0.0, false, semesterNumber, true);
    }

    /** Constructor header row với label tùy chỉnh (VD: "KỲ 1 – 2025-2026"). */
    public static Subject headerOf(int semesterNumber, String label) {
        return new Subject("", label != null ? label : "KỲ HỌC " + semesterNumber, 0,
                "", "", 0.0, false, semesterNumber, true);
    }

    // ── Getters ──────────────────────────────────────────────

    public String getCode()        { return code; }
    public String getName()        { return name; }

    /** Trả String để tương thích ngược với SubjectAdapter ("3 TC"). */
    public String getCredit()      { return credits + " TC"; }

    /** COURSE.credits (int). */
    public int getCredits()        { return credits; }

    /** ENROLLMENT.total_score — null hiển thị là "N/A". */
    public String getScore()       { return score != null ? score : "N/A"; }

    /** ENROLLMENT.letter_grade (VD: "B+"). Rỗng nếu chưa có điểm. */
    public String getLetterGrade() { return letterGrade; }

    /** ENROLLMENT.grade_point (0.0 – 4.0). */
    public double getGradePoint()  { return gradePoint; }

    /** ENROLLMENT.is_passed. */
    public boolean isPassed()      { return isPassed; }

    /** SEMESTER.semester_number (1 hoặc 2). */
    public int getSemester()       { return semester; }

    /** Cờ UI — true nếu dòng này là tiêu đề "KỲ HỌC X". */
    public boolean isHeader()      { return isHeader; }

    // ── Setters ──────────────────────────────────────────────

    public void setScore(String score)             { this.score = score; }
    public void setLetterGrade(String letterGrade) { this.letterGrade = letterGrade; }
    public void setGradePoint(double gradePoint)   { this.gradePoint = gradePoint; }
    public void setPassed(boolean passed)          { isPassed = passed; }

    // ── Helper ───────────────────────────────────────────────

    private static int parseCredits(String credit) {
        if (credit == null || credit.isEmpty()) return 0;
        try {
            return Integer.parseInt(credit.trim().split(" ")[0]);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}