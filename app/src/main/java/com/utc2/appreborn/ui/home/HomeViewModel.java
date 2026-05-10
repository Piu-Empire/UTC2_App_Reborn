package com.utc2.appreborn.ui.home;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.utc2.appreborn.data.local.StudentProfile;
import com.utc2.appreborn.data.repository.NewsRepository;
import com.utc2.appreborn.data.repository.StudentRepository;
import com.utc2.appreborn.ui.home.model.FeatureItem;
import com.utc2.appreborn.ui.home.model.NewsItem;
import com.utc2.appreborn.utils.MockHelper;

import java.util.List;

/**
 * HomeViewModel
 * ──────────────────────────────────────────────────────────────
 * Đổi từ ViewModel → AndroidViewModel để có Application context
 * cho NewsRepository (cần context để khởi tạo SharedPreferences).
 *
 * Package: com.utc2.appreborn.ui.home
 */
public class HomeViewModel extends AndroidViewModel {

    private final NewsRepository    newsRepository;
    private final StudentRepository studentRepository;

    private final LiveData<List<NewsItem>>           newsLiveData;
    private final LiveData<Boolean>                  isLoadingLiveData;
    private final MediatorLiveData<StudentProfile>   studentProfileLiveData =
            new MediatorLiveData<>();

    private final List<FeatureItem> featureList;

    public HomeViewModel(Application application) {
        super(application);

        // Khởi tạo với context — bắt buộc cho SharedPreferences cache
        newsRepository    = NewsRepository.getInstance(application);
        studentRepository = StudentRepository.getInstance(application);

        newsLiveData      = newsRepository.getNewsLiveData();
        isLoadingLiveData = newsRepository.getIsLoadingLiveData();
        featureList       = MockHelper.getFeatureList();

        studentProfileLiveData.addSource(
                studentRepository.getStudentProfile(),
                studentProfileLiveData::setValue);
    }

    // ── Public API ────────────────────────────────────────────

    public LiveData<List<NewsItem>>  getNewsLiveData()           { return newsLiveData;           }
    public LiveData<Boolean>         getIsLoadingLiveData()      { return isLoadingLiveData;      }
    public LiveData<StudentProfile>  getStudentProfileLiveData() { return studentProfileLiveData; }
    public List<FeatureItem>         getFeatureList()            { return featureList;            }

    /**
     * Gọi khi Fragment khởi động.
     * Chỉ gọi API nếu cache đã hết hạn (> 24h).
     */
    public void loadNews() {
        newsRepository.fetchNewsIfNeeded();
    }

    /**
     * Gọi khi người dùng kéo để làm mới (pull-to-refresh).
     * Bỏ qua cache, gọi API ngay lập tức.
     */
    public void refreshNews() {
        newsRepository.forceRefresh();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        newsRepository.cancelActiveCall();
    }
}
