// PATH: app/src/main/java/com/utc2/appreborn/data/local/AppDatabase.java

package com.utc2.appreborn.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.utc2.appreborn.data.local.dao.AdvisorDao;
import com.utc2.appreborn.data.local.dao.ScheduleDao;
import com.utc2.appreborn.data.local.dao.UserDao;
import com.utc2.appreborn.data.local.entity.AdvisorEntity;
import com.utc2.appreborn.data.local.entity.CourseEntity;
import com.utc2.appreborn.data.local.entity.ScheduleEntity;
import com.utc2.appreborn.data.local.entity.SemesterEntity;
import com.utc2.appreborn.data.local.entity.StudentProfileEntity;
import com.utc2.appreborn.data.local.entity.UserEntity;

@Database(
        entities = {
                // ── Lịch học (version 1) ──────────────────────
                CourseEntity.class,
                SemesterEntity.class,
                ScheduleEntity.class,
                // ── Người dùng & Đánh giá (version 2+) ────────
                UserEntity.class,
                AdvisorEntity.class,
                StudentProfileEntity.class
        },
        version = 3,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase instance;

    // ── DAOs ──────────────────────────────────────────────────────────────────

    public abstract ScheduleDao scheduleDao();
    public abstract UserDao     userDao();
    public abstract AdvisorDao  advisorDao();

    // ── Singleton ─────────────────────────────────────────────────────────────

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "schedule_db"
                            )
                            // fallbackToDestructiveMigration() sẽ xóa và tạo lại DB khi version thay đổi
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }
}