package com.utc2.appreborn.ui.results;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.utc2.appreborn.data.local.entity.AcademicWarningEntity;
import com.utc2.appreborn.data.local.entity.CourseEntity;
import com.utc2.appreborn.data.local.entity.SemesterEntity;
import com.utc2.appreborn.data.repository.AcademicResultRepository;

import java.util.List;

/**
 * AcademicResultViewModel - Lớp trung gian giữa Repository và các Fragment học vụ.
 *
 * Kế thừa AndroidViewModel (thay vì ViewModel) để có access vào Application context
 * mà không giữ reference đến Activity/Fragment — tránh memory leak.
 *
 * Vòng đời:
 *  Fragment/Activity → observe LiveData → ViewModel → Repository → Room/API
 *
 * Các Fragment sử dụng ViewModel này:
 *  - WarningsFragment    → getWarnings(), getActiveWarningCount()
 *  - GradesFragment      → getSemesters(), getAllCourses()
 *  - AcademicResultsFragment → getSemesters()
 */
public class AcademicResultViewModel extends AndroidViewModel {

    private final AcademicResultRepository repository;

    /**
     * ID sinh viên hiện tại đang đăng nhập.
     * Tạm thời hardcode = 1L để test với Mock Data.
     *
     * TODO: Thay bằng ID thực lấy từ SharedPreferences / UserSession khi login xong.
     *  VD: long currentUserId = UserSession.getInstance(getApplication()).getUserId();
     */
    private static final long CURRENT_USER_ID = 1L;

    // ─── Constructor ────────────────────────────────────────────────────────────

    public AcademicResultViewModel(@NonNull Application application) {
        super(application);
        repository = new AcademicResultRepository(application);
    }

    // ════════════════════════════════════════════════════════════════════════════
    //  SEMESTER
    // ════════════════════════════════════════════════════════════════════════════

    /**
     * Lấy danh sách kỳ học của sinh viên đang đăng nhập.
     * Fragment observe LiveData này — UI tự cập nhật khi data thay đổi.
     *
     * Cách dùng trong Fragment:
     * <pre>
     *   viewModel.getSemesters().observe(getViewLifecycleOwner(), semesters -> {
     *       // cập nhật adapter
     *   });
     * </pre>
     */
    public LiveData<List<SemesterEntity>> getSemesters() {
        return repository.getSemesters(CURRENT_USER_ID);
    }

    /**
     * Lấy chi tiết một kỳ học — dùng cho màn hình xem kết quả theo kỳ.
     *
     * @param semesterId ID kỳ học cần xem
     */
    public LiveData<SemesterEntity> getSemesterById(long semesterId) {
        return repository.getSemesterById(semesterId);
    }

    // ════════════════════════════════════════════════════════════════════════════
    //  COURSE
    // ════════════════════════════════════════════════════════════════════════════

    /**
     * Lấy toàn bộ danh sách môn học trong catalog.
     * Dùng cho GradesFragment để hiển thị bảng điểm.
     */
    public LiveData<List<CourseEntity>> getAllCourses() {
        return repository.getAllCourses();
    }

    // ════════════════════════════════════════════════════════════════════════════
    //  ACADEMIC WARNING
    // ════════════════════════════════════════════════════════════════════════════

    /**
     * Lấy toàn bộ cảnh báo học vụ của sinh viên, mới nhất lên đầu.
     * Dùng cho WarningsFragment.
     *
     * Cách dùng trong WarningsFragment:
     * <pre>
     *   viewModel.getWarnings().observe(getViewLifecycleOwner(), warnings -> {
     *       allWarnings.clear();
     *       allWarnings.addAll(warnings);
     *       warningAdapter.notifyDataSetChanged();
     *       updateStatCards();
     *   });
     * </pre>
     */
    public LiveData<List<AcademicWarningEntity>> getWarnings() {
        return repository.getWarnings(CURRENT_USER_ID);
    }

    /**
     * Lấy cảnh báo học vụ theo kỳ học — dùng cho filter chip theo kỳ.
     *
     * @param semesterId ID kỳ học cần lọc
     */
    public LiveData<List<AcademicWarningEntity>> getWarningsBySemester(long semesterId) {
        return repository.getWarningsBySemester(CURRENT_USER_ID, semesterId);
    }

    /**
     * Đếm số cảnh báo đang active — dùng để hiển thị badge số trên bottom nav.
     *
     * Cách dùng trong MainActivity:
     * <pre>
     *   viewModel.getActiveWarningCount().observe(this, count -> {
     *       BadgeDrawable badge = bottomNav.getOrCreateBadge(R.id.nav_warnings);
     *       badge.setNumber(count);
     *       badge.setVisible(count > 0);
     *   });
     * </pre>
     */
    public LiveData<Integer> getActiveWarningCount() {
        return repository.getActiveWarningCount(CURRENT_USER_ID);
    }

    // ════════════════════════════════════════════════════════════════════════════
    //  LIFECYCLE
    // ════════════════════════════════════════════════════════════════════════════

    /**
     * Được gọi khi ViewModel bị destroy (user đóng app hoặc Fragment bị remove vĩnh viễn).
     * Dọn dẹp tài nguyên nếu cần.
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        // Không cần cancel LiveData — Room tự quản lý
        // Nếu dùng RxJava sau này: disposables.clear() ở đây
    }
}