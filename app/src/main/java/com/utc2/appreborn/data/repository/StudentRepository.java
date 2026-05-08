package com.utc2.appreborn.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.utc2.appreborn.data.local.StudentLocalDataSource;
import com.utc2.appreborn.data.local.StudentProfile;

/**
 * StudentRepository
 * ──────────────────────────────────────────────────────────────
 * Nguồn dữ liệu duy nhất cho thông tin sinh viên đang đăng nhập.
 *
 * Thứ tự ưu tiên cho tên hiển thị:
 *   1. Google Account → Display Name
 *   2. Google Account → Email prefix (trước ký tự "@")
 *   3. StudentLocalDataSource → FullName (Dữ liệu mẫu hoặc DB)
 */
public class StudentRepository {

    private static StudentRepository instance;
    private final StudentLocalDataSource localDataSource;
    private final Context context;

    // Sử dụng context để truy cập GoogleSignIn
    private StudentRepository(Context context, StudentLocalDataSource localDataSource) {
        this.context = context.getApplicationContext();
        this.localDataSource = localDataSource;
    }

    public static StudentRepository getInstance(Context context) {
        if (instance == null) {
            instance = new StudentRepository(context, StudentLocalDataSource.getInstance());
        }
        return instance;
    }

    /**
     * Trả về LiveData chứa thông tin StudentProfile.
     */
    public LiveData<StudentProfile> getStudentProfile() {
        MutableLiveData<StudentProfile> liveData = new MutableLiveData<>();

        // Đọc profile từ nguồn dữ liệu cục bộ
        StudentProfile localProfile = localDataSource.getStudentProfile();

        // Xác định tên hiển thị tốt nhất từ Google hoặc Local
        String displayName = resolveDisplayName(localProfile.getFullName());

        // Cập nhật giá trị mới cho LiveData
        liveData.setValue(new StudentProfile(
                localProfile.getStudentCode(),
                displayName
        ));

        return liveData;
    }

    /**
     * Giải quyết tên hiển thị theo thứ tự ưu tiên:
     * Google Display Name → Email prefix → Fallback name.
     */
    private String resolveDisplayName(String fallbackName) {
        // Lấy thông tin tài khoản Google đã đăng nhập thay vì FirebaseUser
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);

        if (account != null) {
            // Ưu tiên 1: Tên hiển thị từ tài khoản Google
            String displayName = account.getDisplayName();
            if (displayName != null && !displayName.isEmpty()) {
                return displayName;
            }

            // Ưu tiên 2: Tiền tố email (phần trước @)
            String email = account.getEmail();
            if (email != null && email.contains("@")) {
                return email.split("@")[0];
            }
        }

        // Ưu tiên 3: Giá trị mặc định từ database hoặc mock data[cite: 5]
        return fallbackName;
    }
}