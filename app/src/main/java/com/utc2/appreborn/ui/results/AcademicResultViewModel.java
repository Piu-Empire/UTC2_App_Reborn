package com.utc2.appreborn.ui.results;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.utc2.appreborn.data.repository.AcademicResultRepository;
import com.utc2.appreborn.model.AcademicWarning;
import com.utc2.appreborn.model.CourseGrade;
import com.utc2.appreborn.model.LeaderboardEntry;
import com.utc2.appreborn.model.Scholarship;
import com.utc2.appreborn.network.dto.SemesterResponse;
import com.utc2.appreborn.utils.SessionManager;

import java.util.List;

public class AcademicResultViewModel extends AndroidViewModel {

    private final AcademicResultRepository repository;

    // Cache semesters — chỉ gọi API 1 lần suốt vòng đời ViewModel
    private LiveData<List<SemesterResponse>> semestersLiveData;

    public AcademicResultViewModel(@NonNull Application application) {
        super(application);
        String token = SessionManager.getInstance(application).getAuthToken();
        String className = SessionManager.getInstance(application).getClassName();
        repository = new AcademicResultRepository(token, className);
    }

    /** Trả về cùng 1 LiveData instance — tránh gọi API nhiều lần */
    public LiveData<List<SemesterResponse>> getSemesters() {
        if (semestersLiveData == null) {
            semestersLiveData = repository.getSemesters();
        }
        return semestersLiveData;
    }

    public LiveData<List<CourseGrade>> getGrades(Long semesterId) {
        return repository.getGrades(semesterId);
    }

    public LiveData<List<LeaderboardEntry>> getLeaderboard(Long semesterId, String academicYear) {
        return repository.getLeaderboard(semesterId, academicYear);
    }

    public LiveData<List<Scholarship>> getScholarships() {
        return repository.getScholarships();
    }

    public LiveData<List<AcademicWarning>> getWarnings(Long semesterId) {
        return repository.getWarnings(semesterId);
    }
}