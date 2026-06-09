package com.utc2.appreborn.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.utc2.appreborn.model.AcademicWarning;
import com.utc2.appreborn.model.CourseGrade;
import com.utc2.appreborn.model.LeaderboardEntry;
import com.utc2.appreborn.model.Scholarship;
import com.utc2.appreborn.network.AcademicApiService;
import com.utc2.appreborn.network.ApiClient;
import com.utc2.appreborn.network.ApiResponse;
import com.utc2.appreborn.network.dto.AcademicWarningResponse;
import com.utc2.appreborn.network.dto.CourseGradeResponse;
import com.utc2.appreborn.network.dto.LeaderboardEntryResponse;
import com.utc2.appreborn.network.dto.ScholarshipResponse;
import com.utc2.appreborn.network.dto.SemesterResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * AcademicResultRepository — kết nối 5 màn hình kết quả học tập với backend API.
 * Token được truyền từ ViewModel (lấy qua SessionManager).
 */
public class AcademicResultRepository {

    private final AcademicApiService api;

    private final String className;

    public AcademicResultRepository(String token, String className) {
        api = ApiClient.getInstance(token).create(AcademicApiService.class);
        this.className = className;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  SEMESTERS
    // ════════════════════════════════════════════════════════════════════════

    public LiveData<List<SemesterResponse>> getSemesters() {
        MutableLiveData<List<SemesterResponse>> liveData = new MutableLiveData<>();
        api.getSemesters().enqueue(new Callback<ApiResponse<List<SemesterResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<SemesterResponse>>> call,
                                   Response<ApiResponse<List<SemesterResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    liveData.postValue(response.body().getData());
                } else {
                    liveData.postValue(new ArrayList<>());
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<List<SemesterResponse>>> call, Throwable t) {
                liveData.postValue(new ArrayList<>());
            }
        });
        return liveData;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  GRADES
    // ════════════════════════════════════════════════════════════════════════

