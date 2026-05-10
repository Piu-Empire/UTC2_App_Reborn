package com.utc2.appreborn.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.utc2.appreborn.data.local.dao.AcademicWarningDao;
import com.utc2.appreborn.data.local.dao.CourseDao;
import com.utc2.appreborn.data.local.dao.SemesterDao;
import com.utc2.appreborn.data.local.entity.AcademicWarningEntity;
import com.utc2.appreborn.data.local.entity.CourseEntity;
import com.utc2.appreborn.data.local.entity.SemesterEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * AcademicResultRepository - Single Source of Truth cho dữ liệu học vụ.
 *
 * Kiến trúc:
 *  ViewModel  →  Repository  →  [Room Cache] hoặc [Remote API]
 *
 * Trạng thái hiện tại: sử dụng MOCK DATA để test UI.
 *
 * Để kích hoạt Room hoặc API thật:
 *  1. Đăng ký các DAO trong AppDatabase
 *  2. Bỏ comment các khối "TODO: Uncomment when Room Database/API is ready"
 *  3. Xoá / comment lại phần Mock Data bên dưới
 */
public class AcademicResultRepository {

    // ─── DAOs (sẽ dùng khi Room sẵn sàng) ──────────────────────────────────────

    private final SemesterDao semesterDao;
    private final CourseDao courseDao;
    private final AcademicWarningDao warningDao;

    // ─── Constructor ────────────────────────────────────────────────────────────

    public AcademicResultRepository(Application application) {

        // TODO: Uncomment when Room Database is ready
        // AppDatabase db = AppDatabase.getInstance(application);
        // semesterDao = db.semesterDao();
        // courseDao   = db.courseDao();
        // warningDao  = db.academicWarningDao();

        semesterDao = null;
        courseDao   = null;
        warningDao  = null;
    }

    // ════════════════════════════════════════════════════════════════════════════
    //  SEMESTER
    // ════════════════════════════════════════════════════════════════════════════

    public LiveData<List<SemesterEntity>> getSemesters(long userId) {

        // TODO: Uncomment when Room Database is ready
        // return semesterDao.getSemestersByUserId(userId);

        // ── MOCK DATA ───────────────────────────────────────────────────────────
        MutableLiveData<List<SemesterEntity>> mockLiveData = new MutableLiveData<>();
        List<SemesterEntity> mockList = new ArrayList<>();

        mockList.add(new SemesterEntity(1L, userId, "Học kỳ 1", "2023-2024", 1,
                "2023-09-04", "2024-01-05", 3.45, 20, 20));
        mockList.add(new SemesterEntity(2L, userId, "Học kỳ 2", "2023-2024", 2,
                "2024-02-05", "2024-06-07", 3.20, 18, 16));
        mockList.add(new SemesterEntity(3L, userId, "Học kỳ 1", "2024-2025", 1,
                "2024-09-02", "2025-01-03", 3.60, 21, 21));
        mockList.add(new SemesterEntity(4L, userId, "Học kỳ 2", "2024-2025", 2,
                "2025-02-03", "2025-06-06", 3.10, 19, 17));

        mockLiveData.setValue(mockList);
        return mockLiveData;
        // ── END MOCK DATA ───────────────────────────────────────────────────────
    }

    public LiveData<SemesterEntity> getSemesterById(long semesterId) {

        // TODO: Uncomment when Room Database is ready
        // MutableLiveData<SemesterEntity> result = new MutableLiveData<>();
        // AsyncTask.execute(() -> result.postValue(semesterDao.getSemesterById(semesterId)));
        // return result;

        // ── MOCK DATA ───────────────────────────────────────────────────────────
        MutableLiveData<SemesterEntity> mockLiveData = new MutableLiveData<>();
        mockLiveData.setValue(new SemesterEntity(semesterId, 1L,
                "Học kỳ 1", "2024-2025", 1,
                "2024-09-02", "2025-01-03", 3.60, 21, 21));
        return mockLiveData;
        // ── END MOCK DATA ───────────────────────────────────────────────────────
    }

    // ════════════════════════════════════════════════════════════════════════════
    //  COURSE
    // ════════════════════════════════════════════════════════════════════════════

