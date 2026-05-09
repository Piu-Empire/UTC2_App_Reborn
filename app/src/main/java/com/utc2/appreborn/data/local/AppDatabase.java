package com.utc2.appreborn.data.local;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// ── DAO Imports ─────────────────────────────────────────────
import com.utc2.appreborn.data.local.dao.AcademicWarningDao;
import com.utc2.appreborn.data.local.dao.AdvisorDao;
import com.utc2.appreborn.data.local.dao.CourseDao;
import com.utc2.appreborn.data.local.dao.ScheduleDao;
import com.utc2.appreborn.data.local.dao.SemesterDao;
import com.utc2.appreborn.data.local.dao.StudentDao;
import com.utc2.appreborn.data.local.dao.UserDao;

// ── Entity Imports ──────────────────────────────────────────
import com.utc2.appreborn.data.local.entity.AcademicWarningEntity;
import com.utc2.appreborn.data.local.entity.AdvisorEntity;
import com.utc2.appreborn.data.local.entity.CourseEntity;
import com.utc2.appreborn.data.local.entity.ScheduleEntity;
import com.utc2.appreborn.data.local.entity.SemesterEntity;
import com.utc2.appreborn.data.local.entity.StudentProfileEntity;
import com.utc2.appreborn.data.local.entity.UserEntity;

/**
 * AppDatabase - Room Database duy nhất của ứng dụng (Singleton).
 *
 * ⚠️ QUAN TRỌNG — Quy tắc tăng version:
 *  Mỗi khi thêm/sửa/xoá Entity hoặc thêm column, BẮT BUỘC phải tăng version
 *  và cung cấp Migration, nếu không Room sẽ crash khi upgrade app.
 *
 *  Lịch sử version:
 *   version = 1 → UserEntity, StudentEntity                    (ban đầu)
 *   version = 2 → + SemesterEntity, CourseEntity,              (lần này)
 *                   AcademicWarningEntity
 *
 * Cách đặt tên DB: "utc2_app_db" — nhất quán, không đổi giữa các version.
 */
@Database(
        entities = {
                // ── User / Student ───────────────────────
                UserEntity.class,
                StudentProfileEntity.class,

                // ── Semester / Course / Schedule ────────
                SemesterEntity.class,
                CourseEntity.class,
                ScheduleEntity.class,

                // ── Warning / Advisor ───────────────────
                AcademicWarningEntity.class,
                AdvisorEntity.class
        },
        version = 3,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    // ── Constants ────────────────────────────────────────────

    private static final String DB_NAME = "utc2_app.db";

    // ── Singleton Instance ───────────────────────────────────

    private static volatile AppDatabase instance;

    // ── DAO Methods ──────────────────────────────────────────

    public abstract ScheduleDao scheduleDao();

    /** DAO cho bảng user_profile (đã có từ v1) */
    public abstract UserDao userDao();

    /** DAO cho bảng student_profile (đã có từ v1) */
    public abstract StudentDao studentDao();

    public abstract AdvisorDao advisorDao();

    /** DAO cho bảng semester (thêm ở v2) */
    public abstract SemesterDao semesterDao();

    /** DAO cho bảng course (thêm ở v2) */
    public abstract CourseDao courseDao();

    /** DAO cho bảng academic_warning (thêm ở v2) */
    public abstract AcademicWarningDao academicWarningDao();

    // ─── Singleton getter ────────────────────────────────────────────────────────

    /**
     * Lấy instance duy nhất của AppDatabase.
     * Dùng Double-Checked Locking để an toàn với đa luồng.
     *
     * @param context Nên truyền vào applicationContext để tránh memory leak.
     * @return AppDatabase instance
     */
    public static AppDatabase getInstance(Context context) {

        if (instance == null) {

            synchronized (AppDatabase.class) {

                if (instance == null) {

                    instance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    DB_NAME
                            )
                            // ── Migration từ v1 → v2 ───────────────
                            .addMigrations(MIGRATION_1_2)

                            // ── Nếu schema khác lớn → recreate DB ──
                            .fallbackToDestructiveMigration()

                            .build();
                }
            }
        }

        return instance;
    }
    // ─────────────────────────────────────────────────────────
    // MIGRATIONS
    // ─────────────────────────────────────────────────────────

    /**
     * Migration v1 → v2:
     *  - thêm semester
     *  - thêm course
     *  - thêm academic_warning
     */
    static final androidx.room.migration.Migration MIGRATION_1_2 =
            new androidx.room.migration.Migration(1, 2) {

                @Override
                public void migrate(@NonNull
                                    androidx.sqlite.db.SupportSQLiteDatabase database) {

                    // ── semester ─────────────────────────

                    database.execSQL(
                            "CREATE TABLE IF NOT EXISTS `semester` (" +
                                    "`semester_id` INTEGER NOT NULL, " +
                                    "`user_id` INTEGER NOT NULL, " +
                                    "`semester_name` TEXT, " +
                                    "`academic_year` TEXT, " +
                                    "`semester_number` INTEGER NOT NULL, " +
                                    "`start_date` TEXT, " +
                                    "`end_date` TEXT, " +
                                    "`gpa` REAL NOT NULL, " +
                                    "`total_credits` INTEGER NOT NULL, " +
                                    "`passed_credits` INTEGER NOT NULL, " +
                                    "PRIMARY KEY(`semester_id`))"
                    );

                    // ── course ───────────────────────────

                    database.execSQL(
                            "CREATE TABLE IF NOT EXISTS `course` (" +
                                    "`course_id` INTEGER NOT NULL, " +
                                    "`course_code` TEXT, " +
                                    "`course_name` TEXT, " +
                                    "`credits` INTEGER NOT NULL, " +
                                    "`theory_hours` INTEGER NOT NULL, " +
                                    "`practice_hours` INTEGER NOT NULL, " +
                                    "`department` TEXT, " +
                                    "`description` TEXT, " +
                                    "PRIMARY KEY(`course_id`))"
                    );

                    database.execSQL(
                            "CREATE UNIQUE INDEX IF NOT EXISTS `index_course_course_code` " +
                                    "ON `course` (`course_code`)"
                    );

                    // ── academic_warning ────────────────

                    database.execSQL(
                            "CREATE TABLE IF NOT EXISTS `academic_warning` (" +
                                    "`warning_id` INTEGER NOT NULL, " +
                                    "`user_id` INTEGER NOT NULL, " +
                                    "`semester_id` INTEGER NOT NULL, " +
                                    "`warning_type` TEXT, " +
                                    "`description` TEXT, " +
                                    "`issued_at` INTEGER, " +
                                    "`resolved_at` INTEGER, " +
                                    "`status` TEXT, " +
                                    "PRIMARY KEY(`warning_id`), " +
                                    "FOREIGN KEY(`semester_id`) REFERENCES `semester`(`semester_id`) " +
                                    "ON UPDATE NO ACTION ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED)"
                    );

                    database.execSQL(
                            "CREATE INDEX IF NOT EXISTS `index_academic_warning_user_id` " +
                                    "ON `academic_warning` (`user_id`)"
                    );

                    database.execSQL(
                            "CREATE INDEX IF NOT EXISTS `index_academic_warning_semester_id` " +
                                    "ON `academic_warning` (`semester_id`)"
                    );
                }
            };
}