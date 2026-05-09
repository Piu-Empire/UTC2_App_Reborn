package com.utc2.appreborn.data.local;

import com.utc2.appreborn.utils.MockHelper;

/**
 * StudentLocalDataSource
 * ──────────────────────────────────────────────────────────────
 * Abstracts all local-storage reads for student profile data.
 *
 * Current state  → backed by MockHelper (no DB wired yet).
 * Future state   → replace {@link #getStudentProfile()} body
 *                  with a real SQLite / Room / SharedPreferences read.
 *
 * Used by {@link com.utc2.appreborn.data.repository.StudentRepository}.
 *
 * Package: com.utc2.appreborn.data.local
 */
public class StudentLocalDataSource {

    // ── Singleton ─────────────────────────────────────────────
    private static StudentLocalDataSource instance;

    private StudentLocalDataSource() {}

    public static StudentLocalDataSource getInstance() {
        if (instance == null) {
            instance = new StudentLocalDataSource();
        }
        return instance;
    }

    // ═══════════════════════════════════════════════════════════
    //  Public API
    // ═══════════════════════════════════════════════════════════

    /**
     * Returns the currently logged-in student's profile.
     *
     * TODO: Replace mock data with a real DB / SharedPrefs read:
     *   String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
     *   // query STUDENT_PROFILE WHERE user_id = uid
     *   return new StudentProfile(cursor.getString("student_code"),
     *                             cursor.getString("full_name"));
     */
    public StudentProfile getStudentProfile() {
        return new StudentProfile(
                MockHelper.getMockStudentCode(),
                MockHelper.getMockFullName()
        );
    }
}