    public LiveData<List<CourseEntity>> getAllCourses() {

        // TODO: Uncomment when Room Database is ready
        // return courseDao.getAllCourses();

        // ── MOCK DATA ───────────────────────────────────────────────────────────
        MutableLiveData<List<CourseEntity>> mockLiveData = new MutableLiveData<>();
        List<CourseEntity> mockList = new ArrayList<>();

        mockList.add(new CourseEntity(101L, "IT3040", "Lập trình Java nâng cao",
                3, 30, 15, "Công nghệ thông tin",
                "Các kỹ thuật lập trình Java hiện đại."));
        mockList.add(new CourseEntity(102L, "IT3080", "Cơ sở dữ liệu",
                3, 30, 15, "Công nghệ thông tin",
                "Thiết kế CSDL quan hệ, SQL nâng cao."));
        mockList.add(new CourseEntity(103L, "IT4060", "Phát triển ứng dụng di động",
                3, 20, 25, "Công nghệ thông tin",
                "Phát triển ứng dụng Android và iOS."));

        mockLiveData.setValue(mockList);
        return mockLiveData;
        // ── END MOCK DATA ───────────────────────────────────────────────────────
    }

    // ════════════════════════════════════════════════════════════════════════════
    //  ACADEMIC WARNING
    // ════════════════════════════════════════════════════════════════════════════

    public LiveData<List<AcademicWarningEntity>> getWarnings(long userId) {

        // TODO: Uncomment when Room Database is ready
        // return warningDao.getWarningsByUserId(userId);

        // ── MOCK DATA ───────────────────────────────────────────────────────────
        MutableLiveData<List<AcademicWarningEntity>> mockLiveData = new MutableLiveData<>();
        List<AcademicWarningEntity> mockList = new ArrayList<>();

        mockList.add(new AcademicWarningEntity(1001L, userId, 4L,
                "FAILED_EXAM",
                "Điểm thi kết thúc học phần môn Giải tích 1 dưới 4.0. Yêu cầu thi lại.",
                1_740_000_000_000L, null, "ACTIVE"));

        mockList.add(new AcademicWarningEntity(1002L, userId, 4L,
                "ATTENDANCE",
                "Vắng quá 20% tổng số buổi học môn Lập trình Java nâng cao.",
                1_741_000_000_000L, null, "ACTIVE"));

        mockList.add(new AcademicWarningEntity(1003L, userId, 2L,
                "LOW_GPA",
                "GPA học kỳ 2 năm 2023-2024 đạt 3.20, thấp hơn ngưỡng học bổng 3.50.",
                1_712_000_000_000L, 1_720_000_000_000L, "RESOLVED"));

        mockLiveData.setValue(mockList);
        return mockLiveData;
        // ── END MOCK DATA ───────────────────────────────────────────────────────
    }

    public LiveData<List<AcademicWarningEntity>> getWarningsBySemester(long userId, long semesterId) {

        // TODO: Uncomment when Room Database is ready
        // return warningDao.getWarningsByUserAndSemester(userId, semesterId);

        // ── MOCK DATA ───────────────────────────────────────────────────────────
        MutableLiveData<List<AcademicWarningEntity>> mockLiveData = new MutableLiveData<>();
        List<AcademicWarningEntity> filtered = new ArrayList<>();
        if (semesterId == 4L) {
            filtered.add(new AcademicWarningEntity(1001L, userId, 4L,
                    "FAILED_EXAM", "Điểm thi môn Giải tích 1 dưới 4.0.",
                    1_740_000_000_000L, null, "ACTIVE"));
        }
        mockLiveData.setValue(filtered);
        return mockLiveData;
        // ── END MOCK DATA ───────────────────────────────────────────────────────
    }

    public LiveData<Integer> getActiveWarningCount(long userId) {

        // TODO: Uncomment when Room Database is ready
        // MutableLiveData<Integer> result = new MutableLiveData<>();
        // AsyncTask.execute(() ->
        //     result.postValue(warningDao.countActiveWarnings(userId, "ACTIVE"))
        // );
        // return result;

        // ── MOCK DATA ───────────────────────────────────────────────────────────
        MutableLiveData<Integer> mockCount = new MutableLiveData<>();
        mockCount.setValue(2);
        return mockCount;
        // ── END MOCK DATA ───────────────────────────────────────────────────────
    }

    // ════════════════════════════════════════════════════════════════════════════
    //  CACHE MANAGEMENT
    // ════════════════════════════════════════════════════════════════════════════

    public void clearCacheForUser(long userId) {
        // TODO: Uncomment when Room Database is ready
        // AsyncTask.execute(() -> {
        //     semesterDao.deleteAllByUserId(userId);
        //     warningDao.deleteAllByUserId(userId);
        // });
    }
}