    public LiveData<List<CourseGrade>> getGrades(Long semesterId) {
        MutableLiveData<List<CourseGrade>> liveData = new MutableLiveData<>();
        api.getGrades(semesterId).enqueue(new Callback<ApiResponse<List<CourseGradeResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<CourseGradeResponse>>> call,
                                   Response<ApiResponse<List<CourseGradeResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    liveData.postValue(mapGrades(response.body().getData()));
                } else {
                    liveData.postValue(new ArrayList<>());
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<List<CourseGradeResponse>>> call, Throwable t) {
                liveData.postValue(new ArrayList<>());
            }
        });
        return liveData;
    }

    private List<CourseGrade> mapGrades(List<CourseGradeResponse> raw) {
        List<CourseGrade> result = new ArrayList<>();
        if (raw == null) return result;
        for (CourseGradeResponse r : raw) {
            String semLabel = buildSemLabel(r.semesterName, r.academicYear);
            result.add(new CourseGrade(
                    r.courseCode   != null ? r.courseCode   : "",
                    r.courseName   != null ? r.courseName   : "",
                    r.credits      != null ? r.credits      : 0,
                    r.midtermScore != null ? r.midtermScore : 0.0,
                    r.finalScore   != null ? r.finalScore   : 0.0,
                    r.gradePoint   != null ? r.gradePoint   : 0.0,
                    r.letterGrade  != null ? r.letterGrade  : "—",
                    Boolean.TRUE.equals(r.isPassed),
                    semLabel
            ));
        }
        return result;
    }

    /** "Học kỳ 1" + "2024-2025" → "HK1 2024-2025" */
    private String buildSemLabel(String semesterName, String academicYear) {
        if (semesterName == null || academicYear == null) return "";
        String hk = semesterName.replace("Học kỳ ", "HK");
        return hk + " " + academicYear;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  LEADERBOARD
    // ════════════════════════════════════════════════════════════════════════

    public LiveData<List<LeaderboardEntry>> getLeaderboard(Long semesterId, String academicYear) {
        MutableLiveData<List<LeaderboardEntry>> liveData = new MutableLiveData<>();
        api.getLeaderboard(semesterId, academicYear, className)
                .enqueue(new Callback<ApiResponse<List<LeaderboardEntryResponse>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<LeaderboardEntryResponse>>> call,
                                           Response<ApiResponse<List<LeaderboardEntryResponse>>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            liveData.postValue(mapLeaderboard(response.body().getData()));
                        } else {
                            liveData.postValue(new ArrayList<>());
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<List<LeaderboardEntryResponse>>> call, Throwable t) {
                        liveData.postValue(new ArrayList<>());
                    }
                });
        return liveData;
    }

    private List<LeaderboardEntry> mapLeaderboard(List<LeaderboardEntryResponse> raw) {
        List<LeaderboardEntry> result = new ArrayList<>();
        if (raw == null) return result;
        for (LeaderboardEntryResponse r : raw) {
            result.add(new LeaderboardEntry(
                    r.rank         != null ? r.rank         : 0,
                    r.fullName     != null ? r.fullName     : "",
                    r.initials     != null ? r.initials     : "??",
                    r.totalCredits != null ? r.totalCredits : 0,
                    r.gpa          != null ? r.gpa          : 0.0,
                    Boolean.TRUE.equals(r.isCurrentUser)
            ));
        }
        return result;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  SCHOLARSHIPS
    // ════════════════════════════════════════════════════════════════════════

    public LiveData<List<Scholarship>> getScholarships() {
        MutableLiveData<List<Scholarship>> liveData = new MutableLiveData<>();
        api.getScholarships().enqueue(new Callback<ApiResponse<List<ScholarshipResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ScholarshipResponse>>> call,
                                   Response<ApiResponse<List<ScholarshipResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    liveData.postValue(mapScholarships(response.body().getData()));
                } else {
                    liveData.postValue(new ArrayList<>());
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<List<ScholarshipResponse>>> call, Throwable t) {
                liveData.postValue(new ArrayList<>());
            }
        });
        return liveData;
    }

    private List<Scholarship> mapScholarships(List<ScholarshipResponse> raw) {
        List<Scholarship> result = new ArrayList<>();
        if (raw == null) return result;
        for (ScholarshipResponse r : raw) {
            String status = "received".equals(r.status)
                    ? Scholarship.STATUS_RECEIVED
                    : Scholarship.STATUS_NOT_RECEIVED;
            result.add(new Scholarship(
                    r.name         != null ? r.name         : "",
                    r.organization != null ? r.organization : "",
                    r.amount       != null ? r.amount       : 0L,
                    r.unit         != null ? r.unit         : "",
                    status,
                    r.minGpa       != null ? r.minGpa       : 0.0
            ));
        }
        return result;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  WARNINGS
    // ════════════════════════════════════════════════════════════════════════

    public LiveData<List<AcademicWarning>> getWarnings(Long semesterId) {
        MutableLiveData<List<AcademicWarning>> liveData = new MutableLiveData<>();
        api.getWarnings(semesterId).enqueue(new Callback<ApiResponse<List<AcademicWarningResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<AcademicWarningResponse>>> call,
                                   Response<ApiResponse<List<AcademicWarningResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    liveData.postValue(mapWarnings(response.body().getData()));
                } else {
                    liveData.postValue(new ArrayList<>());
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<List<AcademicWarningResponse>>> call, Throwable t) {
                liveData.postValue(new ArrayList<>());
            }
        });
        return liveData;
    }

    private List<AcademicWarning> mapWarnings(List<AcademicWarningResponse> raw) {
        List<AcademicWarning> result = new ArrayList<>();
        if (raw == null) return result;
        for (AcademicWarningResponse r : raw) {
            // Chỉ hiển thị warning đã duyệt (approved=true)
            if (r.approved == null || !r.approved) continue;
            String status = r.status != null ? r.status : AcademicWarning.STATUS_ACTIVE;
            int icon = "FAILED_EXAM".equals(r.warningType) || "LOW_GPA".equals(r.warningType)
                    ? AcademicWarning.ICON_BOOK
                    : AcademicWarning.ICON_CLOCK;
            result.add(new AcademicWarning(
                    r.warningId != null ? r.warningId.intValue() : 0,
                    mapWarningTitle(r.warningType),
                    r.description,
                    formatDate(r.issuedAt),
                    status,
                    icon
            ));
        }
        return result;
    }

    private String mapWarningTitle(String warningType) {
        if (warningType == null) return "Cảnh báo học vụ";
        switch (warningType) {
            case "FAILED_EXAM": return "Điểm thi không đạt";
            case "LOW_GPA":     return "GPA thấp";
            case "ATTENDANCE":  return "Vắng mặt quá mức";
            default:            return "Cảnh báo học vụ";
        }
    }

    /** "2026-01-15T00:00:00" → "15/01/2026" */
    private String formatDate(String issuedAt) {
        if (issuedAt == null || issuedAt.length() < 10) return "";
        try {
            String[] parts = issuedAt.substring(0, 10).split("-");
            return parts[2] + "/" + parts[1] + "/" + parts[0];
        } catch (Exception e) {
            return issuedAt;
        }
    }
}