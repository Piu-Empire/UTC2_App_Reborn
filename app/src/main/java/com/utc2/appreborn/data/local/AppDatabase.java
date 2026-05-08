package com.utc2.appreborn.data.local;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// Import đầy đủ các DAO từ cả 2 bên
import com.utc2.appreborn.data.local.dao.StudentDao;
import com.utc2.appreborn.data.local.dao.UserDao;
import com.utc2.appreborn.data.local.dao.ScheduleDao;

// Import đầy đủ các Entity từ cả 2 bên
import com.utc2.appreborn.data.local.entity.StudentEntity;
import com.utc2.appreborn.data.local.entity.UserEntity;
import com.utc2.appreborn.data.local.entity.CourseEntity;
import com.utc2.appreborn.data.local.entity.ScheduleEntity;
import com.utc2.appreborn.data.local.entity.SemesterEntity;

@Database(
        entities = {
                UserEntity.class,
                StudentEntity.class,
                CourseEntity.class,
                SemesterEntity.class,
                ScheduleEntity.class
        },
        version  = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DB_NAME = "utc2_app.db"; // Thống nhất dùng tên này cho cả app
    private static volatile AppDatabase instance;

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    DB_NAME)
                            .fallbackToDestructiveMigration() // Xóa data cũ nếu đổi cấu trúc (rất tiện khi dev)
                            .build();
                }
            }
        }
        return instance;
    }

    // Khai báo đầy đủ các phương thức để lấy DAO
    public abstract UserDao userDao();
    public abstract StudentDao studentDao();
    public abstract ScheduleDao scheduleDao();
}