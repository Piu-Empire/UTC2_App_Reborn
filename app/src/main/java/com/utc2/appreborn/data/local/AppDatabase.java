package com.utc2.appreborn.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
// ── DAO Imports ─────────────────────────────────────────────
import com.utc2.appreborn.data.local.dao.AdvisorDao;
import com.utc2.appreborn.data.local.dao.ScheduleDao;
import com.utc2.appreborn.data.local.dao.StudentDao;
import com.utc2.appreborn.data.local.dao.UserDao;

// ── Entity Imports ──────────────────────────────────────────
import com.utc2.appreborn.data.local.entity.AdvisorEntity;
import com.utc2.appreborn.data.local.entity.CourseEntity;
import com.utc2.appreborn.data.local.entity.ScheduleEntity;
import com.utc2.appreborn.data.local.entity.SemesterEntity;
import com.utc2.appreborn.data.local.entity.StudentProfileEntity;
import com.utc2.appreborn.data.local.entity.UserEntity;

@Database(
        entities = {
                // ── Lịch học ─────────────────────────────
                CourseEntity.class,
                SemesterEntity.class,
                ScheduleEntity.class,

                // ── Người dùng ───────────────────────────
                UserEntity.class,
                StudentProfileEntity.class,

                // ── Đánh giá ─────────────────────────────
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

    public abstract UserDao userDao();

    public abstract StudentDao studentDao();

    public abstract AdvisorDao advisorDao();
    // ── Singleton Getter ────────────────────────────────────

    public static AppDatabase getInstance(Context context) {

        if (instance == null) {

            synchronized (AppDatabase.class) {

                if (instance == null) {

                    instance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    DB_NAME
                            )
                            // Xóa DB cũ và tạo lại nếu version thay đổi
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }

        return instance;
    }
}