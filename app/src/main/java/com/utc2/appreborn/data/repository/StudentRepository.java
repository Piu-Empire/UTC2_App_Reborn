package com.utc2.appreborn.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.utc2.appreborn.data.local.StudentProfile;
import com.utc2.appreborn.utils.SessionManager;

/**
 * StudentRepository
 * ──────────────────────────────────────────────────────────────
 * Nguồn dữ liệu duy nhất cho thông tin sinh viên đang đăng nhập.
 * <p>
 * Ưu tiên đọc từ SessionManager cache (được cập nhật sau khi
 * HomeViewModel fetch /api/v1/profile/me thành công).
 * Không còn dùng mock data hoặc GoogleSignIn để lấy tên.
 */
public class StudentRepository {

    private static StudentRepository instance;
    private final SessionManager sessionManager;
    private final MutableLiveData<StudentProfile> studentProfileLiveData = new MutableLiveData<>();

    private StudentRepository(Context context) {
        this.sessionManager = SessionManager.getInstance(context);
    }

    public static StudentRepository getInstance(Context context) {
        if (instance == null) {
            instance = new StudentRepository(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Trả về LiveData chứa thông tin StudentProfile từ cache.
     * Gọi updateFromCache() sau khi fetch API để refresh.
     */
    public LiveData<StudentProfile> getStudentProfile() {
        updateFromCache();
        return studentProfileLiveData;
    }

    /**
     * Đọc fullName và studentCode từ SessionManager rồi post lên LiveData.
     * Gọi từ HomeViewModel sau khi fetch profile API thành công.
     */
    public void updateFromCache() {
        String fullName = sessionManager.getCachedFullName();
        String studentCode = sessionManager.getStudentCode();
        studentProfileLiveData.setValue(new StudentProfile(studentCode, fullName));
    }
}