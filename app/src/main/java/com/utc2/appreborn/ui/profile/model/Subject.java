package com.utc2.appreborn.ui.profile.model;

public class Subject {
    private String code;
    private String name;
    private String credit;
    private String score;
    private int semester;   // 1 hoặc 2
    private boolean isHeader; // true nếu là dòng tiêu đề "KỲ HỌC X"

    // Constructor đầy đủ
    public Subject(String code, String name, String credit, String score, int semester, boolean isHeader) {
        this.code = code;
        this.name = name;
        this.credit = credit;
        this.score = score;
        this.semester = semester;
        this.isHeader = isHeader;
    }

    // Getter
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getCredit() { return credit; }
    public String getScore() { return score; }
    public int getSemester() { return semester; }
    public boolean isHeader() { return isHeader; }
}