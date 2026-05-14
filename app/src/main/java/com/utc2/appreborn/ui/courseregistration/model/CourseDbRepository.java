package com.utc2.appreborn.ui.courseregistration.model;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.utc2.appreborn.data.local.AppDatabase;
import com.utc2.appreborn.data.local.dao.CourseRegistrationDao;
import com.utc2.appreborn.data.local.entity.CourseRegistrationEntity;
import com.utc2.appreborn.ui.courseregistration.exception.CourseException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * CourseDbRepository — Thay thế CourseStorage (file JSON) và phần đăng ký trong RAM.
 *
 * Kết nối trực tiếp với AppDatabase (Room) để:
 *  - Lưu đăng ký học phần vào database → tồn tại sau khi thoát app
 *  - Đọc danh sách đã đăng ký theo userId → đồng bộ đúng người dùng
 *
 * Cách dùng (dùng chung pattern với DormitoryDbRepository):
 *  - Mọi thao tác DB chạy trên background thread (ExecutorService)
 *  - Kết quả trả về UI thread qua Callback
 *
 * Thay CourseStorage.saveConfirmedIds() / loadConfirmedIds() bằng class này
 * trong CourseRegistrationActivity.
 */
public class CourseDbRepository {

    // ── Singleton ─────────────────────────────────────────────
    private static volatile CourseDbRepository instance;

    private final CourseRegistrationDao dao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private CourseDbRepository(Context context) {
        dao = AppDatabase.getInstance(context).courseRegistrationDao();
    }

    public static CourseDbRepository getInstance(Context context) {
        if (instance == null) {
            synchronized (CourseDbRepository.class) {
                if (instance == null) {
                    instance = new CourseDbRepository(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    // ─────────────────────────────────────────────────────────
    // CALLBACK INTERFACES
    // ─────────────────────────────────────────────────────────

    public interface CourseIdsCallback {
        void onResult(List<String> courseIds);
    }

    public interface SimpleCallback {
        void onDone(CourseException error);
    }

    // ─────────────────────────────────────────────────────────
    // LẤY DANH SÁCH ĐÃ ĐĂNG KÝ
    // ─────────────────────────────────────────────────────────

    /**
     * Lấy danh sách courseId đã CONFIRMED của user.
     * Thay thế CourseStorage.loadConfirmedIds()
     */
    public void loadConfirmedIds(long userId, CourseIdsCallback callback) {
        executor.execute(() -> {
            List<String> ids = dao.getConfirmedCourseIds(userId);
            mainHandler.post(() -> callback.onResult(ids));
        });
    }

    // ─────────────────────────────────────────────────────────
    // XÁC NHẬN ĐĂNG KÝ (GHI VÀO DB)
    // ─────────────────────────────────────────────────────────

    /**
     * Xác nhận toàn bộ môn trong giỏ (pending) → lưu vào DB với status CONFIRMED.
     * Thay thế CourseStorage.saveConfirmedIds() + courseRepo.clearPendingRegistrations()
     *
     * @param userId  ID người dùng hiện tại (từ SessionManager)
     * @param courses Danh sách Course đang pending trong giỏ
     */
    public void confirmRegistrations(long userId, List<Course> courses, SimpleCallback callback) {
        executor.execute(() -> {
            try {
                List<CourseRegistrationEntity> entities = new ArrayList<>();
                for (Course c : courses) {
                    // Kiểm tra đã đăng ký chưa (tránh duplicate)
                    CourseRegistrationEntity existing =
                            dao.getRegistrationByCourseId(userId, c.getId());
                    if (existing != null) continue;

                    CourseRegistrationEntity entity = new CourseRegistrationEntity();
                    entity.regId        = "reg_" + userId + "_" + c.getId();
                    entity.userId       = userId;
                    entity.courseId     = c.getId();
                    entity.courseCode   = c.getCourseCode();
                    entity.courseName   = c.getName();
                    entity.credits      = c.getCredits();
                    entity.lecturer     = c.getLecturer();
                    entity.schedule     = c.getSchedule();
                    entity.room         = c.getRoom();
                    entity.semester     = c.getSemester();
                    entity.status       = "CONFIRMED";
                    entity.registeredAt = System.currentTimeMillis();
                    entities.add(entity);
                }
                dao.insertAll(entities);
                mainHandler.post(() -> callback.onDone(null));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onDone(
                        new CourseException("Lỗi khi lưu đăng ký: " + e.getMessage())));
            }
        });
    }

    // ─────────────────────────────────────────────────────────
    // HỦY ĐĂNG KÝ
    // ─────────────────────────────────────────────────────────

    /**
     * Hủy đăng ký một môn học của user.
     */
    public void cancelRegistration(long userId, String courseId, SimpleCallback callback) {
        executor.execute(() -> {
            dao.cancelByCourseId(userId, courseId);
            mainHandler.post(() -> callback.onDone(null));
        });
    }

    /**
     * Xóa toàn bộ đăng ký của user (dùng khi logout).
     */
    public void clearAll(long userId, SimpleCallback callback) {
        executor.execute(() -> {
            dao.deleteAllByUserId(userId);
            mainHandler.post(() -> callback.onDone(null));
        });
    }

    // ─────────────────────────────────────────────────────────
    // TÍNH TỔNG TÍN CHỈ ĐÃ ĐĂNG KÝ (từ DB)
    // ─────────────────────────────────────────────────────────

    public interface IntCallback {
        void onResult(int value);
    }

    /**
     * Lấy tổng tín chỉ đã CONFIRMED của user từ DB.
     */
    public void getTotalConfirmedCredits(long userId, IntCallback callback) {
        executor.execute(() -> {
            int total = dao.getTotalCredits(userId);
            mainHandler.post(() -> callback.onResult(total));
        });
    }
}