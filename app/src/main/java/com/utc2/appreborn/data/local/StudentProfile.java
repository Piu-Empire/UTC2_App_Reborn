package com.utc2.appreborn.data.local;

/**
 * StudentProfile
 * ──────────────────────────────────────────────────────────────
 * Lightweight domain model that mirrors the STUDENT_PROFILE
 * database table used in this project.
 *
 * DB Schema (reference):
 *   TABLE STUDENT_PROFILE (
 *       id            INTEGER PRIMARY KEY,
 *       student_code  TEXT NOT NULL,   ← MSSV, encoded in QR
 *       user_id       TEXT             ← FK to USER_PROFILE / Firebase UID
 *   )
 *
 * Package: com.utc2.appreborn.data.local
 */
public class StudentProfile {

    private final String studentCode; // MSSV, e.g. "SV2024789456"
    private final String fullName;    // Denormalised from USER_PROFILE.full_name

    public StudentProfile(String studentCode, String fullName) {
        this.studentCode = studentCode;
        this.fullName    = fullName;
    }

    public String getStudentCode() { return studentCode; }
    public String getFullName()    { return fullName;    }
}