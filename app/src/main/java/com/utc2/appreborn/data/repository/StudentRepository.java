package com.utc2.appreborn.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.utc2.appreborn.data.local.StudentProfile;
import com.utc2.appreborn.network.ApiClient;
import com.utc2.appreborn.network.ApiResponse;
import com.utc2.appreborn.network.ProfileApiService;
import com.utc2.appreborn.network.dto.ProfileResponse;
import com.utc2.appreborn.utils.MockHelper;
import com.utc2.appreborn.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * StudentRepository
 * ──────────────────────────────────────────────────────────────
 * Nguồn dữ liệu duy nhất cho thông tin sinh viên đang đăng nhập.
 *
 * Thứ tự ưu tiên:
 *   1. SessionManager cache (SharedPreferences) — đã fetch trước đó
 *   2. GET /api/v1/profile/me — fetch từ server, lưu vào SessionManager
 *   3. MockHelper fallback — khi chưa đăng nhập hoặc network lỗi
 */
public class StudentRepository {

    private static final String TAG = "StudentRepository";

    private static StudentRepository instance;
    private final Context context;
    private final MutableLiveData<StudentProfile> profileLiveData = new MutableLiveData<>();

    private StudentRepository(Context context) {
        this.context = context.getApplicationContext();
    }

    public static StudentRepository getInstance(Context context) {
        if (instance == null) {
            instance = new StudentRepository(context);
        }
        return instance;
    }

    /**
     * Trả về LiveData chứa thông tin StudentProfile.
     *
     * Lần đầu gọi: emit ngay cache từ SessionManager (hoặc mock nếu chưa có),
     * đồng thời fetch API nếu user đã đăng nhập để cập nhật data mới nhất.
     */
    public LiveData<StudentProfile> getStudentProfile() {
        // Emit ngay giá trị đang có để UI không bị trống
        emitCachedOrMock();

        // Nếu đã đăng nhập, fetch profile từ server để cập nhật
        SessionManager session = SessionManager.getInstance(context);
        String token = session.getAuthToken();
        if (token != null && !token.isEmpty()) {
            fetchProfileFromApi(token, session);
        }

        return profileLiveData;
    }

    /** Gọi từ HomeViewModel để refresh UI từ cache hiện tại. */
    public void updateFromCache() {
        emitCachedOrMock();
    }

    // ── Private helpers ───────────────────────────────────────

    /** Emit dữ liệu từ SessionManager cache, hoặc mock nếu cache trống. */
    private void emitCachedOrMock() {
        SessionManager session = SessionManager.getInstance(context);
        String fullName    = session.getFullName();
        String studentCode = session.getStudentCode();

        // Nếu cache trống thì dùng mock làm placeholder
        if (fullName.isEmpty()) fullName    = MockHelper.getMockFullName();
        if (studentCode.isEmpty()) studentCode = MockHelper.getMockStudentCode();

        profileLiveData.setValue(new StudentProfile(studentCode, fullName));
    }

    /** Gọi GET /api/v1/profile/me, lưu kết quả vào SessionManager và emit LiveData. */
    private void fetchProfileFromApi(String token, SessionManager session) {
        ProfileApiService api = ApiClient.getInstance(token).create(ProfileApiService.class);
        api.getMyProfile().enqueue(new Callback<ApiResponse<ProfileResponse>>() {

            @Override
            public void onResponse(@NonNull Call<ApiResponse<ProfileResponse>> call,
                                   @NonNull Response<ApiResponse<ProfileResponse>> response) {
                if (!response.isSuccessful() || response.body() == null
                        || !response.body().isSuccess()) {
                    Log.w(TAG, "Profile API failed: HTTP " + response.code());
                    return;
                }

                ProfileResponse data = response.body().getData();
                if (data == null) return;

                String fullName    = data.fullName    != null ? data.fullName    : "";
                String studentCode = data.studentId   != null ? data.studentId   : "";

                // Lưu vào SessionManager để lần sau dùng lại
                session.saveProfile(fullName, studentCode);

                // Cập nhật UI
                profileLiveData.postValue(new StudentProfile(studentCode, fullName));
                Log.d(TAG, "Profile loaded: " + fullName + " / " + studentCode);
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<ProfileResponse>> call,
                                  @NonNull Throwable t) {
                Log.w(TAG, "Profile fetch failed: " + t.getMessage());
                // Giữ nguyên giá trị đã emit (cache hoặc mock)
            }
        });
    }
}