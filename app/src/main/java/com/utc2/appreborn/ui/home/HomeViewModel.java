package com.utc2.appreborn.ui.home;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.utc2.appreborn.data.local.StudentProfile;
import com.utc2.appreborn.data.repository.NewsRepository;
import com.utc2.appreborn.data.repository.StudentRepository;
import com.utc2.appreborn.network.ApiClient;
import com.utc2.appreborn.network.ApiResponse;
import com.utc2.appreborn.network.ProfileApiService;
import com.utc2.appreborn.network.dto.ProfileResponse;
import com.utc2.appreborn.ui.home.model.FeatureItem;
import com.utc2.appreborn.ui.home.model.NewsItem;
import com.utc2.appreborn.utils.MockHelper;
import com.utc2.appreborn.utils.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * HomeViewModel
 * ──────────────────────────────────────────────────────────────
 * Fetch profile từ /api/v1/profile/me MỘT LẦN khi khởi động,
 * cache vào SessionManager, update StudentRepository LiveData.
 * Các màn hình khác (ProfileFragment) đọc từ cache SessionManager.
 */
public class HomeViewModel extends AndroidViewModel {

    private static final String TAG = "HomeViewModel";

    private final NewsRepository newsRepository;
    private final StudentRepository studentRepository;
    private final SessionManager sessionManager;

    private final LiveData<List<NewsItem>> newsLiveData;
    private final LiveData<Boolean> isLoadingLiveData;
    private final MediatorLiveData<StudentProfile> studentProfileLiveData =
            new MediatorLiveData<>();

    private final List<FeatureItem> featureList;

    public HomeViewModel(Application application) {
        super(application);

        newsRepository = NewsRepository.getInstance(application);
        studentRepository = StudentRepository.getInstance(application);
        sessionManager = SessionManager.getInstance(application);

        newsLiveData = newsRepository.getNewsLiveData();
        isLoadingLiveData = newsRepository.getIsLoadingLiveData();
        featureList = MockHelper.getFeatureList();

        studentProfileLiveData.addSource(
                studentRepository.getStudentProfile(),
                studentProfileLiveData::setValue);

        // Fetch profile thực từ server, cập nhật cache + LiveData
        fetchProfile();
    }

    // ── Public API ────────────────────────────────────────────

    public LiveData<List<NewsItem>> getNewsLiveData() {
        return newsLiveData;
    }

    public LiveData<Boolean> getIsLoadingLiveData() {
        return isLoadingLiveData;
    }

    public LiveData<StudentProfile> getStudentProfileLiveData() {
        return studentProfileLiveData;
    }

    public List<FeatureItem> getFeatureList() {
        return featureList;
    }

    public void loadNews() {
        newsRepository.fetchNewsIfNeeded();
    }

    public void refreshNews() {
        newsRepository.forceRefresh();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        newsRepository.cancelActiveCall();
    }

    // ── Private ───────────────────────────────────────────────

    /**
     * Gọi API /profile/me một lần.
     * Khi thành công: lưu cache vào SessionManager, rồi notify StudentRepository
     * để update LiveData (HomeFragment sẽ tự hiển thị tên đúng).
     */
    private void fetchProfile() {
        String token = sessionManager.getAuthToken();
        if (token == null || token.isEmpty()) return;

        ProfileApiService api = ApiClient.getInstance(token).create(ProfileApiService.class);
        api.getMyProfile().enqueue(new Callback<ApiResponse<ProfileResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ProfileResponse>> call,
                                   Response<ApiResponse<ProfileResponse>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess()) {
                    ProfileResponse p = response.body().getData();
                    // Cache vào SessionManager — ProfileFragment, QrFragment sẽ đọc từ đây
                    sessionManager.saveProfile(p.fullName, p.studentId, p.className);
                    // Notify LiveData để HomeFragment cập nhật tên ngay
                    studentRepository.updateFromCache();
                } else {
                    Log.w(TAG, "fetchProfile: response không thành công");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ProfileResponse>> call, Throwable t) {
                Log.w(TAG, "fetchProfile: lỗi mạng — " + t.getMessage());
                // Giữ nguyên giá trị cache cũ trong SessionManager
            }
        });
    }
}