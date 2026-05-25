package com.utc2.appreborn.ui.assessment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.utc2.appreborn.data.local.AppDatabase;
import com.utc2.appreborn.data.local.dao.AdvisorDao;
import com.utc2.appreborn.data.local.dao.UserDao;
import com.utc2.appreborn.data.local.entity.AdvisorEntity;
import com.utc2.appreborn.data.local.entity.StudentProfileEntity;
import com.utc2.appreborn.data.repository.AssessmentRepository;
import com.utc2.appreborn.model.AssessmentCriteria;
import com.utc2.appreborn.model.AssessmentPeriod;
import com.utc2.appreborn.network.dto.assessment.ExternalAssessmentResponse;
import com.utc2.appreborn.network.dto.assessment.StudentAssessmentResponse;
import com.utc2.appreborn.utils.SessionManager;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AssessmentViewModel extends AndroidViewModel {

    public interface SaveCallback { void onResult(boolean success); }

    // ─── Deps ─────────────────────────────────────────────────────────────────
    private final AssessmentRepository repository;
    private final UserDao              userDao;
    private final AdvisorDao           advisorDao;
    private final ExecutorService      executor = Executors.newSingleThreadExecutor();

    // ─── State ────────────────────────────────────────────────────────────────
    private final MutableLiveData<Boolean>  isStudentTab   = new MutableLiveData<>(true);
    private final MutableLiveData<Float>    totalScore     = new MutableLiveData<>(0f);
    private final MutableLiveData<String>   classification = new MutableLiveData<>("");
    private final MediatorLiveData<List<AssessmentCriteria>> criteria = new MediatorLiveData<>();
    private final LiveData<List<AssessmentPeriod>> periods;

    // Header RLSV
    private final MutableLiveData<String> studentName  = new MutableLiveData<>("");
    private final MutableLiveData<String> studentCode  = new MutableLiveData<>("");
    private final MutableLiveData<String> studentClass = new MutableLiveData<>("");

    // Header CVHT
    private final MutableLiveData<String> advisorName = new MutableLiveData<>("");

    private final MutableLiveData<AssessmentPeriod> selectedPeriod = new MutableLiveData<>();
    private LiveData<List<AssessmentCriteria>> currentSource;

    // ─── Constructor ──────────────────────────────────────────────────────────

    public AssessmentViewModel(@NonNull Application application) {
        super(application);
        repository = AssessmentRepository.getInstance(application);
        userDao    = AppDatabase.getInstance(application).userDao();
        advisorDao = AppDatabase.getInstance(application).advisorDao();
        periods    = repository.getAssessmentPeriods(); // gọi API thật

        loadStudentInfo();
        switchTab(true);
    }

    // ─── Public LiveData ──────────────────────────────────────────────────────

    public LiveData<List<AssessmentCriteria>> getCriteria()       { return criteria; }
    public LiveData<List<AssessmentPeriod>>   getPeriods()        { return periods; }
    public LiveData<Float>                    getTotalScore()     { return totalScore; }
    public LiveData<String>                   getClassification() { return classification; }
    public LiveData<Boolean>                  getIsStudentTab()   { return isStudentTab; }

    public LiveData<String> getStudentName()  { return studentName; }
    public LiveData<String> getStudentCode()  { return studentCode; }
    public LiveData<String> getStudentClass() { return studentClass; }
    public LiveData<String> getAdvisorName()  { return advisorName; }

    // ─── Tab switch ───────────────────────────────────────────────────────────

    public void switchTab(boolean toStudentTab) {
        isStudentTab.setValue(toStudentTab);
        if (currentSource != null) criteria.removeSource(currentSource);
        currentSource = repository.getAssessmentCriteria(toStudentTab);
        criteria.addSource(currentSource, list -> {
            criteria.setValue(list);
            recalculate(list);
        });
    }

    // ─── Period ───────────────────────────────────────────────────────────────

    public void setSelectedPeriod(AssessmentPeriod period) {
        selectedPeriod.setValue(period);
    }

    // ─── Score ────────────────────────────────────────────────────────────────

    public void onScoreChanged(List<AssessmentCriteria> list) {
        recalculate(list);
    }

    // ─── Evidence ────────────────────────────────────────────────────────────

    public void addEvidenceUri(int criteriaId, String uri) {
        List<AssessmentCriteria> list = criteria.getValue();
        if (list == null) return;
        for (AssessmentCriteria c : list) {
            if (c.getId() == criteriaId) { c.addEvidenceUri(uri); break; }
        }
        criteria.setValue(list);
    }

    public void removeEvidenceUri(int criteriaId, int fileIndex) {
        List<AssessmentCriteria> list = criteria.getValue();
        if (list == null) return;
        for (AssessmentCriteria c : list) {
            if (c.getId() == criteriaId) { c.removeEvidenceAt(fileIndex); break; }
        }
        criteria.setValue(list);
    }

    // ─── Load dữ liệu khi chọn kỳ — gọi API thật ────────────────────────────

    /**
     * Gọi khi user bấm nút "Chọn" kỳ đánh giá.
     * Luồng: build criteria local → overlay điểm đã lưu (SV tab) → overlay điểm external → post lên UI.
     */
    public void loadForPeriod(AssessmentPeriod period, boolean toStudentTab) {
        selectedPeriod.setValue(period);
        isStudentTab.setValue(toStudentTab);
        if (currentSource != null) { criteria.removeSource(currentSource); currentSource = null; }

        List<AssessmentCriteria> list = repository.getCriteriaList(toStudentTab);

        if (!toStudentTab || period == null) {
            // Tab CVHT hoặc chưa có kỳ: hiển thị criteria local ngay
            criteria.setValue(list);
            recalculate(list);
            return;
        }

        // Tab RLSV: load điểm đã lưu → rồi load điểm external
        String periodId = period.getId();
        repository.getStudentAssessment(periodId,
                /* onLoaded (luôn gọi) */ loadSuccess ->
                        repository.getExternalAssessment(periodId,
                                /* onLoaded (luôn gọi) */ extSuccess -> {
                                    criteria.setValue(list);
                                    recalculate(list);
                                },
                                /* onData */ extData -> applyExternalData(list, extData)
                        ),
                /* onData */ svData -> applyStudentData(list, svData)
        );
    }

    // ─── Save RLSV — gọi API thật ────────────────────────────────────────────

    public void saveAssessment(List<AssessmentCriteria> criteriaList, SaveCallback callback) {
        AssessmentPeriod period = selectedPeriod.getValue();
        String periodId = period != null ? period.getId() : "";
        repository.saveStudentAssessment(criteriaList, periodId,
                success -> callback.onResult(success));
    }

    // ─── Submit CVHT — gọi API thật ──────────────────────────────────────────

    public void submitCvht(List<AssessmentCriteria> criteriaList,
                           String opinion,
                           SaveCallback callback) {
        AssessmentPeriod period = selectedPeriod.getValue();
        String periodId = period != null ? period.getId() : "";
        repository.saveAdvisorAssessment(criteriaList, periodId, opinion,
                success -> callback.onResult(success));
    }

    // ─── Private ─────────────────────────────────────────────────────────────

    private void recalculate(List<AssessmentCriteria> list) {
        if (list == null) return;
        float sum = 0f;
        for (AssessmentCriteria c : list) {
            int t = c.getViewType();
            if (t == AssessmentCriteria.TYPE_CRITERIA || t == AssessmentCriteria.TYPE_DEDUCTION)
                sum += c.getCurrentScore();
        }
        totalScore.setValue(sum);
        classification.setValue(classify(sum, Boolean.TRUE.equals(isStudentTab.getValue())));
    }

    private String classify(float score, boolean isRlsv) {
        if (isRlsv) {
            if (score >= 90) return "Xuất sắc";
            if (score >= 80) return "Tốt";
            if (score >= 65) return "Khá";
            if (score >= 50) return "Trung bình";
            if (score >= 35) return "Yếu";
            return "Kém";
        } else {
            float pct = (score / 60f) * 100f;
            if (pct >= 90) return "Xuất sắc";
            if (pct >= 80) return "Tốt";
            if (pct >= 65) return "Khá";
            if (pct >= 50) return "Trung bình";
            return "Yếu";
        }
    }

    private void applyStudentData(List<AssessmentCriteria> list, StudentAssessmentResponse data) {
        if (data == null || data.items == null) return;
        for (StudentAssessmentResponse.Item item : data.items) {
            for (AssessmentCriteria c : list) {
                if (c.getId() == item.criteriaId) {
                    c.setCurrentScore(item.score);
                    break;
                }
            }
        }
    }

    private void applyExternalData(List<AssessmentCriteria> list, ExternalAssessmentResponse data) {
        if (data == null || data.items == null) return;
        for (ExternalAssessmentResponse.Item item : data.items) {
            for (AssessmentCriteria c : list) {
                if (c.getId() == item.criteriaId) {
                    c.setTapTheScore(item.tapTheScore);
                    c.setKhoaScore(item.khoaScore);
                    c.setTruongScore(item.truongScore);
                    break;
                }
            }
        }
    }

    private void loadStudentInfo() {
        SessionManager session = SessionManager.getInstance(getApplication());

        // Hiển thị từ cache SessionManager ngay (đã được HomeViewModel fetch)
        String cachedName = session.getFullName();
        String cachedCode = session.getStudentCode();
        studentName.setValue(!cachedName.isEmpty() ? cachedName : "Sinh viên");
        studentCode.setValue(!cachedCode.isEmpty() ? cachedCode : "—");
        studentClass.setValue("—");
        advisorName.setValue("—");

        long userId = session.getUserId();
        if (userId <= 0) return; // chưa có userId thật, dùng cache là đủ

        executor.execute(() -> {
            try {
                StudentProfileEntity profile = userDao.getStudentProfileByUserId(userId);
                if (profile != null) {
                    if (profile.fullName != null)    studentName.postValue(profile.fullName);
                    if (profile.studentCode != null) studentCode.postValue(profile.studentCode);
                    studentClass.postValue(profile.className != null ? profile.className : "—");

                    if (profile.advisorId != null) {
                        AdvisorEntity advisor = advisorDao.getAdvisorById(profile.advisorId);
                        if (advisor != null) { advisorName.postValue(advisor.fullName); return; }
                    }
                }
            } catch (Exception ignored) {}
            advisorName.postValue("—");
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}