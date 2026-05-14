package com.utc2.appreborn.data.local;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

// ── DAO Imports ──────────────────────────────────────────────
import com.utc2.appreborn.data.local.dao.AcademicWarningDao;
import com.utc2.appreborn.data.local.dao.AdvisorDao;
import com.utc2.appreborn.data.local.dao.CourseDao;
import com.utc2.appreborn.data.local.dao.CourseRegistrationDao;
import com.utc2.appreborn.data.local.dao.DormitoryDao;
import com.utc2.appreborn.data.local.dao.DormitoryRegistrationDao;
import com.utc2.appreborn.data.local.dao.ScheduleDao;
import com.utc2.appreborn.data.local.dao.SemesterDao;
import com.utc2.appreborn.data.local.dao.StudentDao;
import com.utc2.appreborn.data.local.dao.UserDao;

// ── Entity Imports ───────────────────────────────────────────
import com.utc2.appreborn.data.local.entity.AcademicWarningEntity;
import com.utc2.appreborn.data.local.entity.AdvisorEntity;
import com.utc2.appreborn.data.local.entity.CourseEntity;
import com.utc2.appreborn.data.local.entity.CourseRegistrationEntity;
import com.utc2.appreborn.data.local.entity.DormitoryRegistrationEntity;
import com.utc2.appreborn.data.local.entity.DormitoryRoomEntity;
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
                // ── User / Student ────────────────────────
                UserEntity.class,
                StudentProfileEntity.class,

                // ── Semester / Course / Schedule ────────
                SemesterEntity.class,
                CourseEntity.class,
                ScheduleEntity.class,

                // ── Warning / Advisor ───────────────────
                AcademicWarningEntity.class,
                AdvisorEntity.class,

                // ── Kí túc xá (thêm v4) ───────────────────
                DormitoryRoomEntity.class,
                DormitoryRegistrationEntity.class,

                // ── Đăng ký học phần (thêm v5) ────────────
                CourseRegistrationEntity.class
        },
        version = 6,  // ← tăng lên 6
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
    public abstract DormitoryRegistrationDao dormitoryRegistrationDao();

    // ── DAO (mới v4) — KTX ───────────────────────────────────
    public abstract DormitoryDao dormitoryDao();

    // ── DAO (mới v5) — Đăng ký học phần ─────────────────────
    public abstract CourseRegistrationDao courseRegistrationDao();

    // ── Singleton ─────────────────────────────────────────────
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
                            .addMigrations(
                                    MIGRATION_1_2,
                                    MIGRATION_2_3,
                                    MIGRATION_3_4,
                                    MIGRATION_4_5,
                                    MIGRATION_5_6   // ← FIX migration mới
                            )
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
     * v1 → v2:
     *  - thêm semester
     *  - thêm course
     *  - thêm academic_warning
     */
    static final Migration MIGRATION_1_2 =
            new Migration(1, 2) {

                @Override
                public void migrate(@NonNull SupportSQLiteDatabase db) {

                    // ── semester ─────────────────────────

                    db.execSQL(
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

                    db.execSQL(
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

                    db.execSQL(
                            "CREATE UNIQUE INDEX IF NOT EXISTS `index_course_course_code` " +
                                    "ON `course` (`course_code`)"
                    );

                    // ── academic_warning ────────────────

                    db.execSQL(
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
                                    "ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED)"
                    );

                    db.execSQL(
                            "CREATE INDEX IF NOT EXISTS `index_academic_warning_user_id` " +
                                    "ON `academic_warning` (`user_id`)"
                    );

                    db.execSQL(
                            "CREATE INDEX IF NOT EXISTS `index_academic_warning_semester_id` " +
                                    "ON `academic_warning` (`semester_id`)"
                    );
                }
            };

    /**
     * v2 → v3:
     *  - thêm schedule
     *  - thêm advisor
     */
    static final Migration MIGRATION_2_3 =
            new Migration(2, 3) {

                @Override
                public void migrate(@NonNull SupportSQLiteDatabase db) {

                    db.execSQL(
                            "CREATE TABLE IF NOT EXISTS `schedule` (" +
                                    "`schedule_id` INTEGER NOT NULL, " +
                                    "`user_id` INTEGER NOT NULL, " +
                                    "`course_id` INTEGER NOT NULL, " +
                                    "`day_of_week` INTEGER NOT NULL, " +
                                    "`period_start` INTEGER NOT NULL, " +
                                    "`period_end` INTEGER NOT NULL, " +
                                    "`room` TEXT, " +
                                    "`lecturer` TEXT, " +
                                    "`semester_id` INTEGER NOT NULL, " +
                                    "PRIMARY KEY(`schedule_id`))"
                    );

                    db.execSQL(
                            "CREATE INDEX IF NOT EXISTS `index_schedule_user_id` " +
                                    "ON `schedule` (`user_id`)"
                    );

                    db.execSQL(
                            "CREATE INDEX IF NOT EXISTS `index_schedule_semester_id` " +
                                    "ON `schedule` (`semester_id`)"
                    );

                    db.execSQL(
                            "CREATE TABLE IF NOT EXISTS `advisor` (" +
                                    "`advisor_id` INTEGER NOT NULL, " +
                                    "`user_id` INTEGER NOT NULL, " +
                                    "`full_name` TEXT, " +
                                    "`email` TEXT, " +
                                    "`phone` TEXT, " +
                                    "`department` TEXT, " +
                                    "PRIMARY KEY(`advisor_id`))"
                    );

                    db.execSQL(
                            "CREATE INDEX IF NOT EXISTS `index_advisor_user_id` " +
                                    "ON `advisor` (`user_id`)"
                    );
                }
            };

    /**
     * v3 → v4:
     *  - thêm dormitory_room
     *  - thêm dormitory_registration
     */
    static final Migration MIGRATION_3_4 =
            new Migration(3, 4) {

                @Override
                public void migrate(@NonNull SupportSQLiteDatabase db) {

                    // ── dormitory_room ────────────────────

                    db.execSQL(
                            "CREATE TABLE IF NOT EXISTS `dormitory_room` (" +
                                    "`room_id` INTEGER NOT NULL, " +
                                    "`room_name` TEXT, " +
                                    "`building` TEXT, " +
                                    "`capacity` INTEGER NOT NULL, " +
                                    "`current_occupants` INTEGER NOT NULL, " +
                                    "`price_per_month` INTEGER NOT NULL, " +
                                    "`room_type` TEXT, " +
                                    "`status` TEXT, " +
                                    "`description` TEXT, " +
                                    "PRIMARY KEY(`room_id`))"
                    );

                    // ── dormitory_registration ────────────

                    db.execSQL(
                            "CREATE TABLE IF NOT EXISTS `dormitory_registration` (" +
                                    "`registration_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                                    "`user_id` INTEGER NOT NULL, " +
                                    "`room_id` INTEGER NOT NULL, " +
                                    "`months` INTEGER NOT NULL, " +
                                    "`total_price` INTEGER NOT NULL, " +
                                    "`status` TEXT, " +
                                    "`start_date` TEXT, " +
                                    "`end_date` TEXT, " +
                                    "`created_at` INTEGER NOT NULL, " +
                                    "FOREIGN KEY(`user_id`) REFERENCES `user_profile`(`user_id`) ON DELETE CASCADE, " +
                                    "FOREIGN KEY(`room_id`) REFERENCES `dormitory_room`(`room_id`) ON DELETE CASCADE)"
                    );

                    db.execSQL(
                            "CREATE INDEX IF NOT EXISTS `index_dormitory_registration_user_id` " +
                                    "ON `dormitory_registration` (`user_id`)"
                    );

                    db.execSQL(
                            "CREATE INDEX IF NOT EXISTS `index_dormitory_registration_room_id` " +
                                    "ON `dormitory_registration` (`room_id`)"
                    );
                }
            };

    /**
     * v4 → v5:
     *  - thêm course_registration
     */
    static final Migration MIGRATION_4_5 =
            new Migration(4, 5) {

                @Override
                public void migrate(@NonNull SupportSQLiteDatabase db) {

                    db.execSQL(
                            "CREATE TABLE IF NOT EXISTS `course_registration` (" +
                                    "`reg_id` TEXT NOT NULL, " +
                                    "`user_id` INTEGER NOT NULL, " +
                                    "`course_id` TEXT, " +
                                    "`course_code` TEXT, " +
                                    "`course_name` TEXT, " +
                                    "`credits` INTEGER NOT NULL, " +
                                    "`lecturer` TEXT, " +
                                    "`schedule` TEXT, " +
                                    "`room` TEXT, " +
                                    "`semester` TEXT, " +
                                    "`status` TEXT, " +
                                    "`registered_at` INTEGER NOT NULL, " +
                                    "PRIMARY KEY(`reg_id`))"
                    );

                    db.execSQL(
                            "CREATE UNIQUE INDEX IF NOT EXISTS `index_course_registration_user_id_course_id` " +
                                    "ON `course_registration` (`user_id`, `course_id`)"
                    );

                    db.execSQL(
                            "CREATE INDEX IF NOT EXISTS `index_course_registration_user_id` " +
                                    "ON `course_registration` (`user_id`)"
                    );
                }
            };

    /**
     * v5 → v6:
     * FIX crash đăng ký KTX do FK user_profile
     */
    static final Migration MIGRATION_5_6 =
            new Migration(5, 6) {

                @Override
                public void migrate(@NonNull SupportSQLiteDatabase db) {

                    // 1. Tắt FK tạm thời

                    db.execSQL("PRAGMA foreign_keys=OFF");

                    // 2. Tạo bảng mới

                    db.execSQL(
                            "CREATE TABLE IF NOT EXISTS `dormitory_registration_new` (" +
                                    "`registration_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                                    "`user_id` INTEGER NOT NULL, " +
                                    "`room_id` INTEGER NOT NULL, " +
                                    "`months` INTEGER NOT NULL, " +
                                    "`total_price` INTEGER NOT NULL, " +
                                    "`status` TEXT, " +
                                    "`start_date` TEXT, " +
                                    "`end_date` TEXT, " +
                                    "`created_at` INTEGER NOT NULL, " +
                                    "FOREIGN KEY(`room_id`) REFERENCES `dormitory_room`(`room_id`) ON DELETE CASCADE)"
                    );

                    // 3. Copy dữ liệu cũ

                    db.execSQL(
                            "INSERT OR IGNORE INTO `dormitory_registration_new` " +
                                    "(registration_id, user_id, room_id, months, total_price, " +
                                    "status, start_date, end_date, created_at) " +
                                    "SELECT registration_id, user_id, room_id, months, total_price, " +
                                    "status, start_date, end_date, created_at " +
                                    "FROM `dormitory_registration`"
                    );

                    // 4. Xóa bảng cũ

                    db.execSQL("DROP TABLE `dormitory_registration`");

                    // 5. Đổi tên bảng mới

                    db.execSQL(
                            "ALTER TABLE `dormitory_registration_new` " +
                                    "RENAME TO `dormitory_registration`"
                    );

                    // 6. Tạo lại index

                    db.execSQL(
                            "CREATE INDEX IF NOT EXISTS `index_dormitory_registration_user_id` " +
                                    "ON `dormitory_registration` (`user_id`)"
                    );

                    db.execSQL(
                            "CREATE INDEX IF NOT EXISTS `index_dormitory_registration_room_id` " +
                                    "ON `dormitory_registration` (`room_id`)"
                    );

                    // 7. Bật lại FK

                    db.execSQL("PRAGMA foreign_keys=ON");
                }
            };
}